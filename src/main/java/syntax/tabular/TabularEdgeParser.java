package syntax.tabular;

import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.jetbrains.annotations.Nullable;

public class TabularEdgeParser implements TabularColumnParser<QbeEdge> {
    private final TabularDataParser dataParser;
    private final QueryGraph graph;

    public TabularEdgeParser(QueryGraph graph) {
        this.graph = graph;
        dataParser = new TabularDataParser();
    }

    public QbeEdge parse(TabularHeader header, String exampleData) {
        QbeEdge edge = getOrCreateEdge(header);
        edge.headNode = getHeadNode(edge, header);
        edge.tailNode = getTailNode(edge, header);

        QbeData data = dataParser.parse(exampleData);
        edge.properties.put(header.propertyName, data);

        return edge;
    }

    private QbeEdge getOrCreateEdge(TabularHeader header) {
        @Nullable QbeNode tailNode = graph.get(header.tailNodeName);
        @Nullable QbeNode headNode = graph.get(header.headNodeName);

        @Nullable QbeEdge edge = null;
        if (tailNode != null) {
            edge = tailNode.edges.get(header.name);
        }
        if (edge == null && headNode != null) {
            edge = headNode.edges.get(header.name);
        }

        return edge == null ? new QbeEdge(header.name) : edge;
    }

    private @Nullable QbeNode getHeadNode(QbeEdge edge, TabularHeader header) {
        if (edge.headNode != null) {
            return edge.headNode;
        }

        @Nullable QbeNode node = graph.get(header.headNodeName);
        if (node == null) {
            return header.headNodeName == null ? null : new QbeNode(header.headNodeName);
        }
        return node;
    }

    private QbeNode getTailNode(QbeEdge edge, TabularHeader header) {
        if (edge.tailNode != null) {
            return edge.tailNode;
        }

        @Nullable QbeNode node = graph.get(header.tailNodeName);
        if (node == null) {
            return header.tailNodeName == null ? null : new QbeNode(header.tailNodeName);
        }
        return node;
    }
}
