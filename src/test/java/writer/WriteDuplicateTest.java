package writer;

import base.WriterBaseTest;
import enums.QueryType;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Writer] Exclude Duplicate Rows")
class WriteDuplicateTest extends WriterBaseTest {
    // The duplicate rows occurs mostly with complex queries and aggregations.

    @Test
    @DisplayName("edges are counted")
    void countedEdges() throws Exception {
        var contains = new TabularHeader("contains");
        contains.name = "_agg-count";
        contains.entityName = "contains";
        contains.displayName = "contains.count";
        contains.selected = true;
        var theory = new TabularHeader("contains", "theory");
        QueryGraph query = setupQuery(contains, theory);

        var queryEdge = new QbeEdge("contains");
        queryEdge.type = QueryType.COUNT;
        queryEdge.tailNode = new QbeNode("Book");
        queryEdge.headNode = new QbeNode("Topic");
        query.put(queryEdge);

        var result = new ResultGraph();
        result.put(edge(0, "contains", node(0, "Book"), node(1, "Topic")));

        var agg = new QbeNode("contains");
        agg.selected = true;
        agg.addProperty("_agg-count", 1);
        result.put(agg);

        var expected = "" +
                "| contains.count |\n" +
                "|----------------|\n" +
                "| 1              |\n";
        assertEquals(expected, execute(query, result));
    }
}
