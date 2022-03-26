package writer;

import base.WriterBaseTest;
import graphs.QbeEdge;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] - Edge")
class WriteEdgeTest extends WriterBaseTest {
    private static final String COURSE = "Course";
    private static final String BOOK = "Book";
    private static final String USES = "uses";

    @Test
    @DisplayName("Print Id")
    void printId() throws Exception {
        var uses = new TabularHeader(USES);
        var id = new TabularHeader(USES, "id*");
        QueryGraph query = setupQuery(uses, id);

        var result = new ResultGraph();
        result.put(edge(3, USES, node(1, COURSE), node(2, BOOK)));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 3  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Null")
    void printNull() throws Exception {
        var uses = new TabularHeader(USES);
        var id = new TabularHeader(USES, "id*");
        var title = new TabularHeader(USES, "title*");
        QueryGraph query = setupQuery(uses, id, title);

        var result = new ResultGraph();
        result.put(edge(3, USES, node(1, COURSE), node(2, BOOK)));

        var expected = "" +
                "| id | title |\n" +
                "|----+-------|\n" +
                "| 3  | null  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property")
    void printProperty() throws Exception {
        var uses = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "hidden*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        var edge = (QbeEdge) edge(3, USES, node(1, COURSE), node(2, BOOK))
                .addProperty("hidden", true);
        result.put(edge);

        var expected = "" +
                "| hidden |\n" +
                "|--------|\n" +
                "| true   |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property using Alias")
    void printPropertyUsingAlias() throws Exception {
        var course = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "kind as type*");
        QueryGraph query = setupQuery(course, kind);

        var result = new ResultGraph();
        var edge = edge(3, USES, node(1, COURSE), node(2, BOOK))
                .addProperty("kind", 1);
        result.put((QbeEdge) edge);

        var expected = "" +
                "| type |\n" +
                "|------|\n" +
                "| 1    |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Multiple Columns")
    void printMultipleColumns() throws Exception {
        var uses = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "kind*");
        var partial = new TabularHeader(USES, "partial*");
        QueryGraph query = setupQuery(uses, kind, partial);

        var result = new ResultGraph();
        var edge = edge(3, USES, node(1, COURSE), node(2, BOOK))
                .addProperty("kind", 1)
                .addProperty("partial", false);
        result.put((QbeEdge) edge);

        var expected = "" +
                "| kind | partial |\n" +
                "|------+---------|\n" +
                "| 1    | false   |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Selected Columns")
    void printSelectedColumns() throws Exception {
        var uses = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "kind*");
        var partial = new TabularHeader(USES, "partial");
        QueryGraph query = setupQuery(uses, kind, partial);

        var result = new ResultGraph();
        var edge = edge(3, USES, node(1, COURSE), node(2, BOOK))
                .addProperty("kind", 1)
                .addProperty("partial", false);
        result.put((QbeEdge) edge);

        var expected = "" +
                "| kind |\n" +
                "|------|\n" +
                "| 1    |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Multiple Rows")
    void printMultipleRows() throws Exception {
        var uses = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "hidden*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        var tailNode = node(1, COURSE);
        var headNode = node(2, BOOK);
        var edgeA = (QbeEdge) edge(3, USES, tailNode, headNode)
                .addProperty("hidden", false);
        var edgeB = (QbeEdge) edge(4, USES, tailNode, headNode)
                .addProperty("hidden", true);
        result.put(edgeA);
        result.put(edgeB);

        var expected = "" +
                "| hidden |\n" +
                "|--------|\n" +
                "| false  |\n" +
                "| true   |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Loops")
    void printLoops() throws Exception {
        var uses = new TabularHeader(USES);
        var kind = new TabularHeader(USES, "id*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        result.put(edge(2, USES, node(1, COURSE), node(1, BOOK)));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 2  |\n";
        assertEquals(expected, execute(query, result));
    }
}
