package queries;


import org.junit.Test;

public class AnonymousNodeQueries extends QueryTest {

    @Test
    public void allNodes() throws Exception {
        var graph = session.processQuery("<graph><node /></graph>");
        System.out.println(graph.toGraphML());

        // TODO: Assert
    }

    /*@Test
    public void limitedByAttributes() throws Exception {
        var graph = session.processQuery("<graph>" +
                "<node name=\"Course\">" +
                "<data key=\"\"></data>" +
                "</node>" +
                "</graph");
    }*/
}
