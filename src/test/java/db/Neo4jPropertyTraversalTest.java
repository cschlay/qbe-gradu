package db;

import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import db.neo4j.Neo4jPropertyTraversal;
import demo.CourseGraphDemo;
import graphml.queries.QueryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
            var node = tx.findNodes(label).stream().findAny();

            if (node.isPresent()) {
                var neo4jNode = node.get();
                var property = traversal.getProperties(neo4jNode).get("id");
                assertEquals(neo4jNode.getId(), property.value);
            } else {
                fail("Nodes not found in the test database.");
            }
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
}
