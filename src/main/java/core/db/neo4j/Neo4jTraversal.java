package core.db.neo4j;

import core.graphs.*;
import core.interfaces.QueryTraversable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.api.exceptions.PropertyKeyNotFoundException;

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
    private QbeNode toResultNode(Node neo4jNode, QbeNode query) {
        var result = new QbeNode(neo4jNode.getId(), query.name);

        if (query.properties.isEmpty()) {
            return result;
        }

        for (String propertyName : query.properties.keySet()) {
            @NotNull QbeData queryProperty = Objects.requireNonNull(query.properties.get(propertyName));
            try {
                // TODO: Extract this part to own function
                Object resultProperty = neo4jNode.getProperty(propertyName);

                // TODO: Add support for Regex
                if (!queryProperty.constraints.isEmpty()) {
                    boolean isInteger = queryProperty.isInteger();
                    for (var constraint : queryProperty.constraints) {
                        // TODO: Constraint checks as separate function
                        if (constraint.type == ConstraintType.GREATER_THAN && isInteger) {
                            var v = (Integer) queryProperty.value;
                            if (v > (Integer) constraint.value) {
                                result.properties.put(propertyName, new QbeData(resultProperty));
                            }
                        }
                    }
                } else if (queryProperty.value != null) {
                    if (queryProperty.isString() && queryProperty.value.equals(resultProperty)) {
                        result.properties.put(propertyName, new QbeData(resultProperty));
                    }
                } else if (queryProperty.value == null && queryProperty.constraints.isEmpty()) {
                    result.properties.put(propertyName, new QbeData(resultProperty));
                } else {
                    return null;
                }
            } catch (NotFoundException e) {

            }
        }

        return result;
    }


    public Neo4jTraversal(@NotNull GraphDatabaseService db, @NotNull QueryGraph queryGraph) throws ParserConfigurationException {
        this.db = db;
        this.queryGraph = queryGraph;
        this.resultGraph = new ResultGraph();
    }
}
