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

        Assert.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            if ("Course".equals(node.name)) {
                Assert.assertFalse(node.edges.isEmpty());
                node.edges.forEach(edge -> {
                    Assert.assertEquals("Topic", edge.headNode.name);
                });
            }
        });
    }

    @Test
    public void edgeWithProperties() throws Exception {
        // Assistants who teach on mondays
        var graph = executeQuery(
                "<graph>",
                "   <node name=\"Assistant\">",
                "       <data key=\"name\" />",
                "   </node>",
                "   <edge name=\"teaches\" source=\"Assistant\">",
                "       <data key=\"monday\" type=\"boolean\">",
                "           true",
                "       </data>",
                "   </edge>",
                "</graph>"
        );
        print(graph);

        Assert.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assert.assertFalse(node.edges.isEmpty());
            node.edges.forEach(edge -> {
                Assert.assertTrue((Boolean) edge.properties.get("monday").value);
            });
        });
    }

    @Test
    public void multipleEdges() throws Exception {
        // Courses that have assistants and lecturers
        var graph = executeQuery(
                "<graph>",
                "   <node name=\"Course\">",
                "       <data key=\"title\" />",
                "   </node>",
                "   <node name=\"Assistant\">",
                "       <data key=\"name\" />",
                "   </node>",
                "   <node name=\"Lecturer\">",
                "       <data key=\"name\" />",
                "   </node>",
                "   <edge name=\"teaches\" source=\"Assistant\" target=\"Course\" />",
                "   <edge name=\"teaches\" source=\"Lecturer\" target=\"Course\" />",
                "</graph>");
        print(graph);
        Assert.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assert.assertFalse(node.edges.isEmpty());
            node.edges.forEach(edge ->{
                Assert.assertEquals("teaches", edge.name);
                assert edge.tailNode != null;
                Assert.assertTrue("Assistant".equals(edge.tailNode.name) || "Lecturer".equals(edge.tailNode.name));
            });
        });
    }

    // TODO: Transitive edge
    // TODO: Anonymous edges
}
