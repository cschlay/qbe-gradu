package parser;

import enums.QueryType;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularParser;
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

            assertEquals(2, node.getProperty("id"));
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
            assertNotNull(node.getProperty("length"));
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
            assertEquals("Coin", node.getProperty("title"));
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
            assertEquals("How to Solve It", node.getProperty("title"));
            assertEquals(20.3, node.getProperty("length"));
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
            QueryGraph graph = Utils.first(parser.parse(query));
            QbeEdge songComposed = graph.get("Song").edges.get("composed", "Artist", "Song");
            QbeEdge artistComposed = graph.get("Artist").edges.get("composed", "Artist", "Song");

            assertEquals(9, artistComposed.getProperty("id"));
            assertEquals("9", artistComposed.id);

            assertEquals(songComposed, artistComposed);
        }

        @Test
        void parseQuery() throws Exception {
            var query = "" +
                    "| composed          | hours  |\n" +
                    "|-------------------+--------|\n" +
                    "| QUERY Artist.Song | >= 80  |\n";
            QueryGraph graph = Utils.first(parser.parse(query));
            QbeEdge edge = graph.get("Artist").edges.get("composed", "Artist", "Song");
            assertEquals(QueryType.QUERY, edge.type);
            assertNotNull(edge.getProperty("hours"));
        }

        @Test
        void parseDelete() throws Exception {
            // Delete relations from Artist who are not active with the Song.
            var query = "" +
                    "| composed           | active |\n" +
                    "|--------------------+--------|\n" +
                    "| DELETE Artist.Song | false  |\n";
            QueryGraph graph = Utils.first(parser.parse(query));
            QbeEdge edge = graph.get("Artist").edges.get("composed", "Artist", "Song");
            assertEquals(QueryType.DELETE, edge.type);
            assertEquals(false, edge.getProperty("active"));
        }

        @Test
        void parseInsert() throws Exception {
            // Insert a relation of Artist-composed-Song.
            var query = "" +
                    "| Artist | Artist.id | Song  | Song.id | composed           | composed.active |\n" +
                    "|--------+-----------+-------+---------+--------------------+-----------------|\n" +
                    "| QUERY  | 1         | QUERY | 2       | INSERT Artist.Song | true            |\n";
            QueryGraph graph = Utils.first(parser.parse(query));
            QbeNode artist = graph.get("Artist");
            assertEquals(QueryType.QUERY, artist.type);
            QbeNode song = graph.get("Song");
            assertEquals(QueryType.QUERY, song.type);

            QbeEdge edge = artist.edges.get("composed", "Artist", "Song");
            assertEquals(QueryType.INSERT, edge.type);
            assertEquals(true, edge.getProperty("active"));
        }
    }

    @Test
    @DisplayName("should parse a node and an edge")
    void parseNodeAndEdge() throws Exception {
        var query = "" +
                "| Book  | uses              | Book.title | uses.edition |\n" +
                "|-------+-------------------+---------------------------|\n" +
                "| QUERY | QUERY Course.Book | \"Logic\"  | 3            |\n";
        QueryGraph graph = Utils.first(parser.parse(query));

        QbeNode book = graph.get("Book");
        QbeEdge bookUses = book.edges.get("uses", "Course", "Book");
        assertEquals("Logic", book.getProperty("title"));
        assertEquals(3, bookUses.getProperty("edition"));

        QbeNode course = graph.get("Course");
        QbeEdge courseUses = course.edges.get("uses", "Course", "Book");
        assertEquals(3, courseUses.getProperty("edition"));
        assertEquals(bookUses, courseUses);
    }
}
