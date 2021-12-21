package graphml.queries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

@Disabled public class HiddenEntityQueries extends QueryTest {
    @Test
    public void hiddenNode() throws Exception {
        var graph = executeQuery("<graph>",
                "<node name=\"Course\" hidden=\"true\" />",
                "<node name=\"Lecturer\" />",
                "</graph>");
        print(graph);
        Assertions.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assertions.assertEquals("Lecturer", node.name);
        });
    }

    @Test
    public void hiddenEdge() throws Exception {
        var graph = executeQuery("<graph>",
                "<node name=\"Course\" />",
                "<node name=\"Lecturer\" />",
                "<edge name=\"teaches\" source=\"Lecturer\" target=\"Course\" hidden=\"true\" />",
                "</graph>"
                );
        print(graph);
        AtomicBoolean hasLecturerNode = new AtomicBoolean(false);
        graph.forEach((id, node) -> {
            if ("Lecturer".equals(node.name)) {
                hasLecturerNode.set(true);
                node.edges.values().forEach(edge -> Assertions.assertNotEquals("teaches", edge.name));
            }
        });
        Assertions.assertTrue(hasLecturerNode.get());
    }

    @Test
    public void hiddenProperty() throws Exception {
        var graph = executeQuery("<graph>",
                "<node name=\"Lecturer\">",
                "   <data key=\"name\" />",
                "   <data key=\"title\" hidden=\"true\">Professor</data>",
                "</node>",
                "</graph>");
        print(graph);
        Assertions.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assertions.assertFalse(node.properties.containsKey("title"));
        });
    }
}
