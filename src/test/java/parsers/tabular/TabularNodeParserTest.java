package parsers.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QueryGraph;
import core.graphs.LogicalExpression;
import core.parsers.TabularParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TabularNodeParserTest {
    @Test
    public void shouldParseBooleanFalse() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| false |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        Assertions.assertEquals(false, property.value);
    }

    @Test
    public void shouldParseBooleanTrue() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| true |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        Assertions.assertEquals(true, property.value);
    }

    @Test
    public void shouldParseDouble() throws Exception {
        var query = "" +
                "| Course.averageGrade |\n" +
                "|---------------------|\n" +
                "| 3.59 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "averageGrade");
        Assertions.assertEquals(3.59, property.value);
    }

    @Test
    public void shouldParseInteger() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");
        Assertions.assertEquals(3, property.value);
    }

    @Test
    public void shouldParseString() throws Exception {
        var query = "" +
                "| Course.title                   |\n" +
                "|--------------------------------|\n" +
                "| \"Introduction to Algorithms\" |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "title");
        Assertions.assertEquals("Introduction to Algorithms", property.value);
    }

    // Special cases

    @Test
    public void shouldParseLogicalExpression() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| > 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");

        var expression = (LogicalExpression) property.value;
        assert expression != null;
        Assertions.assertEquals("> 3", expression.value);
    }

    @Test
    public void shouldParseMultipleProperties() throws Exception {
        var query = "" +
                "| Course.title           | Course.difficulty |\n" +
                "|------------------------+-------------------|\n" +
                "| \"Introduction to .*\" | 1                 |";
        var graph = parseQuery(query);
        Assertions.assertEquals(1, graph.order());
        Assertions.assertEquals(0, graph.size());

        var node = graph.get("Course");
        Assertions.assertEquals(2, node.properties.size());

        var title = getProperty(graph, "title").value;
        Assertions.assertEquals("Introduction to .*", title);

        var difficulty = getProperty(graph, "difficulty").value;
        Assertions.assertEquals(1, difficulty);
    }

    // Helpers

    private static QueryGraph parseQuery(String query) throws SyntaxError {
        var parser = new TabularParser();
        return parser.parse(query);
    }

    private static QbeData getProperty(QueryGraph graph, String propertyName) {
        var node = graph.get("Course");
        return node.properties.get(propertyName);
    }
}
