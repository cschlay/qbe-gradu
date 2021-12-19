package tabular.parser;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QueryGraph;
import core.graphs.LogicalExpression;
import syntax.tabular.TabularParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularNodeParserTest {
    // TODO: value parse belong to a separate class.

    @Test
    void shouldParseBooleanFalse() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| false |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        assertEquals(false, property.value);
    }

    @Test
    void shouldParseBooleanTrue() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| true |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        assertEquals(true, property.value);
    }

    @Test
    void shouldParseDouble() throws Exception {
        var query = "" +
                "| Course.averageGrade |\n" +
                "|---------------------|\n" +
                "| 3.59 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "averageGrade");
        assertEquals(3.59, property.value);
    }

    @Test
    void shouldParseInteger() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");
        assertEquals(3, property.value);
    }

    @Test
    void shouldParseString() throws Exception {
        var query = "" +
                "| Course.title                   |\n" +
                "|--------------------------------|\n" +
                "| \"Introduction to Algorithms\" |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "title");
        assertEquals("Introduction to Algorithms", property.value);
    }

    // Special cases

    @Test
    void shouldParseLogicalExpression() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| > 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");

        var expression = (LogicalExpression) property.value;
        assert expression != null;
        assertEquals("> 3", expression.value);
    }

    @Test
    void shouldParseMultipleProperties() throws Exception {
        var query = "" +
                "| Course.title           | Course.difficulty |\n" +
                "|------------------------+-------------------|\n" +
                "| \"Introduction to .*\" | 1                 |";
        var graph = parseQuery(query);
        assertEquals(1, graph.order());
        assertEquals(0, graph.size());

        var node = graph.get("Course");
        assertEquals(2, node.properties.size());

        var title = getProperty(graph, "title").value;
        assertEquals("Introduction to .*", title);

        var difficulty = getProperty(graph, "difficulty").value;
        assertEquals(1, difficulty);
    }

    // TODO: Multiple nodes

    // Helpers
    static QueryGraph parseQuery(String query) throws SyntaxError {
        var parser = new TabularParser();
        return parser.parse(query);
    }

    static QbeData getProperty(QueryGraph graph, String propertyName) {
        var node = graph.get("Course");
        return node.properties.get(propertyName);
    }
}
