package base;

import exceptions.SyntaxError;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import syntax.tabular.TabularHeader;
import syntax.tabular.TabularQueryMeta;
import syntax.tabular.TabularResultWriter;

public abstract class WriterBaseTest {
    private static final TabularResultWriter writer = new TabularResultWriter();

    public QueryGraph setupQuery(TabularHeader... headers) {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(headers);
        return queryGraph;
    }

    protected String execute(QueryGraph query, ResultGraph result) {
        return writer.write(query, result);
    }

    protected QbeEdge edge(long id, String name, QbeNode tail, QbeNode head) {
        var edge = new QbeEdge(id, name);
        edge.selected = true;
        edge.tailNode = tail;
        edge.headNode = head;

        return edge;
    }

    protected QbeNode node(long id, String name) {
        var node = new QbeNode(id, name);
        node.selected = true;

        return node;
    }
}
