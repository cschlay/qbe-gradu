package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.*;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryGraphTraversal {
    private final Transaction tx;
    private final HashMap<Long, QbeNode> pendingNodes;
    private final HashMap<Long, GraphEntity> aggregatedEntities;


    public QueryGraphTraversal(Transaction transaction) {
        tx = transaction;
        pendingNodes = new HashMap<>();
        aggregatedEntities = new HashMap<>();
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        var resultGraph = new ResultGraph();

        for (QbeNode queryNode : queryGraph.values()) {
            ResourceIterator<Node> nodes = tx.findNodes(Label.label(queryNode.name));

            while (nodes.hasNext()) {
                Node node = nodes.next();
                try {
                    QbeNode resultNode = traverseNode(node, queryNode, new QbePath());
                    resultGraph.put(resultNode);
                } catch (InvalidNodeException | IdConstraintException expected) {
                    // Node has invalid properties or edges, should be ignored.
                }
            }
        }

        return resultGraph;
    }

    // TODO: Keep track of path and insert all non-transitive paths
    // TODO: Also check if node already exists in result graph

    private QbeNode traverseNode(Node node, QbeNode queryNode, QbePath path) throws InvalidNodeException, IdConstraintException {
        var resultNode = new QbeNode(node.getId(), queryNode.name);
        resultNode.selected = queryNode.selected;

        if (queryNode.type == QueryType.COUNT) {
            System.out.println("DETECTED COUNT");
        }

        resultNode.properties.putAll(new Neo4jPropertyTraversal(queryNode).getProperties(node));
        pendingNodes.put(node.getId(), resultNode);
        path.add(resultNode);

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
                            resultEdge.tailNode = traverseNode(edge.getStartNode(), queryEdge.tailNode, path.copy());
                        }
                    }
                    if (queryEdge.headNode != null) {
                        if (queryEdge.headNode.name.equals(resultNode.name)) {
                            resultEdge.headNode = resultNode;
                        } else if (pendingNodes.containsKey(edge.getEndNodeId())) {
                            resultEdge.headNode = pendingNodes.get(edge.getEndNodeId());
                        } else {
                            resultEdge.headNode = traverseNode(edge.getEndNode(), queryEdge.headNode, path.copy());
                        }
                    }

                    if (queryEdge.type == QueryType.COUNT) {
                        @Nullable GraphEntity entity =  path.find(queryEdge.aggregationGroup);
                        if (entity != null && !aggregatedEntities.containsKey(resultEdge.longId())) {
                            aggregatedEntities.put(resultEdge.longId(), resultEdge);
                            @Nullable QbeData p = entity.properties.get(queryEdge.name + ".count");
                            if (p != null) {
                                p.value = ((int) p.value) + 1;
                            } else  {
                                var data = new QbeData(1);
                                data.selected = true;
                                entity.properties.put(queryEdge.name + ".count", data);
                            }
                        }
                        // Update the node in path
                    } else {
                        resultEdge.selected = queryEdge.selected;
                        resultNode.edges.put(resultEdge.id, resultEdge);
                    }
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
