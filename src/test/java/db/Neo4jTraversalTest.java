package db;

import base.QueryBaseTest;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import db.neo4j.Neo4jTraversal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jTraversalTest extends QueryBaseTest {
    Neo4jTraversal traversal;

    @BeforeEach
    void setup() {
        traversal = new Neo4jTraversal(db);
    }

    @Test
    @DisplayName("should query edge with properties but nodes without")
    void queryEdgeWithProperties() {
        var tail = new QbeNode("Assistant");
        var head = new QbeNode("Course");
        var edge = new QbeEdge("teaches");
        edge.properties.put("monday", new QbeData(true));
        edge.tailNode = tail;
        edge.headNode = head;

        var queryGraph = new QueryGraph();
        queryGraph.put(tail.name, tail);
        queryGraph.put(head.name, head);
        queryGraph.put(edge);

        var resultGraph = traversal.executeQueryGraph(queryGraph);

        var courseExists = false;
        var assistantExists = false;
        assertFalse(resultGraph.isEmpty());
        for (var node : resultGraph.values()) {
            assertFalse(node.edges.isEmpty(), "node doesn't have edges");
            for (var resultEdge : node.edges.values()) {
                assertEquals("teaches", resultEdge.name);
            }

            if ("Course".equals(node.name)){
                courseExists = true;
            } else if ("Assistant".equals(node.name)) {
                assistantExists = true;
            }
        }

        assertTrue(assistantExists, "Assistant doesn't exist");
        assertTrue(courseExists, "Course doesn't exist");
    }

    @Test
    @DisplayName("should query both edge and nodes with properties")
    void queryNodeAndEdgeWithProperties()
    {

    }

    @Test
    @DisplayName("should query edge with head and tail")
    void queryEdgeWithHeadAndTail() {
        var tail = new QbeNode("Assistant");
        var head = new QbeNode("Course");
        var edge = new QbeEdge("teaches");
        edge.tailNode = tail;
        edge.headNode = head;

        var queryGraph = new QueryGraph();
        queryGraph.put(tail.name, tail);
        queryGraph.put(head.name, head);

        var resultGraph = traversal.executeQueryGraph(queryGraph);
        assertTrue(resultGraph.order() > 0);

        var courseExists = false;
        var assistantExists = false;
        for (var node : resultGraph.values()) {
            if ("Course".equals(node.name)) {
                courseExists = true;
            }
            else if ("Assistant".equals(node.name)) {
                assistantExists = true;
            }
            assertFalse(node.edges.isEmpty());
        }

        assertTrue(assistantExists);
        assertTrue(courseExists);
    }
}
