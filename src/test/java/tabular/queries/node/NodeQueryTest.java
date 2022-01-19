package tabular.queries.node;

import base.QueryBaseTest;
import core.graphs.QbeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class NodeQueryTest extends QueryBaseTest {
    @Test
    void findById() throws Exception {
        // Find a book by its id
        tx.createNode();
        var n = tx.createNode(Label.label("Book"));
        tx.commit();

        var id = String.valueOf(n.getId());
        var query = "" +
                "| Book | id* |\n" +
                "|------+-----|\n" +
                String.format("| QUERY | %s |\n", id);
        assertQuery(query, node -> assertEquals(id, node.id));
    }

    @Test
    void filterByName() throws Exception {
        // Find all nodes with name "Course"
        tx.createNode(Label.label("Book"));
        tx.createNode(Label.label("Course"));
        tx.commit();

        var query = "" +
                "| Course | id* |\n" +
                "|--------+-----|\n" +
                "| QUERY  |     |\n";
        assertQuery(query, node -> assertEquals("Course", node.name));
    }

    @Nested
    class ByPropertyTest {
        @BeforeEach
        void setup() {
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

        @Test
        void byBoolean() throws Exception {
            // Find all used non-used books
            var query = "" +
                    "| Book  | used* |\n" +
                    "|-------+-------|\n" +
                    "| QUERY | false |\n";
            assertQuery(query, node -> assertEquals(false, node.getProperty("used")));
        }

        @Test
        void byDouble() throws Exception {
            // Find all books with exact price of 20.99
            var query = "" +
                    "| Book  | price* |\n" +
                    "|-------+--------|\n" +
                    "| QUERY | 20.99  |\n";
            assertQuery(query, node -> assertEquals(20.99, node.getProperty("price")));
        }

        @Test
        void byInteger() throws Exception {
            // Find all books published in 2022
            var query = "" +
                    "| Book  | year* |\n" +
                    "|-------+-------|\n" +
                    "| QUERY | 2022  |\n";
            assertQuery(query, node -> assertEquals(2022, node.getProperty("year")));
        }

        @Test
        void byString() throws Exception {
            // Find all books where title starts with "Alg"
            var query = "" +
                    "| Book  | title*    |\n" +
                    "|-------+-----------|\n" +
                    "| QUERY | \"Alg.*\" |\n";
            assertQuery(query, node -> assertEquals("Algebra", node.getProperty("title")));
        }

        @Test
        void byLogicalExpression() throws Exception {
            // Find books that are cheaper than or equal to 50
            var query = "" +
                    "| Book  | price* |\n" +
                    "|-------+--------|\n" +
                    "| QUERY | < 50.0 |\n";
            assertQuery(query, node -> {
                var property = node.getProperty("price");
                assert property != null;
                assertTrue((double) property < 50.0);
            });
        }
    }

    private void assertQuery(String query, Consumer<QbeNode> assertion) throws Exception {
        var graph = execute(query);
        assertFalse(graph.isEmpty(), "Graph is empty");

        for (var node : graph.values()) {
            assertion.accept(node);
        }
    }
}
