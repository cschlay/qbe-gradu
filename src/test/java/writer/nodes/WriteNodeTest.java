package writer.nodes;

import base.WriterBaseTest;
import exceptions.SyntaxError;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] - Node")
class WriteNodeTest extends WriterBaseTest {
    @Test
    @DisplayName("Print Id")
    void printId() throws SyntaxError {
        var course = new TabularHeader("Course");
        var id = new TabularHeader("Course", "id*");
        QueryGraph query = setupQuery(course, id);

        var result = new ResultGraph();
        result.put(createNode(1));

        var expected = "" +
                "| id |\n" +
                "|----|\n" +
                "| 1  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Null")
    void printNull() throws SyntaxError {
        var course = new TabularHeader("Course");
        var id = new TabularHeader("Course", "title*");
        QueryGraph query = setupQuery(course, id);

        var result = new ResultGraph().put(createNode(1));
        var expected = "" +
                "| title |\n" +
                "|-------|\n" +
                "| null  |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print Property")
    void printProperty() throws SyntaxError {
        var course = new TabularHeader("Course");
        var name = new TabularHeader("Course", "name*");
        QueryGraph query = setupQuery(course, name);

        var result = new ResultGraph();
        result.put((QbeNode) createNode(1).addProperty("name", "Algebra"));

        var expected = "" +
                "| name      |\n" +
                "|-----------|\n" +
                "| \"Algebra\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print property using Alias")
    void printPropertyUsingAlias() throws SyntaxError {
        var course = new TabularHeader("Course");
        var name = new TabularHeader("Course", "name as title*");
        QueryGraph query = setupQuery(course, name);

        var result = new ResultGraph();
        result.put((QbeNode) createNode(1).addProperty("name", "Algebra"));

        var expected = "" +
                "| title     |\n" +
                "|-----------|\n" +
                "| \"Algebra\" |\n";
        assertEquals(expected, execute(query, result));
    }

    @Test
    @DisplayName("Print multiple columns")
    void printMultipleColumns() throws Exception {
        var title = new TabularHeader("Course", "title*");
        var difficulty = new TabularHeader("Course", "difficulty*");
        QueryGraph query = setupQuery(title, difficulty);

        var result = new ResultGraph();
        var node = (QbeNode) createNode(1)
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
    void printSelectedColumns() throws Exception {
        var title = new TabularHeader("Course", "title*");
        var difficulty = new TabularHeader("Course", "difficulty");
        QueryGraph query = setupQuery(title, difficulty);

        var result = new ResultGraph();
        var node = (QbeNode) createNode(1)
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
    void printMultipleRows() throws Exception {
        var title = new TabularHeader("Course", "title*");
        QueryGraph query = setupQuery(title);

        var result = new ResultGraph();
        result.put((QbeNode) createNode(1).addProperty("title", "Logic"));
        result.put((QbeNode) createNode(2).addProperty("title", "Graph"));

        var expected = "" +
                "| title   |\n" +
                "|---------|\n" +
                "| \"Logic\" |\n" +
                "| \"Graph\" |\n";
        assertEquals(expected, execute(query, result));
    }
}
