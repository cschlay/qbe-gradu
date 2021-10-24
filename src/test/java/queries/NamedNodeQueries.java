package queries;

import org.junit.Test;

public class NamedNodeQueries extends QueryTest {
    @Test
    public void singleNode() throws Exception {
        var graph = session.processQuery("<graph><node name=\"Course\"/></graph>");
        System.out.println(graph.toGraphML());
        // TODO: Assert
    }

    @Test
    public void multipleNodes() throws Exception {
        var graph = session.processQuery("<graph><node name=\"Course\"/></graph>");

    }
}
