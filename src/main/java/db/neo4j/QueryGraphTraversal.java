package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryGraphTraversal {
    private final Transaction tx;
    private final HashMap<Long, QbeEdge> validEdges;
    private final HashMap<Long, QbeNode> validNodes;
    private final ArrayList<Long> pendingEdges;
    private final HashMap<Long, QbeNode> pendingNodes;


    public QueryGraphTraversal(Transaction transaction) {
        tx = transaction;
        validEdges = new HashMap<>();
        validNodes = new HashMap<>();

        pendingEdges = new ArrayList<>();
        pendingNodes = new HashMap<>();
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        var resultGraph = new ResultGraph();

        for (QbeNode queryNode : queryGraph.values()) {
            ResourceIterator<Node> nodes = tx.findNodes(Label.label(queryNode.name));
            nodes.forEachRemaining(node -> {
                try {
                    @Nullable QbeNode resultNode = validNodes.get(node.getId());
                    if (resultNode == null) {
                        resultNode = traverseNode(node, queryNode);
                    }
                    resultGraph.put(resultNode);
                } catch (InvalidNodeException | IdConstraintException expected) {
                    // Node has invalid properties or edges, should be ignored.
                }
            });
        }

        return resultGraph;
    }

    private QbeNode traverseNode(Node node, QbeNode queryNode) throws InvalidNodeException, IdConstraintException {
        var resultNode = new QbeNode(node.getId(), queryNode.name);
        resultNode.properties.putAll(new Neo4jPropertyTraversal(queryNode).getProperties(node));
        pendingNodes.put(node.getId(), resultNode);

        for (var queryEdge : queryNode.edges.values()) {
            var edges = node.getRelationships(RelationshipType.withName(queryEdge.name));
            AtomicBoolean found = new AtomicBoolean(false);
            edges.forEach(edge -> {
                System.out.printf("%s: %s -%s- %s%n", queryNode.name, edge.getStartNodeId(), edge.getId(), edge.getEndNodeId());
                try {
                    @Nullable QbeEdge resultEdge = validEdges.get(edge.getId());
                    if (resultEdge == null) {
                        // Find edge
                        resultEdge = new QbeEdge(edge.getId(), queryEdge.name);
                        resultEdge.properties.putAll(new Neo4jPropertyTraversal(queryEdge).getProperties(edge));

                        if (queryEdge.tailNode != null ) {
                            if (queryEdge.tailNode.name.equals(resultNode.name)) {
                                resultEdge.tailNode = resultNode;
                            } else if (pendingNodes.containsKey(edge.getStartNodeId())) {
                                resultEdge.tailNode = pendingNodes.get(edge.getStartNodeId());
                            } else {
                                resultEdge.tailNode = traverseNode(edge.getStartNode(), queryEdge.tailNode);
                            }
                        }
                        if (queryEdge.headNode != null) {
                            if (queryEdge.headNode.name.equals(resultNode.name)) {
                                resultEdge.headNode = resultNode;
                            } else if (pendingNodes.containsKey(edge.getEndNodeId())) {
                                resultEdge.headNode = pendingNodes.get(edge.getEndNodeId());
                            } else {
                                resultEdge.headNode = traverseNode(edge.getEndNode(), queryEdge.headNode);
                            }
                        }
                    }

                    resultNode.edges.put(resultEdge.id, resultEdge);
                    validEdges.put(resultEdge.longId(), resultEdge);
                    found.set(true);
                } catch (InvalidNodeException | IdConstraintException expected) {
                    //System.out.println("ERR");
                    //expected.printStackTrace();
                    // Expected
                }
            });

            if (!found.get()) {
                pendingNodes.remove(resultNode.longId());
                throw new InvalidNodeException("Node %s doesn't have any relations", node.getId());
            }
        }

        validNodes.put(resultNode.longId(), resultNode);
        pendingNodes.remove(resultNode.longId());
        return resultNode;
    }
}
