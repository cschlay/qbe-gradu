package queries;

import demo.CourseGraphDemo;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Label;

public class NodeQueries extends QueryTest {
    @Test
    public void singleNodeName() throws Exception {
        // Query all nodes that have name Course
        var graph = session.processQuery(String.join("\n",
                "<graph>",
                String.format("<node name=\"%s\" />", CourseGraphDemo.Labels.Course),
                "</graph>"));
        print(graph);

        graph.nodes.forEach((id, node) -> Assert.assertEquals(node.name, CourseGraphDemo.Labels.Course.toString()));
    }

    @Test
    public void multipleNodeNames() throws Exception {
        Label label1 = CourseGraphDemo.Labels.Course;
        Label label2 = CourseGraphDemo.Labels.Topic;

        // Query all nodes that name Course or Topic
        var graph = executeQuery("<graph>",
                String.format("<node name=\"%s\" />", CourseGraphDemo.Labels.Course),
                String.format("<node name=\"%s\" />", CourseGraphDemo.Labels.Topic),
                "</graph>");
        print(graph);

        boolean containsLabel1 = false;
        boolean containsLabel2 = false;
        for (var node : graph.nodes.values()) {
            if (node.name != null) {
                if (node.name.equals(label1.toString())) {
                    containsLabel1 = true;
                }
                if (node.name.equals(label2.toString()))
                    containsLabel2 = true;
            }
        }

        Assert.assertTrue(containsLabel1);
        Assert.assertTrue(containsLabel2);
    }

    @Test
    public void attributeOnly() throws Exception {
        // Query all nodes that have property "title"
        var graph = executeQuery("<graph>",
                "<node>",
                "<data key=\"title\" />",
                "</node>",
                "</graph>"
                );
        print(graph);

        graph.nodes.forEach((id, node) -> Assert.assertNotNull(node.properties.get("title")));
    }

    @Test
    public void numberConstraintAttribute() throws Exception {
        // Query courses that have difficulty rating greater than or equal to 3
        var graph = executeQuery("<graph>",
                "<node>",
                "   <data key=\"difficulty\" type=\"number\">",
                "       <constraint type=\"gt\">",
                "           3",
                "       </constraint>",
                "   </data>",
                "</node></graph>");
        print(graph);
    }
}
