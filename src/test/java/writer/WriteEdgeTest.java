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
    private static final String tail = "Course";
    private static final String head = "Book";
    private static final String entity = "uses";

    @Test
    @DisplayName("Print Id")
    void printId() throws Exception {
        var uses = new TabularHeader(entity);
        var id = new TabularHeader(entity, "id*");
        QueryGraph query = setupQuery(uses, id);

        var result = new ResultGraph();
        result.put(edge(3, entity, node(1, tail), node(2, head)));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 3  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Null")
    void printNull() throws Exception {
        var uses = new TabularHeader(entity);
        var title = new TabularHeader(entity, "title*");
        QueryGraph query = setupQuery(uses, title);

        var result = new ResultGraph();
        result.put(edge(3, entity, node(1, tail), node(2, head)));

        var expected = "" +
                "| title |\n" +
                "|-------|\n" +
                "| null  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property")
    void printProperty() throws Exception {
        var uses = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "hidden*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        var edge = edge(3, entity, node(1, tail), node(2, head))
                .addProperty("hidden", true);
        result.put((QbeEdge) edge);

        var expected = "" +
                "| hidden |\n" +
                "|--------|\n" +
                "| true   |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property using Alias")
    void printPropertyUsingAlias() throws Exception {
        var course = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "kind as type*");
        QueryGraph query = setupQuery(course, kind);

        var result = new ResultGraph();
        var edge = edge(3, entity, node(1, tail), node(2, head))
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
        var uses = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "kind*");
        var partial = new TabularHeader(entity, "partial*");
        QueryGraph query = setupQuery(uses, kind, partial);

        var result = new ResultGraph();
        var edge = edge(3, entity, node(1, tail), node(2, head))
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
        var uses = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "kind*");
        var partial = new TabularHeader(entity, "partial");
        QueryGraph query = setupQuery(uses, kind, partial);

        var result = new ResultGraph();
        var edge = edge(3, entity, node(1, tail), node(2, head))
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
        var uses = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "hidden*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        var edgeA = (QbeEdge) edge(3, entity, node(1, tail), node(2, head))
                .addProperty("hidden", false);
        var edgeB = (QbeEdge) edge(4, entity, node(1, tail), node(2, head))
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
        var uses = new TabularHeader(entity);
        var kind = new TabularHeader(entity, "id*");
        QueryGraph query = setupQuery(uses, kind);

        var result = new ResultGraph();
        result.put(edge(2, entity, node(1, tail), node(1, head)));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 2  |\n";
        assertEquals(expected, execute(query, result));
    }
}
