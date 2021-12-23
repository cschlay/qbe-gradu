package tabular.queries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tabular.TabularTestClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeQueryTest extends TabularTestClass {
    @Test
    @DisplayName("should query all nodes of a kind")
    void queryAllNodes() throws Exception {
        var query = "" +
                "| Course.id as Id | Course.title as Title |\n" +
                "|-----------------+-----------------------|\n" +
                "|                 |                       |\n";

        var graph = executeQuery(query);
        assertTrue(graph.order() > 0);
        graph.values().forEach(node -> assertEquals("Course", node.name));
    }

    @Test
    @DisplayName("should filter nodes by property")
    void filterNodes() throws Exception {
        var query = "" +
                "| Course.title as Title |\n" +
                "|------------ ----------|\n" +
                "| \"Introduction to .*\"  |\n";

        var graph = executeQuery(query);
        assertTrue(graph.order() > 0);
        graph.values().forEach(node -> {
            var title = (String) node.properties.get("title").value;
            assertTrue(title != null && title.startsWith("Introduction to "));
        });
    }
}
