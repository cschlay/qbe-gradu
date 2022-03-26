package writer;

import base.WriterBaseTest;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] - Path")
class WritePathTest extends WriterBaseTest {
    @Test
    @DisplayName("Simple Path")
    void printSimplePath() throws Exception {
        var uses = new TabularHeader("uses.partial*");
        var courseName = new TabularHeader("Course.name*");
        var bookTitle = new TabularHeader("Book.title*");
        QueryGraph query = setupQuery(uses, courseName, bookTitle);

        var result = new ResultGraph();
        var courseNode =  (QbeNode) node(1, "Course").addProperty("name", "Algorithms");
        var bookNode = (QbeNode) node(2, "Book").addProperty("title", "Graph Theory");
        var edge =  (QbeEdge) edge(3, "uses", courseNode, bookNode).addProperty("partial", false);
        result.put(edge);

        var expected = "" +
                "| uses.partial | Course.name  | Book.title     |\n" +
                "|--------------+--------------+----------------|\n" +
                "| false        | \"Algorithms\" | \"Graph Theory\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Cycle")
    void printCycle() throws Exception {
        var uses = new TabularHeader("uses.partial*");
        var writtenFor = new TabularHeader("written_for.kind*");
        var courseName = new TabularHeader("Course.name*");
        var bookTitle = new TabularHeader("Book.title*");
        QueryGraph query = setupQuery(uses, writtenFor, courseName, bookTitle);

        var result = new ResultGraph();
        var courseNode =  (QbeNode) node(1, "Course").addProperty("name", "Algorithms");
        var bookNode = (QbeNode) node(2, "Book").addProperty("title", "Graph Theory");
        var edgeA =  (QbeEdge) edge(3, "uses", courseNode, bookNode).addProperty("partial", false);
        var edgeB =  (QbeEdge) edge(4, "written_for", bookNode, courseNode).addProperty("kind", 1);
        result.put(edgeA);
        result.put(edgeB);

        var expected = "" +
                "| uses.partial | written_for.kind | Course.name  | Book.title     |\n" +
                "|--------------+------------------+--------------+----------------|\n" +
                "| false        | 1                | \"Algorithms\" | \"Graph Theory\" |\n";
        assertEquals(expected, execute(query, result));
    }

    // TODO: multipath situation
}
