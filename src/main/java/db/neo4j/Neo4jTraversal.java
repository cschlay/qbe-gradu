package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.*;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains traversal operations.
 */
public class Neo4jTraversal {
    private static final Logger logger = Logger.getLogger(Neo4jTraversal.class.getName());

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
        logger.log(Level.INFO, "Querying graph: {0}", queryGraph);

        try (var transaction = db.beginTx()) {
            queryGraph.forEach((name, node) -> visitQueryNode(transaction, node));
            queryEdgeQueue.iterator().forEachRemaining(edge -> visitQueryEdge(transaction, edge));
        }

        logger.log(Level.INFO, "Built result graph: {0}", resultGraph);
        return resultGraph;
    }


    public void visitQueryEdge(Transaction transaction, QbeEdge queryEdge) {
        logger.log(Level.INFO, "Querying edge: {0}", queryEdge);

        ArrayList<String> invalidNodeIds = new ArrayList<>();

        for (var resultNode : resultGraph.values())
        {
            // The conditions need to checked once, so that we do not reject valid nodes that do not belong to edge query.
            if (resultNode.equalByName(queryEdge.tailNode) || resultNode.equalByName(queryEdge.headNode)) {
                try {
                    ArrayList<QbeEdge> edges = Neo4jEdgeValidator.validateRelationships(transaction, queryEdge, resultNode, resultGraph);
                    resultNode.edges.addAllById(edges);
                } catch (InvalidNodeException exception) {
                    invalidNodeIds.add(resultNode.id);
                    logger.log(Level.INFO, "Discard node: {0}", exception.getMessage());
                }
            } else if (queryEdge.tailNode == null && queryEdge.headNode == null) {
                System.out.println("BOTH ENDS ARE NULL !");
            }
        }
        invalidNodeIds.forEach(resultGraph::remove);
    }

    public void visitQueryNode(@NotNull Transaction transaction, @NotNull QbeNode queryNode) {
        logger.log(Level.INFO, "Querying node: {0}", queryNode);

        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            ResourceIterator<Node> nodes = transaction.findNodes(label);
            nodes.forEachRemaining((Node neo4jNode) -> visitNeo4jNode(neo4jNode, queryNode));
        } else {
            // Anonymous nodes, means that all are processed
            ResourceIterable<Node> nodes = transaction.getAllNodes();
            nodes.forEach((Node neo4jNode) -> visitNeo4jNode(neo4jNode, queryNode));
        }

        queryEdgeQueue.addAll(queryNode.edges.values());
    }

    private void visitNeo4jNode(Node neo4jNode, QbeNode query) {
        long nodeId = neo4jNode.getId();
        logger.log(Level.INFO, "Visiting Neo4j node: {0}", nodeId);

        if (queryGraph.containsKey(String.valueOf(nodeId))) {
            logger.log(Level.INFO, "Discarding node: {0}", nodeId);
            return;
        }

        try {
            var properties = new Neo4jPropertyTraversal(query).getProperties(neo4jNode);
            var resultNode = new QbeNode(nodeId, query.name);
            resultNode.properties.putAll(properties);

            resultGraph.put(resultNode.id, resultNode);
        } catch (InvalidNodeException ignored) {
            // The node should be discarded.
            logger.log(Level.INFO, "Discarding node: {0}", nodeId);
        }
    }

    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
        queryEdgeQueue = new HashSet<>();
    }
}
