package writer;

import base.WriterBaseTest;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] - Node")
class WriteNodeTest extends WriterBaseTest {
    private static final String COURSE = "Course";

    @Test
    @DisplayName("Print Id")
    void printId() {
        var course = new TabularHeader(COURSE);
        var id = new TabularHeader(COURSE, "id*");
        QueryGraph query = setupQuery(course, id);

        var result = new ResultGraph();
        result.put(node(1, COURSE));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 1  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Null")
    void printNull() {
        var course = new TabularHeader(COURSE);
        var title = new TabularHeader(COURSE, "title*");
        QueryGraph query = setupQuery(course, title);

        var result = new ResultGraph().put(node(1, COURSE));
        var expected = "" +
                "| title |\n" +
                "|-------|\n" +
                "| null  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property")
    void printProperty() {
        var course = new TabularHeader(COURSE);
        var name = new TabularHeader(COURSE, "name*");
        QueryGraph query = setupQuery(course, name);

        var result = new ResultGraph();
        result.put((QbeNode) node(1, COURSE).addProperty("name", "Algebra"));

        var expected = "" +
                "| name      |\n" +
                "|-----------|\n" +
                "| \"Algebra\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print property using Alias")
    void printPropertyUsingAlias() {
        var course = new TabularHeader(COURSE);
        var name = new TabularHeader(COURSE, "name as title*");
        QueryGraph query = setupQuery(course, name);

        var result = new ResultGraph();
        result.put((QbeNode) node(1, COURSE).addProperty("name", "Algebra"));

        var expected = "" +
                "| title     |\n" +
                "|-----------|\n" +
                "| \"Algebra\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print multiple columns")
    void printMultipleColumns() {
        var title = new TabularHeader(COURSE, "title*");
        var difficulty = new TabularHeader(COURSE, "difficulty*");
        QueryGraph query = setupQuery(title, difficulty);

        var result = new ResultGraph();
        var node = (QbeNode) node(1, COURSE)
                .addProperty("title", "Logic")
                .addProperty("difficulty", 2);
        result.put(node);

        var expected = "" +
                "| title   | difficulty |\n" +
                "|---------+------------|\n" +
                "| \"Logic\" | 2          |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print selected columns")
    void printSelectedColumns() {
        var title = new TabularHeader(COURSE, "title*");
        var difficulty = new TabularHeader(COURSE, "difficulty");
        QueryGraph query = setupQuery(title, difficulty);

        var result = new ResultGraph();
        var node = (QbeNode) node(1, COURSE)
                .addProperty("title", "Logic")
                .addProperty("difficulty", 2);
        result.put(node);

        var expected = "" +
                "| title   |\n" +
                "|---------|\n" +
                "| \"Logic\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print multiple rows")
    void printMultipleRows() {
        var title = new TabularHeader(COURSE, "title*");
        QueryGraph query = setupQuery(title);

        var result = new ResultGraph();
        result.put((QbeNode) node(1, COURSE).addProperty("title", "Logic"));
        result.put((QbeNode) node(2, COURSE).addProperty("title", "Graph"));

        var expected = "" +
                "| title   |\n" +
                "|---------|\n" +
                "| \"Logic\" |\n" +
                "| \"Graph\" |\n";
        assertEquals(expected, execute(query, result));
    }
}
