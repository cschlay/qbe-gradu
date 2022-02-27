package db.neo4j;

import base.QueryBaseTest;
import exceptions.InvalidNodeException;
import graphs.QbeData;
import graphs.QbeEdge;
import graphs.QbeNode;
import db.neo4j.Neo4jPropertyTraversal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jPropertyTraversalTest extends QueryBaseTest {
    @Nested
    class QueryTest {
        @Test
        @DisplayName("should include node id as property")
        void includeNodeIdProperty() throws Exception {
            var queryNode = new QbeNode("Course");
            queryNode.properties.put("id", new QbeData(null, true, false));

            var traversal = new Neo4jPropertyTraversal(queryNode);
            run(tx -> {
                var node = tx.createNode(Label.label("Course"));
                tx.commit();

                assertEquals(node.getId(), traversal.getProperties(node).get("id").value);
            });
        }

        @Test
        @DisplayName("should include edge id as property")
        void includeEdgeIdProperty() throws Exception {
            var queryEdge = new QbeEdge("teaches");
            queryEdge.properties.put("id", new QbeData(null, true, false));

            var traversal = new Neo4jPropertyTraversal(queryEdge);
            run(tx -> {
                var tail = tx.createNode();
                var head = tx.createNode();
                var edge = tail.createRelationshipTo(head, RelationshipType.withName("teaches"));
                tx.commit();

                assertEquals(edge.getId(), traversal.getProperties(edge).get("id").value);
            });
        }

        @Test
        @DisplayName("should accept undefined properties as null")
        void acceptUndefinedAsNull() throws Exception {
            var queryNode = new QbeNode("Course");
            queryNode.properties.put("paper", new QbeData("A4", true, true));

            var traversal = new Neo4jPropertyTraversal(queryNode);
            run(tx -> {
                var node = tx.createNode(Label.label("Course"));
                assertNull(traversal.getProperties(node).get("paper").value);
            });
        }

        @Test
        @DisplayName("should accept node if all checks pass")
        void acceptNode() throws Exception {
            var queryNode = new QbeNode("Course");
            queryNode.properties.put("title", new QbeData("Introduction to .*", true, true));
            queryNode.properties.put("difficulty", new QbeData(4, true, false));

            var traversal = new Neo4jPropertyTraversal(queryNode);
            run(tx -> {
                var node = tx.createNode(Label.label("Course"));
                node.setProperty("title", "Introduction to Algorithms");
                node.setProperty("difficulty", 4);

                assertEquals(2, traversal.getProperties(node).size());
            });
        }

        @Test
        @DisplayName("should reject node if property not defined and is non nullable")
        void rejectNodeNull() throws Exception {
            var queryNode = new QbeNode("Course");
            queryNode.properties.put("paper", new QbeData("A4", true, false));

            var traversal = new Neo4jPropertyTraversal(queryNode);
            run(tx -> {
                var node = tx.createNode(Label.label("Course"));
                assertThrows(InvalidNodeException.class, () -> traversal.getProperties(node));
            });
        }

        @Test
        @DisplayName("should reject node if property check fails")
        void rejectNode() throws Exception {
            var queryNode = new QbeNode("Course");
            queryNode.properties.put("period", new QbeData("Fall", true, false));

            var traversal = new Neo4jPropertyTraversal(queryNode);
            run(tx -> {
                var node = tx.createNode(Label.label("Course"));
                node.setProperty("period", "Winter");
                assertThrows(InvalidNodeException.class, () -> traversal.getProperties(node));
            });
        }
    }
}
