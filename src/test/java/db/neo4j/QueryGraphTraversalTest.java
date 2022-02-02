package db.neo4j;

import base.QueryBaseTest;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

class QueryGraphTraversalTest extends QueryBaseTest {
    static class Labels {
        public static Label book = Label.label("Book");
        public static Label course = Label.label("Course");
        public static Label topic = Label.label("Topic");
    }
    static class Relations {
        public static RelationshipType contains = RelationshipType.withName("contains");
        public static RelationshipType uses = RelationshipType.withName("uses");
    }

    @Test
    void simplePath() throws Exception {
        // Database contains two path fragments and one full path.
        // The result should yield the full path.
        var fx = new Object() { Node book; Node course; Node topic; };
        run(tx -> {
            var f1Book = tx.createNode(Labels.book);
            var f1Course = tx.createNode(Labels.course);
            f1Course.createRelationshipTo(f1Book, Relations.uses);

            fx.course = tx.createNode(Labels.course);
            fx.book = tx.createNode(Labels.book);
            fx.topic = tx.createNode(Labels.topic);
            fx.course.createRelationshipTo(fx.book, Relations.uses);
            fx.book.createRelationshipTo(fx.topic, Relations.contains);

            var f2Book = tx.createNode(Labels.book);
            var f2Topic = tx.createNode(Labels.topic);
            f2Book.createRelationshipTo(f2Topic, Relations.contains);
            tx.commit();
        });

        var book = new QbeNode("Book");
        var course = new QbeNode("Course");
        var topic = new QbeNode("Topic");

        var e1 = new QbeEdge("uses");
        e1.tailNode = course;
        e1.headNode = book;

        var e2 = new QbeEdge("contains");
        e2.tailNode = book;
        e2.headNode = topic;

        var queryGraph = new QueryGraph();
        queryGraph.put(e1);
        queryGraph.put(e2);

        run(tx -> {
            var tr = new QueryGraphTraversal(tx);
            var resultGraph = tr.traverse(queryGraph);
            assertEquals(3, resultGraph.order());
            assertTrue(resultGraph.containsKey(String.valueOf(fx.course.getId())));
            assertTrue(resultGraph.containsKey(String.valueOf(fx.book.getId())));
            assertTrue(resultGraph.containsKey(String.valueOf(fx.topic.getId())));
        });
    }

    @Test
    void simpleCycle() throws Exception {
        // Query graph has cycle
    }

    /**
     * Db: (v1, e1, v2) and (v2, e2, v3) without (e1, v2, e2)
     * Query: (v1, e1, v2, e2, v3)
     * Result: None
     */
    @Test
    void excludePartialPaths() throws Exception {
        run(tx -> {
            var v1 = tx.createNode(Label.label("v1"));
            var v2a = tx.createNode(Label.label("v2"));
            var v2b = tx.createNode(Label.label("v2"));
            var v3 = tx.createNode(Label.label("v3"));

            v1.createRelationshipTo(v2a, RelationshipType.withName("e1"));
            v2b.createRelationshipTo(v3, RelationshipType.withName("e2"));
            tx.commit();
        });

        var v1 = new QbeNode("v1");
        var v2 = new QbeNode("v2");
        var v3 = new QbeNode("v3");

        var e1 = new QbeEdge("e1");
        e1.tailNode = v1;
        e1.headNode = v2;

        var e2 = new QbeEdge("e2");
        e2.tailNode = v2;
        e2.headNode = v3;

        var queryGraph = new QueryGraph();
        queryGraph.put(v1);
        queryGraph.put(v2);
        queryGraph.put(v3);
        queryGraph.put(e1);
        queryGraph.put(e2);

        run(tx -> {
           var tr = new QueryGraphTraversal(tx);
           var resultGraph = tr.traverse(queryGraph);
           assertTrue(resultGraph.isEmpty(), "Graph is not empty!");
        });
    }
}
