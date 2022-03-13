package queries.nodes;

import base.QueryBaseStaticTest;
import enums.QueryType;
import graphs.QbeNode;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Node - Aggregation Count")
class AggregateCountTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            tx.createNode(Label.label("Book"));
            tx.createNode(Label.label("Book")).setProperty("year", 2022);
            tx.createNode(Label.label("Topic"));
            tx.commit();
        });
    }

    @Test
    @DisplayName("Count by Name Query Graph")
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        var queryNode = new QbeNode("Book");
        queryNode.type = QueryType.COUNT;
        queryGraph.put(queryNode);
        assertEquals(2, execute(queryGraph).get("Book").property("_agg-count"));
    }

    @Test
    @DisplayName("Count by Name Tabular")
    void asQbeTable() throws Exception {
        var query = "" +
                "| Book  |\n" +
                "|-------|\n" +
                "| COUNT |\n";
        assertEquals(2, execute(query).get("Book").property("_agg-count"));
    }

    @Test
    void filterAndCountByProperty() throws Exception {
        var query = "" +
                "| Book  | year* |\n" +
                "|-------+-------|\n" +
                "| COUNT | 2022  |\n";
        assertEquals(1, execute(query).get("Book").property("_agg-count"));
    }

    // Extra: Group and Count by Property
    // Extra: Count Anonymous Nodes
}
