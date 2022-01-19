package tabular.queries.edge;

import base.QueryBaseTest;
import core.graphs.QbeEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class EdgeQueryTest extends QueryBaseTest {
    @Test
    void findById() throws Exception {
        var n1 = tx.createNode(Label.label("Book"));
        var n2 = tx.createNode(Label.label("Topic"));
        n1.createRelationshipTo(n2, RelationshipType.withName("contains"));
        var e = n1.createRelationshipTo(n2, RelationshipType.withName("contains"));
        tx.commit();

        var id = String.valueOf(e.getId());
        var query = "" +
                "| contains | id* |\n" +
                "|----------+-----|\n" +
                String.format("| QUERY Book.Topic | %s |\n", id);
        assertQuery(query, edge -> assertEquals(id, edge.id));
    }

    @Test
    void filterByName() throws Exception {
        var n1 = tx.createNode(Label.label("Book"));
        var n2 = tx.createNode(Label.label("Topic"));
        var n3 = tx.createNode(Label.label("Store"));
        n1.createRelationshipTo(n2, RelationshipType.withName("contains"));
        n1.createRelationshipTo(n3, RelationshipType.withName("sold_by"));
        tx.commit();

        var query = "" +
                "| sold_by          | id* |\n" +
                "|------------------+-----|\n" +
                "| QUERY Book.Store |     |\n";
        assertQuery(query, edge -> assertEquals("sold_by", edge.name));
    }

    @Test
    void ensureDirection() throws Exception {
        var n1 = tx.createNode(Label.label("Book"));
        var n2 = tx.createNode(Label.label("Topic"));
        var e1 = n1.createRelationshipTo(n2, RelationshipType.withName("contains"));
        var e2 = n2.createRelationshipTo(n1, RelationshipType.withName("contains"));
        tx.commit();

        var q1 = "" +
                "| contains         | id* |\n" +
                "|------------------+-----|\n" +
                "| QUERY Book.Topic |     |\n";
        assertQuery(q1, edge -> assertEquals(String.valueOf(e1.getId()), edge.id));

        var q2 = "" +
                "| contains          | id* |\n" +
                "|-------------------------|\n" +
                "|  QUERY Topic.Book |     |\n";
        assertQuery(q2, edge -> assertEquals(String.valueOf(e2.getId()), edge.id));

    }

    @Nested
    class ByPropertyTest {
        @BeforeEach
        void setup() {
            try (var tx = db.beginTx()) {
                var n1 = tx.createNode(Label.label("Author"));
                var n2 = tx.createNode(Label.label("Book"));
                var e1 = n1.createRelationshipTo(n2, RelationshipType.withName("writes"));
                e1.setProperty("code", "box");
                e1.setProperty("started", 2021);
                e1.setProperty("reviewed", false);
                e1.setProperty("hours", 39.40);

                var n3 = tx.createNode(Label.label("Author"));
                var n4 = tx.createNode(Label.label("Book"));
                var e2 = n3.createRelationshipTo(n4, RelationshipType.withName("writes"));
                e2.setProperty("code", "thread");
                e2.setProperty("started", 2020);
                e2.setProperty("reviewed", true);
                e2.setProperty("hours", 200.0);
                tx.commit();
            }
        }

        @Test
        void byBoolean() throws Exception {
            var query = "" +
                    "| writes            | reviewed* |\n" +
                    "|-------------------+-----------|\n" +
                    "| QUERY Author.Book | false     |\n";
            assertQuery(query, edge -> assertEquals(false, edge.getProperty("reviewed")));
        }

        @Test
        void byDouble() throws Exception {
            var query = "" +
                    "| writes            | hours* |\n" +
                    "|-------------------+--------|\n" +
                    "| QUERY Author.Book | 200.0  |\n";
            assertQuery(query, edge -> assertEquals(200.0, edge.getProperty("hours")));
        }

        @Test
        void byInteger() throws Exception {
            var query = "" +
                    "| writes            |started* |\n" +
                    "|-------------------+---------|\n" +
                    "| QUERY Author.Book | 2021    |\n";
            assertQuery(query, edge -> assertEquals(2021, edge.getProperty("started")));
        }

        @Test
        void byString() throws Exception {
            var query = "" +
                    "| writes            | code*   |\n" +
                    "|-------------------+---------|\n" +
                    "| QUERY Author.Book | \"box\" |\n";
            assertQuery(query, edge -> assertEquals("box", edge.getProperty("code")));
        }

        @Test
        void byLogicalExpression() throws Exception {
            var query = "" +
                    "| writes            | started* |\n" +
                    "|-------------------+----------|\n" +
                    "| QUERY Author.Book | >= 2019  |\n";
            assertQuery(query, edge -> {
                var property = edge.getProperty("started");
                assert property != null;
                assertTrue((int) property >= 2019);
            });
        }
    }

    private void assertQuery(String query, Consumer<QbeEdge> assertion) throws Exception {
        var graph = execute(query);
        assertFalse(graph.isEmpty(), "Graph is empty");

        for (var node : graph.values()) {
            assertFalse(node.edges.isEmpty());
            for (var edge : node.edges.values()) {
                assertion.accept(edge);
            }
        }
    }
}
