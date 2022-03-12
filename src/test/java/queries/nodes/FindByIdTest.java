package queries.nodes;

import base.QueryBaseStaticTest;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Node - Find by Id")
class FindByIdTest extends QueryBaseStaticTest {
    private static long id;

    @BeforeAll
    static void setup() throws Exception {
        var fx = new Object() { Node node; };
        run(tx -> {
            fx.node = tx.createNode(Label.label("Book"));
            tx.commit();
        });
        id = fx.node.getId();
    }

    @Test
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        queryGraph.put(new QbeNode(id, "Book"));
        assertResult(execute(queryGraph));
    }

    @Test
    void asQbeTable() throws Exception {
        var query = "" +
                "| Book  | id* |\n" +
                "|-------+-----|\n" +
                "| QUERY | %s  |\n";
        assertResult(execute(query, id));
    }

    private void assertResult(ResultGraph graph) {
        assertNotNull(graph.get(String.valueOf(id)));
    }
}
