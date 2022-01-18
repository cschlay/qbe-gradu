package db;

import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import db.neo4j.Neo4jNodeTraversal;
import demo.CourseGraphDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jNodeTraversalTest extends QueryTest {
    @Test
    @DisplayName("should filter nodes by name and properties")
    void filterByNameAndProperties() {
        var label = CourseGraphDemo.Labels.Course;
        var traversal = new Neo4jNodeTraversal(getDatabase());

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("title", new QbeData("Introduction to .*"));
        queryNode.properties.put("difficulty", new QbeData(4));

        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);

        try (var tx = getDatabase().beginTx()) {
            var properties = new HashMap<String , Object>();
            properties.put("title", "Introduction to Algorithms");
            properties.put("difficulty", 4);

            var nodes = tx.findNodes(label, properties);
            nodes.forEachRemaining(node -> {
                var resultNode = resultGraph.get(String.valueOf(node.getId()));
                assertNotNull(resultNode);
            });
        }
    }

    @Test
    @DisplayName("should retrieve nodes by name")
    void retrieveNodesByName() {
        var label = CourseGraphDemo.Labels.Course;
        var traversal = new Neo4jNodeTraversal(getDatabase());

        var queryNode = new QbeNode(label.name());
        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);
        assertTrue(resultGraph.unvisitedEdges.isEmpty());

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
    void retrieveNodeById() {
        var label = CourseGraphDemo.Labels.Course;
        var traversal = new Neo4jNodeTraversal(getDatabase());

        try (var tx = getDatabase().beginTx()) {
            var node = tx.findNodes(label).stream().findAny();
            if (node.isPresent()) {
                var neo4jNode = node.get();
                var queryNode = new QbeNode(neo4jNode.getId(), label.name());

                var queryGraph = new QueryGraph();
                queryGraph.put(queryNode.name, queryNode);

                var resultGraph = traversal.traverse(queryGraph);
                assertEquals(1, resultGraph.order());

                var resultNode = resultGraph.get(queryNode.id);
                assertNotNull(resultNode);
            } else {
                fail("Database doesn't have any nodes!");
            }
        }
    }

    // There may be case where you put an id but there are multiple relations?
}
