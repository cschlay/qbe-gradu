package queries.graphml;

import demo.CourseGraphDemo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

public class NodeQueries extends QueryTest {
    @Test
    public void singleNodeName() throws Exception {
        // Query all nodes that have name Course
        var graph = executeQuery("<graph><node name=\"Course\" /></graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        graph.forEach((id, node) -> Assertions.assertEquals(node.name, CourseGraphDemo.Labels.Course.toString()));
    }

    @Test
    public void multipleNodeNames() throws Exception {
        Label label1 = CourseGraphDemo.Labels.Course;
        Label label2 = CourseGraphDemo.Labels.Topic;

        // Query all nodes that name Course or Topic
        var graph = executeQuery(
                "<graph>",
                "   <node name=\"Course\" />",
                "   <node name=\"Topic\" />",
                "</graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        boolean containsLabel1 = false;
        boolean containsLabel2 = false;
        for (var node : graph.values()) {
            if (node.name != null) {
                if (node.name.equals(label1.toString())) {
                    containsLabel1 = true;
                }
                if (node.name.equals(label2.toString()))
                    containsLabel2 = true;
            }
        }

        Assertions.assertTrue(containsLabel1);
        Assertions.assertTrue(containsLabel2);
    }

    @Test
    public void attributeOnly() throws Exception {
        // Query all nodes that have property "title"
        var graph = executeQuery("<graph><node><data key=\"title\" /></node></graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        graph.forEach((id, node) -> Assertions.assertNotNull(node.properties.get("title")));
    }

    @Test
    public void numberProperty() throws Exception {
        var graph = executeQuery("<graph>",
                "<node name=\"Course\">",
                "   <data key=\"difficulty\" type=\"integer\">",
                "       3",
                "   </data>",
                "</node></graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        graph.forEach((id, node) -> {
            var property = node.properties.get("difficulty");
            Assertions.assertNotNull(property);
            Assertions.assertEquals(3, property.value);
        });
    }

    @Test
    public void numberPropertyWithConstraints() throws Exception {
        // Query courses that have difficulty rating greater than 3
        var graph = executeQuery("<graph>",
                "<node>",
                "   <data key=\"difficulty\" type=\"integer\">",
                "       <constraint type=\"gt\">",
                "           3",
                "       </constraint>",
                "   </data>",
                "</node></graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        graph.forEach((id, node) -> {
            var property = node.properties.get("difficulty");
            Assertions.assertNotNull(property);
            Assertions.assertEquals(4, property.value);
        });
    }
    // TODO: MORE CONSTRAINTS NEED TO BE IMPLEMENTED

    @Test
    public void regexQuery() throws Exception {
        // Get all "Introduction to ..." courses
        var graph = executeQuery("<graph>",
                "<node name=\"Course\">",
                "   <data key=\"title\">",
                "       \\bIntroduction to .*",
                "   </data>",
                "</node></graph>");
        print(graph);

        Assertions.assertTrue(graph.size() > 0);
        graph.forEach((id, node) -> {
            Assertions.assertEquals("Course", node.name);
            var title = node.properties.get("title").value;
            assert title != null;
            Assertions.assertTrue(((String) title).startsWith("Introduction to"));
        });
    }
}
