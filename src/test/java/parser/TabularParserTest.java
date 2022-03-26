package parser;

import enums.QueryType;
import exceptions.SyntaxError;
import graphs.QueryGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularParser;
import syntax.tabular.TabularQueryMeta;
import utilities.Utils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Parser] Parse Queries")
class TabularParserTest {
    TabularParser parser = new TabularParser();

    @Nested
    @DisplayName("Simple node (short notation)")
    class SimpleNodeTest {
        @Test
        void parseIdProperty() throws Exception {
            var query = "" +
                    "| Song  | id |\n" +
                    "|-------+----|\n" +
                    "| QUERY | 2  |\n";
            var graph = Utils.first(parser.parse(query));
            var node = graph.get("Song");

            assertEquals(2, node.addProperty("id"));
            assertEquals("2", node.id);
        }

        @Test
        void parseQuery() throws Exception {
            var query = "" +
                    "| Song  | length  |\n" +
                    "|-------+---------|\n" +
                    "| QUERY | >= 20.3 |\n";
            var graph = Utils.first(parser.parse(query));
            var node = graph.get("Song");

            assertEquals(QueryType.QUERY, node.type);
            assertNotNull(node.addProperty("length"));
        }

        @Test
        void parseDelete() throws Exception {
            var query = "" +
                    "| Song   | title    |\n" +
                    "|--------+----------|\n" +
                    "| DELETE | \"Coin\" |\n";
            var graph = Utils.first(parser.parse(query));
            var node = graph.get("Song");

            assertEquals(QueryType.DELETE, node.type);
            assertEquals("Coin", node.addProperty("title"));
        }

        @Test
        void parseInsert() throws Exception {
            var query = "" +
                    "| Song   | title               | length |\n" +
                    "|--------+---------------------+--------|\n" +
                    "| INSERT | \"How to Solve It\" | 20.3   |\n";
            var graph = Utils.first(parser.parse(query));
            var node = graph.get("Song");

            assertEquals(QueryType.INSERT, node.type);
            assertEquals("How to Solve It", node.addProperty("title"));
            assertEquals(20.3, node.addProperty("length"));
        }
    }

    @Nested
    @DisplayName("Simple edge (short notation)")
    class SimpleEdgeTest {
        @Test
        void parseIdProperty() throws Exception {
            var query = "" +
                    "| composed          | id |\n" +
                    "|-------------------+----|\n" +
                    "| QUERY Artist.Song | 9  |\n";
            var graph = Utils.first(parser.parse(query));
            var song = graph.get("Song");
            var songComposed = song.edges.get("composed");

            var artist = graph.get("Artist");
            var artistComposed = artist.edges.get("composed");

            assertEquals(9, artistComposed.addProperty("id"));
            assertEquals("9", artistComposed.id);

            assertEquals(songComposed, artistComposed);
        }

        @Test
        void parseQuery() throws Exception {
            var query = "" +
                    "| composed          | hours  |\n" +
                    "|-------------------+--------|\n" +
                    "| QUERY Artist.Song | >= 80  |\n";
            var graph = Utils.first(parser.parse(query));
            var edge = graph.getEdge("Artist", "composed");
            assertEquals(QueryType.QUERY, edge.type);
            assertNotNull(edge.addProperty("hours"));
        }

        @Test
        void parseDelete() throws Exception {
            // Delete relations from Artist who are not active with the Song.
            var query = "" +
                    "| composed           | active |\n" +
                    "|--------------------+--------|\n" +
                    "| DELETE Artist.Song | false  |\n";
            var graph = Utils.first(parser.parse(query));
            var edge = graph.getEdge("Artist", "composed");
            assertEquals(QueryType.DELETE, edge.type);
            assertEquals(false, edge.addProperty("active"));
        }

        @Test
        void parseInsert() throws Exception {
            // Insert a relation of Artist-composed-Song.
            var query = "" +
                    "| Artist | Artist.id | Song  | Song.id | composed           | composed.active |\n" +
                    "|--------+-----------+-------+---------+--------------------+-----------------|\n" +
                    "| QUERY  | 1         | QUERY | 2       | INSERT Artist.Song | true            |\n";
            var graph = Utils.first(parser.parse(query));
            var artist = graph.get("Artist");
            assertEquals(QueryType.QUERY, artist.type);
            var song = graph.get("Song");
            assertEquals(QueryType.QUERY, song.type);

            var edge = artist.edges.get("composed");
            assertEquals(QueryType.INSERT, edge.type);
            assertEquals(true, edge.addProperty("active"));
        }
    }

    @Test
    @DisplayName("should parse a node and an edge")
    void parseNodeAndEdge() throws Exception {
        var query = "" +
                "| Book  | uses              | Book.title | uses.edition |\n" +
                "|-------+-------------------+---------------------------|\n" +
                "| QUERY | QUERY Course.Book | \"Logic\"  | 3            |\n";
        var graph = Utils.first(parser.parse(query));

        var book = graph.get("Book");
        var bookUses = book.edges.get("uses");
        assertEquals("Logic", book.addProperty("title"));
        assertEquals(3, bookUses.addProperty("edition"));

        var course = graph.get("Course");
        var courseUses = course.edges.get("uses");
        assertEquals(3, courseUses.addProperty("edition"));
        assertEquals(bookUses, courseUses);
    }
}
