package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TabularEdgeParser implements TabularColumnParser<QbeEdge> {
    private final TabularDataParser dataParser;
    // Cache the parsed edges, so that lookup is faster.
    private final Map<String, QbeEdge> edges;
    private final QueryGraph graph;

    public TabularEdgeParser(QueryGraph graph) {
        this.graph = graph;

        edges = new HashMap<>();
        dataParser = new TabularDataParser();
    }

    public QbeEdge parseEntity(TabularHeader header, String value) throws SyntaxError {
        String[] parts = value.split("([ .])");
        if (parts.length != 3) {
            throw new SyntaxError("Edge entity column must include operation e.g. 'CREATE Topic.Song'.");
        }

        QbeEdge edge = new QbeEdge(header.name);
        edge.type = TabularTokens.getQueryType(parts[0]);
        edge.tailNode = getOrCreateNode(parts[1]);
        edge.headNode = getOrCreateNode(parts[2]);
        edges.put(edge.name, edge);
        return edge;
    }

    public QbeEdge parseProperty(TabularHeader header, String value) {
        QbeEdge edge = getOrCreateEdge(header.entityName);
        QbeData data = dataParser.parse(value);
        edge.properties.put(header.name, data);

        if ("id".equals(header.name)) {
            edge.id = value;
        }

        return edge;
    }

    private QbeEdge getOrCreateEdge(String name) {
        @Nullable QbeEdge edge = edges.get(name);
        if (edge != null) {
            return edge;
        }

        edge = new QbeEdge(name);
        edges.put(edge.name, edge);
        return edge;
    }

    private QbeNode getOrCreateNode(String name) {
        @Nullable QbeNode node = graph.get(name);
        return node != null ? node : new QbeNode(name);
    }
}
