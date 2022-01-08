package tabular.parser;

import org.junit.jupiter.api.DisplayName;
import syntax.tabular.TabularParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularNodeParserTest {
    TabularParser parser = new TabularParser();

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
