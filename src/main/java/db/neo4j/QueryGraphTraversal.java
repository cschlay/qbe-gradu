package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.*;
import core.utilities.Numbers;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import java.util.HashMap;

public class QueryGraphTraversal {
    private final Transaction tx;
    private final HashMap<String, GraphEntity> aggregatedEntities;
    private final HashMap<Long, QbeNode> pendingNodes;
    private ResultGraph currentResultGraph;

    public QueryGraphTraversal(Transaction transaction) {
        tx = transaction;
        aggregatedEntities = new HashMap<>();
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
        var resultNode = new QbeNode(node.getId(), queryNode.name);
        resultNode.selected = queryNode.selected;
        resultNode.properties.putAll(new Neo4jPropertyTraversal(queryNode).getProperties(node));
        pendingNodes.put(node.getId(), resultNode);
        path.add(resultNode);

        for (var queryEdge : queryNode.edges.values()) {
            boolean found = false;
            Iterable<Relationship> edges = node.getRelationships(RelationshipType.withName(queryEdge.name));

            for (Relationship edge : edges) {
                try {
                    @Nullable QbeEdge resultEdge = traverseEdge(edge, queryEdge, resultNode, path);
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
                throw new InvalidNodeException("Node %s doesn't have any relations", node.getId());
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

        if (queryEdge.tailNode != null) {
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

    private void mutableAggregateCount(GraphEntity queryEntity, GraphEntity resultEntity, QbePath path) {
        @Nullable GraphEntity aggregationEntity;

        if (queryEntity.aggregationGroup == null) {
            aggregationEntity = currentResultGraph.get(queryEntity.name);
            if (aggregationEntity == null) {
                // Even if the query entity is edge, it will still be used as node
                aggregationEntity = new QbeNode(queryEntity.name);
                aggregationEntity.selected = true;
                currentResultGraph.put((QbeNode) aggregationEntity);
            }
        } else {
            aggregationEntity = path.find(queryEntity.aggregationGroup);
        }

        if (aggregationEntity != null && !aggregatedEntities.containsKey(resultEntity.id)) {
            aggregatedEntities.put(resultEntity.id, resultEntity);
            String propertyName = "_agg-count";
            @Nullable QbeData property = aggregationEntity.properties.get(propertyName);

            if (property != null && property.value != null) {
                int counter = (int) property.value;
                property.value = counter + 1;
            } else {
                property = new QbeData(1);
                property.selected = true;
                aggregationEntity.properties.put(propertyName, property);
            }
        }
    }

    // TODO: What if we use queue in GraphEntity to aggregate and not here to prevent duplicate queries?
    private void mutableAggregateSum(GraphEntity queryEntity, GraphEntity resultEntity, QbePath path) {
        @Nullable GraphEntity aggregationEntity;

        if (queryEntity.aggregationGroup == null) {
            aggregationEntity = currentResultGraph.get(queryEntity.name);
            if (aggregationEntity == null) {
                // Even if the query entity is edge, it will still be used as node
                aggregationEntity = new QbeNode(queryEntity.name);
                aggregationEntity.selected = true;
                currentResultGraph.put((QbeNode) aggregationEntity);
            }
        }
        else {
            aggregationEntity = path.find(queryEntity.aggregationGroup);
        }

        if (aggregationEntity != null && !aggregationEntity.aggregatedIds.contains(resultEntity.id)) {
            aggregationEntity.aggregatedIds.add(resultEntity.id);
            @Nullable QbeData property = aggregationEntity.properties.get(queryEntity.aggregationProperty);

            if (property != null && property.value != null) {
                Object a = property.value;
                Object b = resultEntity.property(queryEntity.aggregationProperty);
                if (b != null) {
                    property.value = Numbers.plus(a, b);
                }
            } else {
                Object initialCount = resultEntity.property(queryEntity.aggregationProperty);
                property = new QbeData(initialCount instanceof Number ? initialCount : 0);
                property.selected = true;
                aggregationEntity.properties.put(queryEntity.aggregationProperty, property);
            }
        }
    }
}
