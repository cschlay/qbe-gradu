package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.*;
import interfaces.ResultWriter;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Contains traversal operations.
 */
public class Neo4jTraversal {
    @NotNull private final GraphDatabaseService db;
    @NotNull private final QueryGraph queryGraph;
    @NotNull private final ResultGraph resultGraph;
    @NotNull private final HashSet<QbeEdge> queryEdgeQueue;

    private final ArrayList<String> hiddenNodeIds;
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

            hiddenNodeIds.forEach(resultGraph::remove);
        }

        return resultGraph;
    }


    public void visitQueryEdge(Transaction transaction, QbeEdge queryEdge) {
        ArrayList<String> invalidNodeIds = new ArrayList<>();

        for (var resultNode : resultGraph.values())
        {
            // The conditions need to checked once, so that we do not reject valid nodes that do not belong to edge query.
            if (resultNode.hasSameName(queryEdge.tailNode) || resultNode.hasSameName(queryEdge.headNode)) {
                try {
                    ArrayList<QbeEdge> edges = Neo4jEdgeValidator.validateRelationships(transaction, queryEdge, resultNode, resultGraph);
                    resultNode.edges.addAll(edges);
                } catch (InvalidNodeException ignore) {
                    invalidNodeIds.add(resultNode.id);
                }
            } else if (queryEdge.tailNode == null && queryEdge.headNode == null) {
                System.out.println("BOTH ENDS ARE NULL !");
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

    private void visitNeo4jNode(Node neo4jNode, QbeNode query) {
        long nodeId = neo4jNode.getId();
        if (queryGraph.containsKey(String.valueOf(nodeId))) {
            return;
        }

        try {
            var result = new QbeNode(neo4jNode.getId(), query.name);
            result.properties = Neo4jPropertyTraversal.getProperties(neo4jNode, query.properties);
            resultGraph.put(result.id, result);
            if (query.isHidden) {
                hiddenNodeIds.add(result.id);
            }
        } catch (InvalidNodeException ignored) {}
    }

    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
        queryEdgeQueue = new HashSet<>();
        hiddenNodeIds = new ArrayList<>();
    }
}
