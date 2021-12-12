package parsers;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QueryGraph;
import core.parsers.TabularParser;
import org.junit.Assert;
import org.junit.Test;

public class TabularNodeTest {
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
        Assert.assertEquals(property.value, 3.59);
    }

    @Test
    public void shouldParseInteger() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| 3 |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "difficulty");
        Assert.assertEquals(property.value, 3);
    }

    @Test
    public void shouldParseString() throws Exception {
        var query = "" +
                "| Course.title                   |\n" +
                "|--------------------------------|\n" +
                "| \"Introduction to Algorithms\" |";
        var graph = parseQuery(query);
        var property = getProperty(graph, "title");
        Assert.assertEquals(property.value, "Introduction to Algorithms");
    }

    private static QueryGraph parseQuery(String query) throws SyntaxError {
        var parser = new TabularParser();
        return parser.parse(query);
    }

    private static QbeData getProperty(QueryGraph graph, String propertyName) {
        var node = graph.get("Course");
        return node.properties.get(propertyName);
    }
}
