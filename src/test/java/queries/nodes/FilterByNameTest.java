package queries.nodes;

import base.QueryBaseStaticTest;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Node - Filter by Name")
class FilterByNameTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            tx.createNode(Label.label("Book"));
            tx.createNode(Label.label("Course"));
            tx.commit();
        });
    }

    @Test
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.put(new QbeNode("Course"));
        assertResult(execute(queryGraph));
    }

    @Test
    void asQbeTable() throws Exception {
        var query = "" +
                "| Course | id* |\n" +
                "|--------+-----|\n" +
                "| QUERY  |     |\n";
        assertResult(execute(query));
    }

    private void assertResult(ResultGraph graph) throws Exception {
        eachNode(graph, (tx, node) -> assertEquals("Course", node.name));
    }
}
