package db.neo4j;

import enums.QueryType;
import exceptions.IdConstraintException;
import exceptions.InvalidNodeException;
import graphs.*;
import utilities.Numbers;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import java.util.HashMap;

public class Neo4jQueryGraphTraversal {
    private final Transaction tx;
    private final HashMap<Long, QbeNode> pendingNodes;
    private ResultGraph currentResultGraph;

    public Neo4jQueryGraphTraversal(Transaction transaction) {
        tx = transaction;
        pendingNodes = new HashMap<>();
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        currentResultGraph = new ResultGraph();

        for (QbeNode queryNode : queryGraph.values()) {
            ResourceIterator<Node> nodes = tx.findNodes(Label.label(queryNode.name));

            while (nodes.hasNext()) {
                Node node = nodes.next();
                try {
                    @Nullable QbeNode resultNode = traverseNode(node, queryNode, new QbePath());
                    if (resultNode != null) {
                        resultNode.selected = true;
                        currentResultGraph.put(resultNode);
                    }
                } catch (InvalidNodeException | IdConstraintException expected) {
                    // Node has invalid properties or edges, should be ignored.
                }
            }
        }

        return currentResultGraph;
    }

    private @Nullable QbeNode traverseNode(Node node, QbeNode queryNode, QbePath path) throws InvalidNodeException, IdConstraintException {
        if (currentResultGraph.containsKey(String.valueOf(node.getId()))) {
            return currentResultGraph.get(String.valueOf(node.getId()));
        }

        if (pendingNodes.containsKey(node.getId())) {
            return pendingNodes.get(node.getId());
        }

        var resultNode = new QbeNode(node.getId(), queryNode.name);
        resultNode.properties.putAll(new Neo4jPropertyTraversal(queryNode).getProperties(node));
        pendingNodes.put(node.getId(), resultNode);
        path.add(resultNode);

        for (var queryEdge : queryNode.edges.values()) {
            boolean found = false;
            Iterable<Relationship> edges = node.getRelationships(RelationshipType.withName(queryEdge.name));

            for (Relationship edge : edges) {
                try {
                    @Nullable QbeEdge resultEdge = traverseEdge(edge, queryEdge, resultNode, path.copy());
                    if (resultEdge != null) {
                        resultNode.edges.put(resultEdge.id, resultEdge);
                    }
                    found = true;
                } catch (InvalidNodeException | IdConstraintException expected) {
                    // Either tail or head node is invalid so the edge is discarded.
                }
            }

            if (!found) {
                pendingNodes.remove(resultNode.longId());
                throw new InvalidNodeException("Node \"%s\" doesn't have any edges named \"%s\"", node.getLabels(), queryEdge.name);
            }
        }

        pendingNodes.remove(resultNode.longId());

        if (queryNode.type == QueryType.COUNT) {
            mutableAggregateCount(queryNode, resultNode, path);
            return null;
        }

        if (queryNode.type == QueryType.SUM) {
            mutableAggregateSum(queryNode, resultNode, path);
            return null;
        }

        return resultNode;
    }

    private @Nullable QbeEdge traverseEdge(Relationship edge, QbeEdge queryEdge, QbeNode resultNode, QbePath path) throws InvalidNodeException, IdConstraintException {
        var resultEdge = new QbeEdge(edge.getId(), queryEdge.name);
        resultEdge.properties.putAll(new Neo4jPropertyTraversal(queryEdge).getProperties(edge));
        path.add(queryEdge, resultEdge);

        // The Neo4j always returns the edges (tail) -> (head)
        if (queryEdge.tailNode != null) {
            if (queryEdge.tailNode.name.equals(resultNode.name)) {
                resultEdge.tailNode = resultNode;
            } else if (edge.getStartNode().hasLabel(Label.label(queryEdge.tailNode.name))) {
                resultEdge.tailNode = traverseNode(edge.getStartNode(), queryEdge.tailNode, path.copy());
            } else {
                throw new InvalidNodeException("The edge \"%s\" has invalid tail node \"%s\".", queryEdge.name, queryEdge.tailNode.name);
            }
        }

        if (queryEdge.headNode != null) {
            if (queryEdge.headNode.name.equals(resultNode.name)) {
                resultEdge.headNode = resultNode;
            } else if (edge.getEndNode().hasLabel(Label.label(queryEdge.headNode.name))) {
                resultEdge.headNode = traverseNode(edge.getEndNode(), queryEdge.headNode, path.copy());
            } else {
                throw new InvalidNodeException("The edge \"%s\" has invalid head node \"%s\".", queryEdge.name, queryEdge.headNode.name);
            }
        }

        if (path.isValid() && queryEdge.type == QueryType.COUNT) {
            mutableAggregateCount(queryEdge, resultEdge, path);
            return null;
        } else if (queryEdge.type == QueryType.SUM) {
            mutableAggregateSum(queryEdge, resultEdge, path);
            return null;
        } else {
            resultEdge.selected = queryEdge.selected;
        }

        return resultEdge;
    }

    private @Nullable GraphEntity findAggregationEntity(GraphEntity queryEntity, QbePath path) {
        if (queryEntity.aggregationGroup == null) {
            @Nullable GraphEntity aggregationEntity = currentResultGraph.get(queryEntity.name);
            if (aggregationEntity == null) {
                // Even if the query entity is edge, it will still be used as node
                var aggregationNode = new QbeNode(queryEntity.name);
                aggregationNode.selected = true;
                currentResultGraph.put(aggregationNode);
                return aggregationNode;
            }
            return aggregationEntity;
        }
        else {
            return path.find(queryEntity.aggregationGroup);
        }
    }

    private void mutableAggregateCount(GraphEntity queryEntity, GraphEntity resultEntity, QbePath path) {
        @Nullable GraphEntity aggregationEntity = findAggregationEntity(queryEntity, path);

        if (aggregationEntity != null && aggregationEntity.isNotAggregated(resultEntity)) {
            aggregationEntity.addAggregated(resultEntity);
            String propertyName = "_agg-count";
            @Nullable QbeData property = aggregationEntity.properties.get(propertyName);

            if (property != null && property.value instanceof Integer) {
                int counter = (int) property.value;
                property.value = counter + 1;
            } else {
                property = new QbeData(1);
                property.selected = true;
                aggregationEntity.properties.put(propertyName, property);
            }
        }
    }

    private void mutableAggregateSum(GraphEntity queryEntity, GraphEntity resultEntity, QbePath path) {
        @Nullable GraphEntity aggregationEntity = findAggregationEntity(queryEntity, path);

        if (aggregationEntity != null && aggregationEntity.isNotAggregated(resultEntity)) {
            aggregationEntity.addAggregated(resultEntity);
            @Nullable QbeData property = aggregationEntity.properties.get(queryEntity.aggregationProperty);

            if (property != null && property.value != null) {
                Object a = property.value;
                Object b = resultEntity.addProperty(queryEntity.aggregationProperty);
                if (b != null) {
                    property.value = Numbers.plus(a, b);
                }
            } else {
                Object initialCount = resultEntity.addProperty(queryEntity.aggregationProperty);
                property = new QbeData(initialCount instanceof Number ? initialCount : 0);
                property.selected = true;
                aggregationEntity.properties.put(queryEntity.aggregationProperty, property);
            }
        }
    }
}
