package queries;

import demo.CourseGraphDemo;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Label;

public class NodeQueries extends QueryTest {
    @Test
    public void singleNodeName() throws Exception {
        // Every node should have same name
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

        // Both nodes with the label should be included
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
        var graph = executeQuery("<graph>",
                "<node>",
                "<data key=\"title\" />",
                "</node>",
                "</graph>"
                );
        print(graph);

        graph.nodes.forEach((id, node) -> Assert.assertNotNull(node.properties.get("title")));
    }
}
