package queries;

import org.junit.Assert;
import org.junit.Test;

public class EdgeQueries extends QueryTest {
    @Test
    public void edgeBetweenTwoNodes() throws Exception {
        // Query all courses that contains any topic
        var graph = executeQuery(
                "<graph>",
                "   <node name=\"Course\" />",
                "   <node name=\"Topic\" />",
                "   <edge name=\"contains\" source=\"Course\" target=\"Topic\" />",
                "</graph>");
        print(graph);

        graph.forEach((id, node) -> {
            if ("Course".equals(node.name)) {
                Assert.assertFalse(node.edges.isEmpty());
                node.edges.forEach(edge -> {
                    Assert.assertEquals("Topic", edge.headNode.name);
                });
            }
        });
    }

    // Transitive edge
}
