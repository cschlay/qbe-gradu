package db.neo4j;

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
        int edgeCount = 0;
        for (Relationship relationship : relationships) {
            QbeEdge resultEdge = visitNeo4jEdge(relationship, queryEdge, graph);
            if (hasPath(queryEdge, resultEdge, relationship)) {
                if (!queryEdge.isHidden) {
                    edges.add(resultEdge);
                }
                hasValidEdge = true;
            }
            edgeCount++;
        }

        if (!hasValidEdge && (!queryEdge.isTransitive && edgeCount == 0)) {
            throw new InvalidNodeException("Node %s doesn't have a valid edge", resultNode);
        }

        return edges;
    }

    // Recursive solution is possible
    private static boolean hasPath(QbeEdge queryEdge, QbeEdge resultEdge, Relationship relationship) {
        boolean tailIsValid = queryEdge.tailNode == null;
        boolean headIsValid = queryEdge.headNode == null;
        if (queryEdge.tailNode != null) {
            tailIsValid = queryEdge.tailNode.equalByName(resultEdge.tailNode);
        }
        if (queryEdge.headNode != null) {
            headIsValid = queryEdge.headNode.equalByName(resultEdge.headNode);
        }

        if (queryEdge.isTransitive) {
            if (tailIsValid) {
                return pathToHeadExists(relationship.getEndNode(), Label.label(queryEdge.headNode.name), 0);
            }
            if (headIsValid) {
                return pathToTailExists(relationship.getStartNode(), Label.label(queryEdge.tailNode.name), 0);
            }
            // Accept all if both edges are null.
            return true;
        }
        return tailIsValid && headIsValid;
    }

    private static boolean pathToTailExists(Node node, Label label, int recursionCount) {
        if (node.hasLabel(label)) {
            return true;
        }
        if (recursionCount == 5) {
            return false;
        }
        // DFS without coloring, may end to nodes that were visited previously
        Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING);
        for (var relationship : relationships) {
            boolean found = pathToHeadExists(relationship.getStartNode(), label, recursionCount + 1);
            if (found) {
                return true;
            }
        }
        return false;
    }

    private static boolean pathToHeadExists(Node node, Label label, int recursionCount) {
        if (node.hasLabel(label)) {
            return true;
        }
        if (recursionCount == 5) {
            return false;
        }
        // DFS without coloring, may end to nodes that were visited previously
        Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING);
        for (var relationship : relationships) {
            boolean found = pathToHeadExists(relationship.getEndNode(), label, recursionCount + 1);
            if (found) {
                return true;
            }
        }
        return false;
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
        if (resultNode.equalByName(queryEdge.tailNode)) {
            direction = Direction.OUTGOING;
        } else if (resultNode.equalByName(queryEdge.headNode)) {
            // TODO: Reverse lookup doesn't work! the name is certainly not same with transitive edges
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

        var properties = new Neo4jPropertyTraversal(queryEdge).getProperties(neo4jEdge);
        var resultEdge = new QbeEdge(id, queryEdge.name);
        resultEdge.properties.putAll(properties);
        resultEdge.tailNode = graph.get(String.valueOf(tailNodeId));
        resultEdge.headNode = graph.get(String.valueOf(headNodeId));

        return resultEdge;
    }
}
