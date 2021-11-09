package queries;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class HiddenEntityQueries extends QueryTest {
    @Test
    public void hiddenNode() throws Exception {
        var graph = executeQuery("<graph>",
                "<node name=\"Course\" hidden=\"true\" />",
                "<node name=\"Lecturer\" />",
                "</graph>");
        print(graph);
        Assert.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assert.assertEquals("Lecturer", node.name);
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
                node.edges.forEach(edge -> Assert.assertNotEquals("teaches", edge.name));
            }
        });
        Assert.assertTrue(hasLecturerNode.get());
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
        Assert.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assert.assertFalse(node.properties.containsKey("title"));
        });
    }
}
