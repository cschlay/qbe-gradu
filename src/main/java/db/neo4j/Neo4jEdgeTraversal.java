package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

public class Neo4jEdgeTraversal {
    private final ResultGraph resultGraph;

    public Neo4jEdgeTraversal(ResultGraph graph) {
        resultGraph = graph;
    }

    public QbeNode query(Node neo4jNode, QbeNode queryNode, QbeNode resultNode)
            throws InvalidNodeException {

        for (QbeEdge queryEdge : queryNode.edges.values()) {
            updateNodeRelations(neo4jNode, resultNode, queryEdge);
        }

        return resultNode;
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
            } catch (InvalidNodeException exception) {

                exception.printStackTrace();
                // Edge is not valid and should be discarded.
            }

            if (!edgeFound) {
                // At least one edge must exist, otherwise node is not valid.
                throw new InvalidNodeException();
            }
        }
    }

    private QbeEdge createEdgeWithHead(Relationship neo4jEdge, QbeEdge queryEdge) {
        String tailId = String.valueOf(neo4jEdge.getStartNodeId());
        String edgeId = String.valueOf(neo4jEdge.getId());

        @Nullable QbeEdge edge = resultGraph.getEdge(tailId, edgeId);
        if (edge != null) {
            return edge;
        }

        return new QbeEdge(neo4jEdge.getId(), queryEdge.name);
    }

    private QbeEdge createEdgeWithTail(Relationship neo4jEdge, QbeEdge queryEdge) {
        String headId = String.valueOf(neo4jEdge.getEndNodeId());
        String edgeId = String.valueOf(neo4jEdge.getId());

        @Nullable QbeEdge edge = resultGraph.getEdge(headId, edgeId);
        if (edge != null) {
            return edge;
        }

        return new QbeEdge(neo4jEdge.getId(), queryEdge.name);
    }
}
