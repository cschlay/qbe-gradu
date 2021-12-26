package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.*;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Contains traversal operations. */
public class Neo4jTraversal {
    private static final Logger logger = Logger.getLogger(Neo4jTraversal.class.getName());

    private final GraphDatabaseService db;
    private final QueryGraph queryGraph;
    private final Neo4jNodeTraversal nodeTraversal;

    public Neo4jTraversal(GraphDatabaseService database, QueryGraph queryGraph) {
        this.queryGraph = queryGraph;

        db = database;
        nodeTraversal = new Neo4jNodeTraversal(db);
    }

    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * <p>Implementation is DFS -algorithm in Introduction to Algorithms 3rd ed. p.604.
     *
     * @return result graph for traversal
     */
    public ResultGraph buildResultGraph() {
        logger.log(Level.INFO, "Querying graph: {0}", queryGraph);

        return nodeTraversal.traverse(queryGraph);
    }

    /*public void visitQueryEdge(Transaction transaction, ResultGraph resultGraph, QbeEdge queryEdge) {
        logger.log(Level.INFO, "Querying edge: {0}", queryEdge);

        ArrayList<String> invalidNodeIds = new ArrayList<>();

        for (var resultNode : resultGraph.values()) {
            // The conditions need to checked once, so that we do not reject valid nodes that do not
            // belong to edge query.
            if (resultNode.equalByName(queryEdge.tailNode)
                    || resultNode.equalByName(queryEdge.headNode)) {
                try {
                    ArrayList<QbeEdge> edges =
                            Neo4jEdgeValidator.validateRelationships(
                                    transaction, queryEdge, resultNode, resultGraph);
                    resultNode.edges.addAllById(edges);
                } catch (InvalidNodeException exception) {
                    // TODO: This get incorrectly discarded because the edge is not defined smh
                    System.out.printf("tobe discarded %s %n", resultNode);
                    invalidNodeIds.add(resultNode.id);
                    logger.log(Level.INFO, "Discard node because of edge properties: {0}", resultNode);
                }
            } else if (queryEdge.tailNode == null && queryEdge.headNode == null) {
                System.out.println("BOTH ENDS ARE NULL !");
            }
        }
        invalidNodeIds.forEach(resultGraph::remove);
    }*/
}
