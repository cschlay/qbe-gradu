package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.utilities.Debug;
import core.utilities.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for the edge columns
 */
public class TabularEdgeParser implements TabularColumnParser<QbeEdge> {
    private final TabularDataParser dataParser;
    private final QueryGraph graph;

    // Cache the parsed edges, so that lookup is faster
    private final Map<String, QbeEdge> edges;

    public TabularEdgeParser(QueryGraph graph) {
        this.graph = graph;
        edges = new HashMap<>();
        dataParser = new TabularDataParser();
    }

    /**
     * Parse the entity column of an edge. The reference to the edge is stored if properties are parsed later
     *
     * @param header name is used as edge name
     * @param value like 'QUERY Tail.Head'
     * @return the edge
     * @throws SyntaxError if header name starts with lowercase letter or syntax is incorrect for the value
     */
    public QbeEdge parseEntity(TabularHeader header, String value) throws SyntaxError {
        if (Utils.startsWithUppercase(header.name)) {
            throw new SyntaxError("Entity name for edges must start with lowercase letter e.g. 'sells'.");
        }

        String[] parts = value.split("([ .])");
        if (parts.length != 3 || (parts[0].length() == 0) || (parts[1].length()) == 0) {
            throw new SyntaxError("Edge entity column must include operation e.g. 'CREATE Topic.Song'.");
        }

        QbeEdge edge = new QbeEdge(header.name);
        edge.type = TabularTokens.getQueryType(parts[0]);
        // This is a problem, it creates empty relations if name cannot be parsed
        edge.tailNode = getOrCreateNode(parts[1]);
        edge.headNode = getOrCreateNode(parts[2]);
        edges.put(edge.name, edge);
        return edge;
    }

    /**
     * Parse the property into edge. A new edge will be created if not previously added.
     *
     * @param header name is the property name
     * @param value of the property
     * @return the edge
     */
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
