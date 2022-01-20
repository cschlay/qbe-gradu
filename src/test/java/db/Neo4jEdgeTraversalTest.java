package db;

import base.QueryBaseTest;
import core.exceptions.InvalidNodeException;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import db.neo4j.Neo4jEdgeTraversal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jEdgeTraversalTest extends QueryBaseTest {
    @Nested
    @DisplayName("using head nodes")
    class HeadNodeTest {
        @Test
        @DisplayName("should query edge by name")
        void queryEdgeByName() {
            inTransaction(tx -> {
                var head = tx.createNode(Label.label("Topic"));
                var tail = tx.createNode();
                var relation = tail.createRelationshipTo(head, RelationshipType.withName("contains"));

                var queryNode = new QbeNode("Topic");
                var queryEdge = new QbeEdge("contains");
                queryEdge.headNode = queryNode;
                queryNode.edges.put(queryEdge.name, queryEdge);

                var resultNode = new QbeNode(head.getId(), "Topic");
                var resultGraph = new ResultGraph();
                var traversal = new Neo4jEdgeTraversal(resultGraph);

                try {
                    traversal.query(head, queryNode, resultNode);
                } catch (InvalidNodeException e) {
                    e.printStackTrace();
                }

                assertFalse(resultNode.edges.isEmpty(), "result node doesn't have edges");

                var assertionEdge = resultNode.edges.get(String.valueOf(relation.getId()));
                assertNotNull(assertionEdge);
                assertEquals("contains", assertionEdge.name);
                assertEquals(resultNode, assertionEdge.headNode);
                assertNull(assertionEdge.tailNode);
            });
        }

        @Test
        @DisplayName("should filter edges")
        void filterEdges() {
            inTransaction(tx -> {
                // Setup
                var head = tx.createNode(Label.label("Topic"));
                var tail = tx.createNode(Label.label("Book"));
                var relation = tail.createRelationshipTo(head, RelationshipType.withName("contains"));
                relation.setProperty("difficulty", 3);

                var trash = tx.createNode();
                tail.createRelationshipTo(trash, RelationshipType.withName("trash"));
                tail.createRelationshipTo(trash, RelationshipType.withName("contains")).setProperty("difficulty", 2);


                var queryNode = new QbeNode("Topic");
                var queryEdge = new QbeEdge("contains");
                queryEdge.headNode = queryNode;
                queryEdge.properties.put("difficulty", new QbeData(3));
                queryNode.edges.put(queryEdge.name, queryEdge);

                var resultNode = new QbeNode(head.getId(), "Topic");
                var resultGraph = new ResultGraph();
                var traversal = new Neo4jEdgeTraversal(resultGraph);

                try {
                    traversal.query(head, queryNode, resultNode);
                } catch (InvalidNodeException e) {
                    e.printStackTrace();
                }

                assertEquals(1, resultNode.edges.size());
                var resultEdge = resultNode.edges.get(String.valueOf(relation.getId()));
                assertNotNull(resultEdge);
                assertFalse(resultEdge.properties.isEmpty());
                assertEquals(relation.getProperty("difficulty"), resultEdge.property("difficulty"));
            });
        }
    }

    @Nested
    @DisplayName("using tail nodes")
    class TailNodeTest {
        @Test
        @DisplayName("should query edge by name")
        void queryEdgeByName() {
            inTransaction(tx -> {
                var head = tx.createNode();
                var tail = tx.createNode(Label.label("Book"));
                var relation = tail.createRelationshipTo(head, RelationshipType.withName("contains"));

                var queryNode = new QbeNode("Book");
                var queryEdge = new QbeEdge("contains");
                queryEdge.tailNode = queryNode;
                queryNode.edges.put(queryEdge.name, queryEdge);

                var resultNode = new QbeNode(tail.getId(), "Book");
                var resultGraph = new ResultGraph();
                var traversal = new Neo4jEdgeTraversal(resultGraph);

                try {
                    traversal.query(tail, queryNode, resultNode);
                } catch (InvalidNodeException e) {
                    e.printStackTrace();
                }

                assertFalse(resultNode.edges.isEmpty(), "result node doesn't have edges");

                var assertionEdge = resultNode.edges.get(String.valueOf(relation.getId()));
                assertNotNull(assertionEdge);
                assertEquals("contains", assertionEdge.name);
                assertEquals(resultNode, assertionEdge.tailNode);
                assertNull(assertionEdge.headNode);
            });
        }

        @Test
        @DisplayName("should filter edges")
        void filterEdges() {
            inTransaction(tx -> {
                // Setup
                var head = tx.createNode(Label.label("Topic"));
                var tail = tx.createNode(Label.label("Book"));
                var relation = tail.createRelationshipTo(head, RelationshipType.withName("contains"));
                relation.setProperty("difficulty", 3);

                var trash = tx.createNode();
                tail.createRelationshipTo(trash, RelationshipType.withName("trash"));
                tail.createRelationshipTo(trash, RelationshipType.withName("contains")).setProperty("difficulty", 2);


                var queryNode = new QbeNode("Book");
                var queryEdge = new QbeEdge("contains");
                queryEdge.tailNode = queryNode;
                queryEdge.properties.put("difficulty", new QbeData(3));
                queryNode.edges.put(queryEdge.name, queryEdge);

                var resultNode = new QbeNode(tail.getId(), "Book");
                var resultGraph = new ResultGraph();
                var traversal = new Neo4jEdgeTraversal(resultGraph);

                try {
                    traversal.query(tail, queryNode, resultNode);
                } catch (InvalidNodeException e) {
                    e.printStackTrace();
                }

                assertEquals(1, resultNode.edges.size());
                var resultEdge = resultNode.edges.get(String.valueOf(relation.getId()));
                assertNotNull(resultEdge);
                assertFalse(resultEdge.properties.isEmpty());
                assertEquals(relation.getProperty("difficulty"), resultEdge.property("difficulty"));
            });
        }

    }
}
