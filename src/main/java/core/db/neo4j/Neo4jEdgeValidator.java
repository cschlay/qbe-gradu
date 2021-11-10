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
        ArrayList<QbeEdge> edges = new ArrayList<>();
        Iterable<Relationship> relationships = getRelationships(transaction, queryEdge, resultNode);

        boolean hasValidEdge = false;
        for (Relationship relationship : relationships) {
            QbeEdge resultEdge = visitNeo4jEdge(relationship, queryEdge, graph);
            if (hasPath(queryEdge, resultEdge, relationship)) {
                if (!queryEdge.isHidden) {
                    edges.add(resultEdge);
                }
                hasValidEdge = true;
            }
        }

        if (!hasValidEdge) {
            throw new InvalidNodeException();
        }

        return edges;
    }

    // Recursive solution is possible
    private static boolean hasPath(QbeEdge queryEdge, QbeEdge resultEdge, Relationship relationship) {
        boolean tailIsValid = queryEdge.tailNode == null;
        boolean headIsValid = queryEdge.headNode == null;
        if (queryEdge.tailNode != null) {
            tailIsValid = queryEdge.tailNode.hasSameName(resultEdge.tailNode);
        }
        if (queryEdge.headNode != null) {
            headIsValid = queryEdge.headNode.hasSameName(resultEdge.headNode);
        }

        return tailIsValid && headIsValid;
        // TODO: Transitive edges use relationship to traverse
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
        if (resultNode.hasSameName(queryEdge.tailNode)) {
            direction = Direction.OUTGOING;
        } else if (resultNode.hasSameName(queryEdge.headNode)) {
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
