package db.neo4j;

import core.graphs.*;
import org.neo4j.graphdb.*;

/**
 * Contains traversal operations
 */
public class Neo4jTraversal {
    private final Neo4jNodeTraversal nodeTraversal;

    /**
     * The constructor should accept only database.
     * It is possible that multiple query graphs is used.
     *
     * @param database connection
     */
    public Neo4jTraversal(GraphDatabaseService database) {
        nodeTraversal = new Neo4jNodeTraversal(database);
    }

    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * @return result graph of the traversal
     */
    public ResultGraph executeQueryGraph(QueryGraph queryGraph) {
        return nodeTraversal.traverse(queryGraph);
    }
}
