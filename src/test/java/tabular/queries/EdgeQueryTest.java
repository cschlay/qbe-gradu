package tabular.queries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tabular.TabularTestClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeQueryTest extends TabularTestClass {
    @Test
    @DisplayName("should query all edges using explicit notation")
    void queryAllEdgesExplicit() throws Exception {
        String query = "" +
                "| Course.id | teaches.Assistant.Course.monday |\n" +
                "|-----------+---------------------------------|\n" +
                "|           | true                            |\n";

        var graph = executeQuery(query);
        assertTrue(graph.order() > 0);
        graph.values().forEach(node -> {
            assertTrue(node.edges.size() > 0, "node has missing edge");
            node.edges.forEach(edge -> {
                assertEquals("teaches", edge.name);
                assertEquals(true, edge.properties.get("monday").value, "node doesn't have the property");
            });
        });
    }
}
