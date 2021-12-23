package tabular.queries;

import demo.CourseGraphDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Relationship;
import tabular.TabularTestClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeQueryTest extends TabularTestClass {
    @Test
    @DisplayName("should query all edges using explicit notation")
    void queryAllEdgesExplicit() throws Exception {
        String query =
                ""
                        + "| Course.id | teaches.Assistant.Course.monday |\n"
                        + "|-----------+---------------------------------|\n"
                        + "|           | true                            |\n";

        var graph = executeQuery(query);
        System.out.println(graph);

        assertTrue(graph.order() > 0);

        for (var node : graph.values()) {
            assertEquals(1, node.edges.size());

            for (var edge : node.edges.values()) {
                assertEquals("teaches", edge.name);
                assertEquals(true, edge.properties.get("monday").value);
            }
        }
    }
}
