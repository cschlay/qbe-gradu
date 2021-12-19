package tabular.parser;

import core.exceptions.SyntaxError;
import core.graphs.QueryGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularParser;

import static org.junit.jupiter.api.Assertions.*;

class TabularEdgeParserTest {
    @Test
    @DisplayName("should parse one column")
    void parseOneColumn() throws Exception {
        String query =
                ""
                        + "| teaches.Course.Topic.fullTime |\n"
                        + "|-------------------------------|\n"
                        + "| false                         |\n";

        var graph = parseQuery(query);
        var edge = graph.get("Course").findEdge("teaches");
        assert edge != null;

        var property = edge.properties.get("fullTime");
        assertEquals(false, property.value);
    }

    @Test
    @DisplayName("should parse multiple columns")
    void parseMultipleColumns() throws Exception {
        String query =
                ""
                        + "| teaches.Course.Topic.fullTime | contains.Course.Topic.depth |\n"
                        + "|-------------------------------+-----------------------------|\n"
                        + "| true                          | 3                           |\n";

        var graph = parseQuery(query);

        var course = graph.get("Course");
        assertEquals(0, course.properties.size());
        assertEquals(2, course.edges.size());

        var topic = graph.get("Topic");
        assertEquals(0, topic.properties.size());
        assertEquals(2, topic.edges.size());

        var teaches = course.findEdge("teaches");
        assert teaches != null;
        assertEquals(true, teaches.properties.get("fullTime").value);

        var contains = course.findEdge("contains");
        assert contains != null;
        assertEquals(3, contains.properties.get("depth").value);
    }

    @Test
    @DisplayName("should parse short notation")
    void parseShortNotation() throws Exception {
        fail("NI");
    }

    @Test
    @DisplayName("should parse with anonymous tailNode")
    void parseAnonymousTailNode() throws Exception {
        fail("NI");
    }

    @Test
    @DisplayName("should parse anonymous headNode")
    void parseAnonymousHeadNode() throws Exception {
        fail("NI");
    }

    // Helpers
    static QueryGraph parseQuery(String query) throws SyntaxError {
        var parser = new TabularParser();
        return parser.parse(query);
    }
}
