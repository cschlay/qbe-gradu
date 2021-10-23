package core.db.neo4j;

import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Contains traversal operations.
 */
public class Neo4jTraversal {
    @NotNull
    private final GraphDatabaseService db;

    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * Implementation is DFS -algorithm in Introduction to Algorithms 3rd ed. p.604.
     *
     * @param queryGraph
     * @return
     */
    public ResultGraph traverse(@NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        var resultGraph = new ResultGraph();
        try (var tx = db.beginTx()) {
            queryGraph.nodes.forEach((name, node) -> {
                // Supports only single labeled graph databases.
                Label neo4jLabel = Label.label(node.name);
                var nodes = tx.findNodes(neo4jLabel);
                nodes.forEachRemaining(n -> {
                    QbeNode resultNode = new QbeNode();
                    resultNode.id = String.valueOf(n.getId());
                    resultNode.name = node.name;
                    resultGraph.nodes.put(resultNode.getId(), resultNode);
                });
            });
        }

        return resultGraph;
    }

    public Neo4jTraversal(@NotNull GraphDatabaseService db) {
        this.db = db;
    }
}
