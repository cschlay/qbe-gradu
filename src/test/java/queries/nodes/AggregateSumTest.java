package queries.nodes;

import base.QueryBaseStaticTest;
import enums.QueryType;
import graphs.QbeData;
import graphs.QbeNode;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Node - Aggregation SUM")
class AggregateSumTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Label label = Label.label("Book");
            Node node = tx.createNode(label);
            node.setProperty("year", 1990);
            node.setProperty("price", 5.00);
            tx.createNode(label).setProperty("price", 60.00);
            tx.commit();
        });
    }

    @Test
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        var queryNode = new QbeNode("Book");
        queryNode.type = QueryType.SUM;
        queryNode.aggregationProperty = "price";
        queryNode.properties.put("price", new QbeData(null));
        queryGraph.put(queryNode);
        assertEquals(65.0, execute(queryGraph).get("Book").property("price"));
    }

    @Test
    void asQbeTable() throws Exception {
        var query = "" +
                "| Book  | price* |\n" +
                "|-------+--------|\n" +
                "| QUERY | SUM _  |\n";
        assertEquals(65.0, execute(query).get("Book").property("price"));
    }

    @Test
    void filterAndSumByProperty() throws Exception {
        var query = "" +
                "| Book  | year* | price* |\n" +
                "|-------+-------+--------|\n" +
                "| QUERY | 1990  | SUM _  |\n";
        assertEquals(5.00, execute(query).get("Book").property("price"));
    }

    // Extra: Group and Sum by Property
    // Extra: Sum Anonymous Nodes
}
