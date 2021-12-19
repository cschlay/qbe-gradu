package syntax.tabular;

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

    public QbeNode parse(TabularHeader header, String exampleData) {
        @Nullable QbeNode node = graph.get(header.name);
        if (node == null) {
            node = new QbeNode(header.name);
        }
        QbeData data = dataParser.parse(exampleData);
        node.properties.put(header.propertyName, data);

        return node;
    }
}
