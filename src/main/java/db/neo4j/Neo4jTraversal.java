package db.neo4j;

import graphs.QueryGraph;
import graphs.ResultGraph;
import org.neo4j.graphdb.*;

/**
 * Contains traversal operations
 */
public class Neo4jTraversal {
    private final GraphDatabaseService databaseService;

    /**
     * The constructor should accept only database.
     * It is possible that multiple query graphs is used.
     *
     * @param database connection
     */
    public Neo4jTraversal(GraphDatabaseService database) {
        databaseService = database;
    }

    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * @return result graph of the traversal
     */
    public ResultGraph executeQueryGraph(QueryGraph queryGraph) {
        Transaction tx = databaseService.beginTx();
        var traversal = new Neo4jQueryGraphTraversal(tx);
        return traversal.traverse(queryGraph);
    }
}
