package tabular.parser;

import core.graphs.GraphEntityOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TabularParserTest {
    TabularParser parser = new TabularParser();

    @Nested
    @DisplayName("Simple node (short notation)")
    class SimpleNodeTest {
        @Test
        void parseQuery() throws Exception {
            var query = "" +
                    "| Song  | title | length  |\n" +
                    "|-------+-------+---------|\n" +
                    "| QUERY |       | >= 20.3 |\n";
            var graph = parser.parse(query);
            var node = graph.get("Song");

            assertEquals(GraphEntityOperations.QUERY, node.operation);
            assertNotNull(node.getProperty("title"));
            assertNotNull(node.getProperty("length"));
        }

        @Test
        void parseCreate() {

        }

        @Test
        void parseInsert() {

        }

        @Test
        void parseUpdate() {

        }
    }

    @Nested
    @DisplayName("Simple edge (short notation)")
    class SimpleEdgeTest {

    }

    /*@Test
    @DisplayName("should parse a node and an edge")
    void parseNodeAndEdge() throws Exception {
        var query = "" +
                "| Book.title | uses.Course.Book.edition |\n" +
                "|------------+--------------------------|\n" +
                "| \"Logic\"  | 3                        |";
        var graph = parser.parse(query);

        var book = graph.get("Book");
        var bookUses = book.edges.get("uses");
        assertEquals("Logic", book.getProperty("title"));
        assertEquals(3, bookUses.getProperty("edition"));

        var course = graph.get("Course");
        var courseUses = course.edges.get("uses");
        assertEquals(3, courseUses.getProperty("edition"));

        assertEquals(bookUses, courseUses);
    }*/
}
