package tabular;

import graphs.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularQueryMeta;
import syntax.tabular.TabularResultWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] TabularResultWriter Class")
class TabularResultWriterTest {
    TabularResultWriter writer = new TabularResultWriter();

    @Test
    void includeId() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(new String[] { "Course.id*" });

        var resultGraph = new ResultGraph();
        var node = new QbeNode(1, "Course");
        node.selected = true;
        node.properties.put("title", new QbeData("Algebra"));
        resultGraph.put(node.id, node);

        var expected = "" +
                "| Course.id |\n" +
                "|-----------|\n" +
                "| 1         |\n";
        assertEquals(expected, writer.write(queryGraph, resultGraph));
    }

    @Test
    @DisplayName("Should write renamed header")
    void renameHeader() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(new String[] { "Course.title as Title*" });

        var resultGraph = new ResultGraph();
        var node = new QbeNode(1, "Course");
        node.selected = true;
        node.properties.put("title", new QbeData("Introduction to Logic"));
        resultGraph.put(node.id, node);

        var expected = "" +
                "| Title                   |\n" +
                "|-------------------------|\n" +
                "| \"Introduction to Logic\" |\n";

        var writer = new TabularResultWriter();
        assertEquals(expected, writer.write(queryGraph, resultGraph));
    }

    @Test
    @DisplayName("Should write multiple columns")
    void writeMultipleColumns() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(new String[] { "Course.title*", "Course.difficulty*" });

        var resultGraph = new ResultGraph();
        var node = new QbeNode(1, "Course");
        node.selected = true;
        node.properties.put("title", new QbeData("Logic"));
        node.properties.put("difficulty", new QbeData(2));
        resultGraph.put(node.id, node);

        var expected = "" +
                "| Course.title | Course.difficulty |\n" +
                "|--------------+-------------------|\n" +
                "| \"Logic\"      | 2                 |\n";

        var writer = new TabularResultWriter();
        assertEquals(expected, writer.write(queryGraph, resultGraph));
    }

    @Test
    @DisplayName("Should write multiple columns")
    void writeMultipleRows() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(new String[] { "Course.title*" });

        var resultGraph = new ResultGraph();

        var node1 = new QbeNode(1, "Course");
        node1.selected = true;
        node1.properties.put("title", new QbeData("Logic"));
        resultGraph.put(node1.id, node1);

        var node2 = new QbeNode(2, "Course");
        node2.selected = true;
        node2.properties.put("title", new QbeData("Graph"));
        resultGraph.put(node2.id, node2);

        var expected = "" +
                "| Course.title |\n" +
                "|--------------|\n" +
                "| \"Logic\"      |\n" +
                "| \"Graph\"      |\n";

        var writer = new TabularResultWriter();
        assertEquals(expected, writer.write(queryGraph, resultGraph));
    }
}
