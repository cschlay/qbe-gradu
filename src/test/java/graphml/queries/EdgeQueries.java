package graphml.queries;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            if ("Course".equals(node.name)) {
                Assertions.assertFalse(node.edges.isEmpty());
                node.edges.forEach(edge -> {
                    Assertions.assertEquals("Topic", edge.headNode.name);
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

        Assertions.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assertions.assertFalse(node.edges.isEmpty());
            node.edges.forEach(edge -> {
                Assertions.assertTrue((Boolean) edge.properties.get("monday").value);
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
        Assertions.assertFalse(graph.isEmpty());
        graph.forEach((id, node) -> {
            Assertions.assertFalse(node.edges.isEmpty());
            node.edges.forEach(edge ->{
                Assertions.assertEquals("teaches", edge.name);
                assert edge.tailNode != null;
                Assertions.assertTrue("Assistant".equals(edge.tailNode.name) || "Lecturer".equals(edge.tailNode.name));
            });
        });
    }

    @Test
    public void transitiveEdge() throws Exception {
        // Assistants who know about First-Order Logic by teaching it
        var graph = executeQuery("<graph>",
                "   <node name=\"Topic\">",
                "       <data key=\"title\">",
                "           First-Order Logic",
                "       </data>",
                "   </node>",
                "   <node name=\"Assistant\">",
                "       <data key=\"name\" />",
                "   </node>",
                "   <edge name=\"teaches\" source=\"Assistant\" target=\"Topic\" transitive=\"true\" />",
                "</graph>");
        print(graph);
    }

    // TODO: Anonymous edges, where name is null
    // TODO: Explicit tests where source or target is null
    // TODO: Everything is null!

}
