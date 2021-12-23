package db;

import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import db.neo4j.Neo4jNodeTraversal;
import demo.CourseGraphDemo;
import graphml.queries.QueryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jNodeTraversalTest extends QueryTest {
    @Test
    @DisplayName("should filter nodes by property")
    void filterByProperty() {

    }

    @Test
    @DisplayName("should filter by multiple properties")
    void filterByMultipleProperties() {

    }

    @Test
    @DisplayName("should retrieve nodes by name")
    void retrieveAllNodesByName() {
        var label = CourseGraphDemo.Labels.Course;
        var traversal = new Neo4jNodeTraversal(getDatabase());

        var queryNode = new QbeNode(label.name());
        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);

        try (var tx = getDatabase().beginTx()) {
            var nodes = tx.findNodes(label);
            assertEquals(nodes.stream().count(), resultGraph.order());

            nodes.forEachRemaining(node -> {
                assertNotNull(resultGraph.get(String.valueOf(node.getId())));
            });
        }
    }

    @Test
    @DisplayName("should retrieve node by id")
    void retrieveNodeByName() {

    }

    @Test
    @DisplayName("should retrieve multiple nodes")
    void retrieveMultipleNodes() {

    }
}
