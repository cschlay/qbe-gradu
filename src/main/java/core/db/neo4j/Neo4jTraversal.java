package core.db.neo4j;

import core.graphs.*;
import core.interfaces.QueryTraversable;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Objects;

/**
 * Contains traversal operations.
 */
public class Neo4jTraversal implements QueryTraversable {
    @NotNull
    private final GraphDatabaseService db;

    @NotNull
    private final QueryGraph queryGraph;

    @NotNull
    private final ResultGraph resultGraph;


    /**
     * Traverse the query graph and build the result graph while using depth-first-search (DFS).
     *
     * Implementation is DFS -algorithm in Introduction to Algorithms 3rd ed. p.604.
     *
     * @return result graph for traversal
     */
    public ResultGraph buildResultGraph() {
        try (var transaction = db.beginTx()) {
            // TODO: Move node traversal to own place
            queryGraph.forEach((name, node) -> processQueryNode(transaction, node));
        }

        return resultGraph;
    }


    public void processQueryNode(@NotNull Transaction transaction, @NotNull QbeNode queryNode) {
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            ResourceIterator<Node> nodes = transaction.findNodes(label);
            nodes.forEachRemaining((Node neo4jNode) -> addResultNode(neo4jNode, queryNode));
        } else {
            // Anonymous nodes, means that all are processed
            ResourceIterable<Node> nodes = transaction.getAllNodes();
            nodes.forEach((Node neo4jNode) -> addResultNode(neo4jNode, queryNode));
        }
    }

    private void addResultNode(Node neo4jNode, QbeNode query) {
        var result = new QbeNode(neo4jNode.getId(), query.name);

        for (String propertyName : query.properties.keySet()) {
            @NotNull QbeData qbeData = Objects.requireNonNull(query.properties.get(propertyName));
            try {
                Object value = neo4jNode.getProperty(propertyName);

                // Only include properties that passes constraint checks
                if (qbeData.checkConstraints(value)) {
                    result.properties.put(propertyName, new QbeData(value));
                } else {
                    return;
                }
            } catch (NotFoundException e) {
                // Non-nullable properties must always be defined.
                if (!qbeData.isNullable) {
                    return;
                }
            }
        }

        // TODO: Process edges
        resultGraph.nodes.put(result.id, result);
    }


    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
    }
}
