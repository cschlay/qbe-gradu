package base;

import exceptions.SyntaxError;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import syntax.tabular.TabularHeader;
import syntax.tabular.TabularQueryMeta;
import syntax.tabular.TabularResultWriter;

public abstract class WriterBaseTest {
    private static final TabularResultWriter writer = new TabularResultWriter();

    public QueryGraph setupQuery(TabularHeader... headers) throws SyntaxError {
        var queryGraph = new QueryGraph();
        queryGraph.meta = new TabularQueryMeta(headers);
        return queryGraph;
    }

    protected String execute(QueryGraph query, ResultGraph result) {
        return writer.write(query, result);
    }

    protected QbeNode createNode(long id) {
        var node = new QbeNode(id, "Course");
        node.selected = true;
        return node;
    }
}
