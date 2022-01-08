package tabular.queries.node;

import base.QueryBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.*;

class ReadTest extends QueryBaseTest {
    @Test
    @DisplayName("should find by id")
    void findById() throws Exception {
        tx.createNode();
        tx.createNode();
        var node = tx.createNode(Label.label("Book"));
        tx.commit();

        var id = String.valueOf(node.getId());
        var query = "" +
                "| Book.id* |\n" +
                "|----------|\n" +
                String.format("| %s       |", id);
        var graph = execute(query);
        assertEquals(1, graph.order());
        assertNotNull(graph.get(id));
    }

    @Test
    @DisplayName("should filter by name")
    void filterByName() throws Exception {
        tx.createNode(Label.label("Book"));
        tx.createNode(Label.label("Lecturer"));

        var label = Label.label("Course");
        tx.createNode(label);
        tx.createNode(label);
        tx.commit();

        var query = "" +
                "| Course.id* |\n" +
                "|------------|\n" +
                "|            |\n";

        var graph = execute(query);
        assertEquals(2, graph.order());
    }

    @Nested @DisplayName("filter by properties")
    class ByPropertyTest {
        @BeforeEach void setup() {
            try (var tx = db.beginTx()) {
                var label = Label.label("Book");
                var n1 = tx.createNode(label);
                n1.setProperty("title", "Logic");
                n1.setProperty("year", 2022);
                n1.setProperty("used", false);
                n1.setProperty("price", 20.99);

                var n2 = tx.createNode(label);
                n2.setProperty("title", "Graphs");
                n2.setProperty("year", 2019);
                n2.setProperty("used", true);
                n2.setProperty("price", 30.99);

                var n3 = tx.createNode(label);
                n3.setProperty("title", "Algebra");
                n3.setProperty("year", 2010);
                n3.setProperty("used", false);
                n3.setProperty("price", 70.99);

                tx.commit();
            }
        }

        @Test void byBoolean() throws Exception {
            var query = "" +
                    "| Book.used* |\n" +
                    "|------------|\n" +
                    "| false      |\n";
            var graph = execute(query);
            assertFalse(graph.isEmpty());
            graph.values().forEach(node -> assertEquals(false, node.getProperty("used")));
        }

        @Test void byDouble() throws Exception {
            var query = "" +
                    "| Book.price* |\n" +
                    "|-------------|\n" +
                    "| 20.99       |\n";
            var graph = execute(query);
            assertFalse(graph.isEmpty());
            graph.values().forEach(node -> assertEquals(20.99, node.getProperty("price")));
        }

        @Test void byInteger() throws Exception {
            var query = "" +
                    "| Book.year* |\n" +
                    "|------------|\n" +
                    "| 2022       |\n";
            var graph = execute(query);
            assertFalse(graph.isEmpty());
            graph.values().forEach(node -> assertEquals(2022, node.getProperty("year")));
        }

        @Test void byString() throws Exception {
            var query = "" +
                    "| Book.title* |\n" +
                    "|-------------|\n" +
                    "| \"Alg.*\"   |\n";
            var graph = execute(query);
            assertFalse(graph.isEmpty());
            graph.values().forEach(node -> assertEquals("Algebra", node.getProperty("title")));
        }

        @Test void byLogicalExpression() throws Exception {
            var query = "" +
                    "| Book.price*          |\n" +
                    "|----------------------|\n" +
                    "| <= 50.0              |\n";
            var graph = execute(query);
            assertFalse(graph.isEmpty());
            graph.values().forEach(node -> {
                var property = (Double) node.getProperty("price");
                assert property != null;
                assertTrue(property < 50.0);
            });
        }

        @Test void byMultipleProperties() throws Exception {
            var query = "" +
                    "| Book.used* | Book.title* | Book.year* | Book.price* |\n" +
                    "|------------+-------------+------------+-------------|\n" +
                    "| false      | \"Logic\"   | 2022       | 20.99       |\n";
            var graph = execute(query);
            assertEquals(1, graph.order());
        }
    }
}
