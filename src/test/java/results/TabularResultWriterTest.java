package results;

import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularResultWriterTest {
    @Test
    @DisplayName("Should")
    void shouldWriteRenamedHeader() throws Exception {
        var resultGraph = new ResultGraph();

        var node = new QbeNode("Course");
        node.properties.put("title", new QbeData("Introduction to Logic"));
        resultGraph.put("Course", node);
        var headers = new String[] { "Course.title as Title" };

        var expected = "" +
                "| Title                 |\n" +
                "|-----------------------|\n" +
                "| Introduction to Logic |";
        assertEquals(expected, resultGraph.toTabularString(headers));
    }
}
