package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryGraphTraversal {
    private final Transaction tx;
    private final HashMap<Long, QbeNode> pendingNodes;


    public QueryGraphTraversal(Transaction transaction) {
        tx = transaction;
        pendingNodes = new HashMap<>();
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        var resultGraph = new ResultGraph();

        for (QbeNode queryNode : queryGraph.values()) {
            ResourceIterator<Node> nodes = tx.findNodes(Label.label(queryNode.name));
            nodes.forEachRemaining(node -> {
                try {
                    var resultNode = traverseNode(node, queryNode);
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
                try {
                    var resultEdge = new QbeEdge(edge.getId(), queryEdge.name);
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

                    resultNode.edges.put(resultEdge.id, resultEdge);
                    found.set(true);
                } catch (InvalidNodeException | IdConstraintException expected) {
                    // Either tail or head node is invalid so the edge is discarded.
                }
            });

            if (!found.get()) {
                pendingNodes.remove(resultNode.longId());
                throw new InvalidNodeException("Node %s doesn't have any relations", node.getId());
            }
        }

        pendingNodes.remove(resultNode.longId());
        return resultNode;
    }
}
