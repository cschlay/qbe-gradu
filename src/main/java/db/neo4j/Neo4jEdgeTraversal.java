package db.neo4j;

import core.exceptions.*;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryType;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

public class Neo4jEdgeTraversal {
    private final ResultGraph resultGraph;

    public Neo4jEdgeTraversal(ResultGraph graph) {
        resultGraph = graph;
    }

    public QbeNode query(Transaction tx, Node neo4jNode, QbeNode queryNode, QbeNode resultNode)
            throws InvalidNodeException {

        for (QbeEdge queryEdge : queryNode.edges.values()) {
            if (QueryType.INSERT == queryEdge.type) {
                try {
                    QbeEdge edge = createEdge(tx, neo4jNode, resultNode, queryEdge);
                    resultGraph.put(edge);
                } catch (QueryException expected) {
                    // The error is thrown because, head node is used in creation.
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                updateNodeRelations(neo4jNode, resultNode, queryEdge);
            }
        }

        return resultNode;
    }

    private QbeEdge createEdge(Transaction tx, Node neo4jNode, QbeNode resultNode, QbeEdge queryEdge) throws QueryException  {
        boolean isTail = queryEdge.tailNode != null && queryEdge.tailNode.name.equals(resultNode.name);

        if (isTail && queryEdge.headNode != null && queryEdge.headNode.id != null) {
            Node head = Neo4j.findNode(tx, queryEdge.headNode);
            Relationship neo4jEdge = Neo4j.Edge.create(queryEdge.name, neo4jNode, head);

            var resultEdge = new QbeEdge(neo4jEdge.getId(), queryEdge.name);
            new Neo4jPropertyTraversal(queryEdge).mutableCopyProperties(neo4jEdge, resultEdge);
            resultEdge.tailNode = resultNode;
            resultEdge.headNode = resultGraph.computeIfAbsent(queryEdge.headNode.id, k -> new QbeNode(neo4jEdge.getEndNodeId(), queryEdge.headNode.name));

            return resultEdge;
        }

        // TODO: Group node creation is not supported.

        throw new QueryException("Cannot create relationship with head node '%s', create it with tail node '%s'.", queryEdge.headNode, queryEdge.tailNode);
    }

    private void updateNodeRelations(Node neo4jNode, QbeNode resultNode, QbeEdge queryEdge)
            throws InvalidNodeException {
        boolean edgeFound = false;

        RelationshipType type = RelationshipType.withName(queryEdge.name);

        Iterable<Relationship> relationships = neo4jNode.getRelationships(type);
        for (Relationship relationship : relationships) {
            // Check if query edge is valid
            try {
                var properties = new Neo4jPropertyTraversal(queryEdge).getProperties(relationship);
                @Nullable QbeEdge resultEdge = null;

                if (queryEdge.headNode != null && resultNode.name.equals(queryEdge.headNode.name)) {
                    // node is head of the edge
                    resultEdge = createEdgeWithHead(relationship, queryEdge);
                    resultEdge.headNode = resultNode;
                } else if (queryEdge.tailNode != null && resultNode.name.equals(queryEdge.tailNode.name)) {
                    // node is tail of the edge
                    resultEdge = createEdgeWithTail(relationship, queryEdge);
                    resultEdge.tailNode = resultNode;
                }

                if (resultEdge != null) {
                    edgeFound = true;
                    resultEdge.properties.putAll(properties);
                    resultNode.edges.put(resultEdge.id, resultEdge);
                }
            } catch (InvalidNodeException | IdConstraintException exception) {
                // Edge is not valid and should be discarded.
            }
        }

        if (!edgeFound) {
            // At least one edge must exist, otherwise node is not valid.
            throw new InvalidNodeException();
        }
    }

    private QbeEdge createEdgeWithHead(Relationship neo4jEdge, QbeEdge queryEdge) {
        String tailId = String.valueOf(neo4jEdge.getStartNodeId());
        String edgeId = String.valueOf(neo4jEdge.getId());

        try {
            return resultGraph.edge(tailId, edgeId);
        } catch (EntityNotFound expected) {
            return new QbeEdge(neo4jEdge.getId(), queryEdge.name);
        }
    }

    private QbeEdge createEdgeWithTail(Relationship neo4jEdge, QbeEdge queryEdge) {
        String headId = String.valueOf(neo4jEdge.getEndNodeId());
        String edgeId = String.valueOf(neo4jEdge.getId());

        try {
            return resultGraph.edge(headId, edgeId);
        } catch (EntityNotFound expected) {
            return new QbeEdge(neo4jEdge.getId(), queryEdge.name);
        }
    }
}
