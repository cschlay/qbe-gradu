package tabular.queries;

import base.QueryBaseResetEachTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

class QueryEdgeTest extends QueryBaseResetEachTest {
    @Test
    void ensureDirection() throws Exception {
        var fx = new Object() { String id1; String id2; };
        run(tx -> {
            Node n1 = tx.createNode(Label.label("Book"));
            Node n2 = tx.createNode(Label.label("Topic"));
            Relationship e1 = n1.createRelationshipTo(n2, RelationshipType.withName("contains"));
            Relationship e2 = n2.createRelationshipTo(n1, RelationshipType.withName("contains"));
            tx.commit();

            fx.id1 = String.valueOf(e1.getId());
            fx.id2 = String.valueOf(e2.getId());
        });

        var q1 = "" +
                "| contains         | id* |\n" +
                "|------------------+-----|\n" +
                "| QUERY Book.Topic |     |\n";
        eachEdge(execute(q1), (tx, edge) -> assertEquals(fx.id1, edge.id));

        var q2 = "" +
                "| contains         | id* |\n" +
                "|------------------+-----|\n" +
                "| QUERY Topic.Book |     |\n";
        eachEdge(execute(q2), (tx, edge) -> assertEquals(fx.id2, edge.id));
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
            eachEdge(execute(query), (tx, edge) -> assertEquals(false, edge.property("reviewed")));
        }

        @Test
        void byDouble() throws Exception {
            var query = "" +
                    "| writes            | hours* |\n" +
                    "|-------------------+--------|\n" +
                    "| QUERY Author.Book | 200.0  |\n";
            eachEdge(execute(query), (tx, edge) -> assertEquals(200.0, edge.property("hours")));
        }

        @Test
        void byInteger() throws Exception {
            var query = "" +
                    "| writes            |started* |\n" +
                    "|-------------------+---------|\n" +
                    "| QUERY Author.Book | 2021    |\n";
            eachEdge(execute(query), (tx, edge) -> assertEquals(2021, edge.property("started")));
        }

        @Test
        void byString() throws Exception {
            var query = "" +
                    "| writes            | code*   |\n" +
                    "|-------------------+---------|\n" +
                    "| QUERY Author.Book | \"box\" |\n";
            eachEdge(execute(query), (tx, edge) -> assertEquals("box", edge.property("code")));
        }

        @Test
        void byLogicalExpression() throws Exception {
            var query = "" +
                    "| writes            | started* |\n" +
                    "|-------------------+----------|\n" +
                    "| QUERY Author.Book | >= 2019  |\n";
            eachEdge(execute(query), (tx, edge) -> {
                var property = edge.property("started");
                assert property != null;
                assertTrue((int) property >= 2019);
            });
        }
    }
}
