package core.db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import java.util.ArrayList;

public class Neo4jEdgeValidator {
    public static ArrayList<QbeEdge> validateRelationships(
            Transaction transaction,
            QbeEdge queryEdge,
            QbeNode resultNode,
            ResultGraph graph
    ) throws InvalidNodeException {
        assert queryEdge.tailNode != null && queryEdge.headNode != null;
        ArrayList<QbeEdge> edges = new ArrayList<>();
        Iterable<Relationship> relationships = getRelationships(transaction, queryEdge, resultNode);

        for (Relationship relationship : relationships) {
            QbeEdge resultEdge = visitNeo4jEdge(relationship, queryEdge, graph);
            if (hasPath(resultEdge, relationship)) {
                edges.add(resultEdge);
            }
        }

        if (edges.isEmpty()) {
            throw new InvalidNodeException();
        }

        return edges;
    }

    // Recursive solution is possible
    private static boolean hasPath(QbeEdge edge, Relationship relationship) {
        String startNodeId = String.valueOf(relationship.getStartNodeId());
        String endNodeId = String.valueOf(relationship.getEndNodeId());

        return startNodeId.equals(edge.tailNode.id) && endNodeId.equals(edge.headNode.id);
        // TODO: Transitive edges
    }

    private static Iterable<Relationship> getRelationships(
            Transaction transaction,
            QbeEdge queryEdge,
            QbeNode resultNode
    ) {
        Node neo4jNode = getNeo4jNode(transaction, resultNode.id);

        @Nullable RelationshipType edgeName = queryEdge.name != null
                ? RelationshipType.withName(queryEdge.name)
                : null;
        Direction direction = Direction.BOTH;
        // TODO: Move if-conditions to QbeEdge
        if (queryEdge.tailNode != null && queryEdge.tailNode.name.equals(resultNode.name)) {
            direction = Direction.OUTGOING;
        } else if (queryEdge.headNode != null && queryEdge.headNode.name.equals(resultNode.name)) {
            direction = Direction.INCOMING;
        }

        return edgeName != null
                ? neo4jNode.getRelationships(direction, edgeName)
                : neo4jNode.getRelationships(direction);
    }

    private static Node getNeo4jNode(Transaction transaction, String id) {
        return transaction.getNodeById(Long.parseLong(id));
    }

    private static QbeEdge visitNeo4jEdge(Relationship neo4jEdge, QbeEdge queryEdge, ResultGraph graph) throws InvalidNodeException {
        long id = neo4jEdge.getId();
        long tailNodeId = neo4jEdge.getStartNodeId();
        long headNodeId = neo4jEdge.getEndNodeId();

        var resultEdge = new QbeEdge(id);
        resultEdge.properties = Neo4jPropertyTraversal.getProperties(neo4jEdge, queryEdge.properties);
        resultEdge.name = queryEdge.name;
        resultEdge.tailNode = graph.get(String.valueOf(tailNodeId));
        resultEdge.headNode = graph.get(String.valueOf(headNodeId));

        return resultEdge;
    }
}
