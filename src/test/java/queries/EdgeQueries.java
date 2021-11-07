package queries;

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
    }

    // Transitive edge
}
