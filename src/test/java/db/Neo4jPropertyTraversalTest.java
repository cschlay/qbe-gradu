package db;

import core.exceptions.InvalidNodeException;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import db.neo4j.Neo4jPropertyTraversal;
import demo.CourseGraphDemo;
import graphml.queries.QueryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jPropertyTraversalTest extends QueryTest {
    @Test
    @DisplayName("should include node id as property")
    void includeNodeIdProperty() throws Exception {
        var label = CourseGraphDemo.Labels.Course;

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("id", new QbeData(null, true, false));

        var traversal = new Neo4jPropertyTraversal(queryNode);
        try (var tx = getDatabase().beginTx()) {
            var neo4jNode = getNeo4jNode(tx, label);
            var property = traversal.getProperties(neo4jNode).get("id");
            assertEquals(neo4jNode.getId(), property.value);
        }
    }

    @Test
    @DisplayName("should include edge id as property")
    void includeEdgeIdProperty() throws Exception {
        var label = CourseGraphDemo.Relations.teaches;

        var queryEdge = new QbeEdge(label.name());
        queryEdge.properties.put("id", new QbeData(null, true, false));

        var traversal = new Neo4jPropertyTraversal(queryEdge);
        try (var tx = getDatabase().beginTx()) {
            var edge = tx.findRelationships(label).stream().findAny();

            if (edge.isPresent()) {
                var neo4jEdge = edge.get();
                var property = traversal.getProperties(neo4jEdge).get("id");
                assertEquals(neo4jEdge.getId(), property.value);
            } else {
                fail("Nodes not found in the test database.");
            }
        }
    }

    @Test
    @DisplayName("should accept undefined properties as null")
    void acceptUndefinedAsNull() throws Exception {
        var label = CourseGraphDemo.Labels.Course;

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("paper", new QbeData("A4", true, true));

        var traversal = new Neo4jPropertyTraversal(queryNode);
        try (var tx = getDatabase().beginTx()) {
            var node = getNeo4jNode(tx, label);
            var properties = traversal.getProperties(node);
            assertNull(properties.get("paper").value);
        }
    }

    @Test
    @DisplayName("should accept node if all checks pass")
    void acceptNode() throws Exception {
        var label = CourseGraphDemo.Labels.Course;

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("title", new QbeData("Introduction to .*", true, true));
        queryNode.properties.put("difficulty", new QbeData(4, true, false));

        try (var tx = getDatabase().beginTx()) {
            var node = tx.findNode(label, "title", "Introduction to Algorithms");

            var traversal = new Neo4jPropertyTraversal(queryNode);
            var properties = traversal.getProperties(node);
            assertEquals(2, properties.size());
        }
    }

    @Test
    @DisplayName("should reject node if a property fails")
    void rejectNode() {
        var label = CourseGraphDemo.Labels.Course;

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("paper", new QbeData("A4", true, false));

        var traversal = new Neo4jPropertyTraversal(queryNode);
        try (var tx = getDatabase().beginTx()) {
            var node = getNeo4jNode(tx, label);
            assertThrows(InvalidNodeException.class, () -> traversal.getProperties(node));
        }
    }

    @Test
    @DisplayName("should accept use non-selected properties in validation")
    void validateWithNonSelectedProperties() {
        var label = CourseGraphDemo.Labels.Course;

        var queryNode = new QbeNode(label.name());
        queryNode.properties.put("title", new QbeData("Introduction to .*", false, true));
        queryNode.properties.put("difficulty", new QbeData(2, false, false));

        try (var tx = getDatabase().beginTx()) {
            var node = tx.findNode(label, "title", "Introduction to Algorithms");

            var traversal = new Neo4jPropertyTraversal(queryNode);
            assertThrows(InvalidNodeException.class, () -> traversal.getProperties(node));
        }
    }

    private Node getNeo4jNode(Transaction tx, Label label) {
        var node = tx.findNodes(label).stream().findAny();
        return node.orElseGet(node::get);
    }
}
