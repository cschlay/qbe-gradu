package tabular;

import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularResultWriterTest {
    @Test
    @DisplayName("Should include id")
    void includeId() throws Exception {
        var graph = new ResultGraph();

        var node = new QbeNode(1, "Course");
        node.properties.put("title", new QbeData("Algebra"));
        graph.put(node.id, node);

        var headers = new String[] { "Course.id" };
        var expected = "" +
                "| Course.id |\n" +
                "|-----------|\n" +
                "| 1         |\n";
        assertEquals(expected, graph.toTabularString(headers));
    }

    @Test
    @DisplayName("Should write renamed header")
    void renameHeader() throws Exception {
        var graph = new ResultGraph();

        var node = new QbeNode(1, "Course");
        node.properties.put("title", new QbeData("Introduction to Logic"));
        graph.put(node.id, node);

        var headers = new String[] { "Course.title as Title" };
        var expected = "" +
                "| Title                   |\n" +
                "|-------------------------|\n" +
                "| \"Introduction to Logic\" |\n";
        assertEquals(expected, graph.toTabularString(headers));
    }

    @Test
    @DisplayName("Should write multiple columns")
    void writeMultipleColumns() throws Exception {
        var graph = new ResultGraph();

        var node = new QbeNode(1, "Course");
        node.properties.put("title", new QbeData("Logic"));
        node.properties.put("difficulty", new QbeData(2));
        graph.put(node.id, node);

        var headers = new String[] { "Course.title", "Course.difficulty" };
        var expected = "" +
                "| Course.title | Course.difficulty |\n" +
                "|--------------+-------------------|\n" +
                "| \"Logic\"      | 2                 |\n";
        assertEquals(expected, graph.toTabularString(headers));
    }

    @Test
    @DisplayName("Should write multiple columns")
    void writeMultipleRows() throws Exception {
        var graph = new ResultGraph();

        var node1 = new QbeNode(1, "Course");
        node1.properties.put("title", new QbeData("Logic"));
        graph.put(node1.id, node1);

        var node2 = new QbeNode(2, "Course");
        node2.properties.put("title", new QbeData("Graph"));
        graph.put(node2.id, node2);

        var headers = new String[] { "Course.title" };
        var expected = "" +
                "| Course.title |\n" +
                "|--------------|\n" +
                "| \"Logic\"      |\n" +
                "| \"Graph\"      |\n";
        assertEquals(expected, graph.toTabularString(headers));
    }
}
