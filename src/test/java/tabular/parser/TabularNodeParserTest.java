package tabular.parser;

import core.exceptions.SyntaxError;
import core.graphs.GraphEntityOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import syntax.tabular.TabularParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TabularNodeParserTest {
    TabularParser parser = new TabularParser();

    @Nested
    @DisplayName("entity columns")
    class EntityColumnTest {
        @ParameterizedTest
        @EnumSource(GraphEntityOperations.class)
        @DisplayName("should parse CREATE, QUERY, UPDATE, and DELETE commands")
        void parseCommands(GraphEntityOperations command) throws Exception {
            var query = "" +
                    "| Book   |\n" +
                    "|--------|\n" +
                    String.format("| %s |\n", command.name());
            var graph = parser.parse(query);
            var node = graph.get("Book");
            assertEquals(command, node.type);
        }

        @Test
        @DisplayName("should throw SyntaxError if command is not valid")
        void syntaxErrors() {
            var query = "" +
                    "| Book |\n" +
                    "|------|\n" +
                    "| PUT  |\n";
            var exception = assertThrows(SyntaxError.class, () -> parser.parse(query));
            assertEquals("Invalid entity command PUT, should be CREATE, DELETE, UPDATE or QUERY", exception.getMessage());
        }
    }

    @Test
    @DisplayName("should parse one node")
    void parseOneNode() throws Exception {
        var query =
                ""
                        + "| Course.averageGrade |\n"
                        + "|---------------------|\n"
                        + "| 3.59                |";

        var graph = parser.parse(query);
        assertEquals(1, graph.order());
        assertEquals(1, graph.get("Course").properties.size());

        graph.values().forEach(node -> assertEquals(3.59, node.getProperty("averageGrade")));
    }

    @Test
    @DisplayName("should parse multiple nodes")
    void parseMultipleNodes() throws Exception {
        var query =
                ""
                        + "| Course.title                   | Book.title |\n"
                        + "|--------------------------------+------------|\n"
                        + "| \"Introduction to Algorithms\" | \"Graphs\" |";

        var graph = parser.parse(query);
        assertEquals(2, graph.order());
        assertEquals(1, graph.get("Book").properties.size());
        assertEquals(1, graph.get("Course").properties.size());
    }

    @Test
    @DisplayName("should parse multiple properties")
    void parseMultipleProperties() throws Exception {
        var query =
                ""
                        + "| Course.title           | Course.difficulty |\n"
                        + "|------------------------+-------------------|\n"
                        + "| \"Introduction to .*\" | 1                 |";

        var graph = parser.parse(query);
        assertEquals(1, graph.order());
        assertEquals(2, graph.get("Course").properties.size());
    }

    @Test
    @DisplayName("should parse multiple nodes and properties")
    void parseMultipleNodesAndProperties() throws Exception {
        var query =
                ""
                        + "| Course.title           | Course.difficulty | Book.title | Book.author |\n"
                        + "|------------------------+-------------------+------------+-------------|\n"
                        + "| \"Introduction to .*\" | 1                 | \"Graphs\" | \"Chess\"   |";

        var graph = parser.parse(query);
        assertEquals(2, graph.order());
        assertEquals(2, graph.get("Course").properties.size());
        assertEquals(2, graph.get("Book").properties.size());
    }
}
