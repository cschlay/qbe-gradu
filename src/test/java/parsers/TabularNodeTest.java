package parsers;

import core.graphs.QueryGraph;
import core.parsers.TabularParser;
import org.junit.Assert;
import org.junit.Test;

public class TabularNodeTest {
    @Test
    public void shouldParseString() throws Exception {
        var query = "" +
                "| Course.title                   |\n" +
                "|--------------------------------|\n" +
                "| \"Introduction to Algorithms\" |";

        var parser = new TabularParser();
        QueryGraph graph = parser.parse(query);
        Assert.assertEquals(1, graph.order());
        Assert.assertEquals(0, graph.size());

        var node = graph.get("Course");
        Assert.assertEquals(1, node.properties.size());

        var property = node.properties.get("title");
        Assert.assertEquals(property.value, "Introduction to Algorithms");
    }

    @Test
    public void shouldParseInteger() throws Exception {
        var query = "" +
                "| Course.difficulty |\n" +
                "|-------------------|\n" +
                "| 3 |";

        var parser = new TabularParser();
        QueryGraph graph = parser.parse(query);

        var node = graph.get("Course");
        var property = node.properties.get("difficulty");
        Assert.assertEquals(property.value, 3);
    }

    @Test
    public void shouldParseDouble() throws Exception {
        var query = "" +
                "| Course.averageGrade |\n" +
                "|---------------------|\n" +
                "| 3.0 |";

        var parser = new TabularParser();
        QueryGraph graph = parser.parse(query);

        var node = graph.get("Course");
        var property = node.properties.get("averageGrade");
        Assert.assertEquals(property.value, 3.0);
    }

    // TODO: Boolean!
}
