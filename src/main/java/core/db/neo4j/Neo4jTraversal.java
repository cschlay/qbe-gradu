package core.db.neo4j;

import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import core.interfaces.QueryTraversable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Objects;
import java.util.regex.Pattern;

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
     * @return
     */
    public ResultGraph buildResultGraph() {
        try (var transaction = db.beginTx()) {
            // TODO: Move node traversal to own place
            queryGraph.nodes.forEach((name, node) -> processQueryNode(transaction, node));
        }

        return resultGraph;
    }


    public void processQueryNode(@NotNull Transaction transaction, @NotNull QbeNode queryNode) {
        // TODO: Merge these operations
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            ResourceIterator<Node> nodes = transaction.findNodes(label);
            nodes.forEachRemaining(node -> {
                QbeNode resultNode = toResultNode(node, queryNode);
                if (resultNode != null) {
                    // TODO: Check the edges
                    resultGraph.nodes.put(resultNode.id, resultNode);
                }
            });
        } else {
            // Anonymous nodes
            ResourceIterable<Node> nodes = transaction.getAllNodes();
            nodes.forEach(node -> {
                QbeNode resultNode = toResultNode(node, queryNode);
                if (resultNode != null) {
                    // TODO: Check the edges
                    resultGraph.nodes.put(resultNode.id, resultNode);
                }
            });
        }
    }

    @Nullable
    private QbeNode toResultNode(Node node, QbeNode queryNode) {
        var resultNode = new QbeNode();
        resultNode.setId(node.getId());
        resultNode.name = queryNode.name;

        if (!queryNode.properties.isEmpty()) {
            for (String propertyName : queryNode.properties.keySet()) {
                Object queryProperty = queryNode.properties.get(propertyName);
                // TODO: no such property NotFoundException
                Object resultProperty = node.getProperty(propertyName);

                if (queryProperty instanceof Pattern) {
                    // TODO: Add support for regex.
                } else if (Objects.isNull(queryProperty) || queryProperty.equals(resultProperty)) {
                    resultNode.properties.put(propertyName, resultProperty);
                } else {
                    // Reject the node if property is different or don't exist.
                    return null;
                }
            }
        }
        return resultNode;
    }


    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
    }
}
