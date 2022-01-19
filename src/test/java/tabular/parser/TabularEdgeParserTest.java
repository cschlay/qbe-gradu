package tabular.parser;

import core.graphs.QueryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import syntax.tabular.TabularParser;

import static org.junit.jupiter.api.Assertions.*;

class TabularEdgeParserTest {
    TabularParser parser = new TabularParser();

    @Nested
    @DisplayName("entity column")
    class EntityColumnTest {
        @ParameterizedTest
        @EnumSource(QueryType.class)
        @DisplayName("should parse CREATE, QUERY, UPDATE, and DELETE commands")
        void parseCommands(QueryType command) throws Exception {
            var query = "" +
                    "| contains |\n" +
                    "|----------|\n" +
                    String.format("| %s Book.Topic |\n", command.name());
            var graph = parser.parse(query);

            var n1 = graph.get("Book");
            var n2 = graph.get("Topic");
            var edge = n1.edges.get("contains");
            assertEquals(edge, n2.edges.get("contains"));
            assertEquals(command, edge.type);
            assertEquals(n1, edge.tailNode);
            assertEquals(n2, edge.headNode);
        }
    }

    @Test
    @DisplayName("should parse one column")
    void parseOneColumn() throws Exception {
        var query =
                ""
                        + "| teaches.Course.Topic.fullTime |\n"
                        + "|-------------------------------|\n"
                        + "| false                         |\n";

        var graph = parser.parse(query);
        var edge = graph.get("Course").edges.get("teaches");

        var property = edge.properties.get("fullTime");
        assertEquals(false, property.value);
    }

    @Test
    @DisplayName("should parse multiple edges")
    void parseMultipleEdges() throws Exception {
        var query =
                ""
                        + "| teaches.Course.Topic.fullTime | contains.Course.Topic.depth |\n"
                        + "|-------------------------------+-----------------------------|\n"
                        + "| true                          | 3                           |\n";

        var graph = parser.parse(query);

        var course = graph.get("Course");
        assertEquals(0, course.properties.size());
        assertEquals(2, course.edges.size());

        var topic = graph.get("Topic");
        assertEquals(0, topic.properties.size());
        assertEquals(2, topic.edges.size());

        var teaches = course.edges.get("teaches");
        assertEquals(true, teaches.properties.get("fullTime").value);

        var contains = course.edges.get("contains");
        assertEquals(3, contains.properties.get("depth").value);
    }

    @Test
    @DisplayName("should parse multiple property")
    void parseMultipleProperty() throws Exception {
        var query =
                ""
                        + "| teaches.Course.Topic.fullTime | teaches.Course.Topic.students |\n"
                        + "|-------------------------------+-------------------------------|\n"
                        + "| true                          | 20                            |\n";

        var graph = parser.parse(query);
        var course = graph.get("Course");
        var teaches = course.edges.get("teaches");
        assert teaches != null;

        assertEquals(true, teaches.properties.get("fullTime").value);
        assertEquals(20, teaches.properties.get("students").value);

        var topic = graph.get("Topic");
        assertEquals(teaches, topic.edges.get("teaches"));
    }

    @Test
    @DisplayName("should parse with anonymous tailNode")
    void parseAnonymousTailNode() throws Exception {
        var query = "" +
                "| teaches._.Topic.year |\n" +
                "|----------------------|\n" +
                "| 2022                 |";
        var graph = parser.parse(query);
        assertEquals(1, graph.order());

        var topic = graph.get("Topic");
        var teaches = topic.edges.get("teaches");
        assertNull(teaches.tailNode);
        assertEquals(2022, teaches.properties.get("year").value);
    }

    @Test
    @DisplayName("should parse anonymous headNode")
    void parseAnonymousHeadNode() throws Exception {
        var query = "" +
                "| teaches.Course._.year |\n" +
                "|-----------------------|\n" +
                "| 2022                  |";
        var graph = parser.parse(query);
        assertEquals(1, graph.order());

        var topic = graph.get("Course");
        var teaches = topic.edges.get("teaches");
        assertNull(teaches.headNode);
        assertEquals(2022, teaches.properties.get("year").value);
    }
}
