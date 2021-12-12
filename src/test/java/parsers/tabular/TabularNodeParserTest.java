package parsers.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QueryGraph;
import core.parsers.LogicalExpression;
import core.parsers.TabularParser;
import org.junit.Assert;
import org.junit.Test;

public class TabularNodeParserTest {
    @Test
    public void shouldParseBooleanFalse() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| false |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        Assert.assertEquals(false, property.value);
    }

    @Test
    public void shouldParseBooleanTrue() throws Exception {
        var query = "" +
                "| Course.graduateOnly |\n" +
                "|---------------------|\n" +
                "| true |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "graduateOnly");
        Assert.assertEquals(true, property.value);
    }

    @Test
    public void shouldParseDouble() throws Exception {
        var query = "" +
                "| Course.averageGrade |\n" +
                "|---------------------|\n" +
                "| 3.59 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "averageGrade");
        Assert.assertEquals(3.59, property.value);
    }

    @Test
    public void shouldParseInteger() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");
        Assert.assertEquals(3, property.value);
    }

    @Test
    public void shouldParseString() throws Exception {
        var query = "" +
                "| Course.title                   |\n" +
                "|--------------------------------|\n" +
                "| \"Introduction to Algorithms\" |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "title");
        Assert.assertEquals("Introduction to Algorithms", property.value);
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
        Assert.assertEquals("> 3", expression.value);
    }

    @Test
    public void shouldParseMultipleProperties() throws Exception {
        var query = "" +
                "| Course.title           | Course.difficulty |\n" +
                "|------------------------+-------------------|\n" +
                "| \"Introduction to .*\" | 1                 |";
        var graph = parseQuery(query);
        Assert.assertEquals(1, graph.order());
        Assert.assertEquals(0, graph.size());

        var node = graph.get("Course");
        Assert.assertEquals(2, node.properties.size());

        var title = getProperty(graph, "title").value;
        Assert.assertEquals("Introduction to .*", title);

        var difficulty = getProperty(graph, "difficulty").value;
        Assert.assertEquals(1, difficulty);
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
