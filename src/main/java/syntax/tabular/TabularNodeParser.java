package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.jetbrains.annotations.Nullable;

public class TabularNodeParser implements TabularColumnParser<QbeNode> {
    private final TabularDataParser dataParser;
    private final QueryGraph graph;

    public TabularNodeParser(QueryGraph graph) {
        this.graph = graph;
        dataParser = new TabularDataParser();
    }

    public QbeNode parseEntity(TabularHeader header, String value) throws SyntaxError {
        var node = new QbeNode(header.name);
        node.type = TabularTokens.getQueryType(value);
        return node;
    }

    public QbeNode parseProperty(TabularHeader header, String value) {
        @Nullable QbeNode node = graph.get(header.entityName);
        if (node == null) {
            node = new QbeNode(header.entityName);
        }
        QbeData data = dataParser.parse(value);
        node.properties.put(header.name, data);

        if ("id".equals(header.name)) {
            node.id = value;
        }

        return node;
    }
}
