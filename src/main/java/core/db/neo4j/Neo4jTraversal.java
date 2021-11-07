package core.db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.*;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Contains traversal operations.
 */
public class Neo4jTraversal {
    @NotNull private final GraphDatabaseService db;
    @NotNull private final QueryGraph queryGraph;
    @NotNull private final ResultGraph resultGraph;
    @NotNull private final HashSet<QbeEdge> queryEdgeQueue;

    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * Implementation is DFS -algorithm in Introduction to Algorithms 3rd ed. p.604.
     *
     * @return result graph for traversal
     */
    public ResultGraph buildResultGraph() {
        try (var transaction = db.beginTx()) {
            queryGraph.forEach((name, node) -> visitQueryNode(transaction, node));


            queryEdgeQueue.iterator().forEachRemaining(edge -> {
                visitQueryEdge(transaction, edge);
            });
        }

        return resultGraph;
    }


    public void visitQueryEdge(Transaction transaction, QbeEdge queryEdge) {
        ArrayList<String> invalidNodeIds = new ArrayList<>();

        if (queryEdge.tailNode == null && queryEdge.headNode == null) {

        } else if (queryEdge.tailNode == null) {
            // head is not null

        } else if (queryEdge.headNode == null) {
            // tail is not null
            for (var resultNode : resultGraph.values()) {
                try {
                    if (queryEdge.tailNode.name.equals(resultNode.name)) {
                        var neo4jNode = transaction.getNodeById(Long.parseLong(resultNode.id));

                        Iterable<Relationship> relationships;
                        if (queryEdge.name != null) {
                            relationships = neo4jNode.getRelationships(Direction.OUTGOING, RelationshipType.withName(queryEdge.name));
                        } else {
                            relationships = neo4jNode.getRelationships(Direction.OUTGOING);
                        }

                        int size = 0;
                        for (var relationship : relationships) {
                            QbeEdge resultEdge = visitNeo4jEdge(relationship, queryEdge);
                            resultNode.edges.add(resultEdge);
                            size++;
                        }

                        if (size == 0) {
                            throw new InvalidNodeException();
                        }
                    }
                } catch (InvalidNodeException ignore) {
                    // Must remove
                    invalidNodeIds.add(resultNode.id);
                }
            }
        } else {
            // both tail and head are not null
            for (var resultNode : resultGraph.values())
            {
                if (queryEdge.headNode.name.equals(resultNode.name) || queryEdge.tailNode.name.equals(resultNode.name)) {
                    try {
                        ArrayList<QbeEdge> edges = Neo4jEdgeValidator.validateRelationships(transaction, queryEdge, resultNode, resultGraph);
                        resultNode.edges.addAll(edges);
                    } catch (InvalidNodeException ignore) {
                        invalidNodeIds.add(resultNode.id);
                    }
                }
            }
        }
        invalidNodeIds.forEach(resultGraph::remove);
    }

    public void visitQueryNode(@NotNull Transaction transaction, @NotNull QbeNode queryNode) {
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            ResourceIterator<Node> nodes = transaction.findNodes(label);
            nodes.forEachRemaining((Node neo4jNode) -> visitNeo4jNode(neo4jNode, queryNode));
        } else {
            // Anonymous nodes, means that all are processed
            ResourceIterable<Node> nodes = transaction.getAllNodes();
            nodes.forEach((Node neo4jNode) -> visitNeo4jNode(neo4jNode, queryNode));
        }
        queryEdgeQueue.addAll(queryNode.edges);
    }

    private QbeEdge visitNeo4jEdge(Relationship neo4jEdge, QbeEdge queryEdge) throws InvalidNodeException {
        long id = neo4jEdge.getId();
        long tailNodeId = neo4jEdge.getStartNodeId();
        long headNodeId = neo4jEdge.getEndNodeId();

        var resultEdge = new QbeEdge(id);
        resultEdge.properties = Neo4jPropertyTraversal.getProperties(neo4jEdge, queryEdge.properties);
        resultEdge.name = queryEdge.name;
        resultEdge.tailNode = resultGraph.get(String.valueOf(tailNodeId));
        resultEdge.headNode = resultGraph.get(String.valueOf(headNodeId));

        return resultEdge;
    }

    private void visitNeo4jNode(Node neo4jNode, QbeNode query) {
        long nodeId = neo4jNode.getId();
        if (queryGraph.containsKey(String.valueOf(nodeId))) {
            return;
        }

        try {
            var result = new QbeNode(neo4jNode.getId(), query.name);
            result.properties = Neo4jPropertyTraversal.getProperties(neo4jNode, query.properties);
            resultGraph.put(result.id, result);
        } catch (InvalidNodeException ignored) {}
    }

    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
        queryEdgeQueue = new HashSet<QbeEdge>();
    }
}
