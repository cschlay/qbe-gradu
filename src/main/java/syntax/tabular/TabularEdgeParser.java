package syntax.tabular;

import enums.QueryType;
import exceptions.SyntaxError;
import graphs.QbeData;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import interfaces.TabularColumnParser;
import utilities.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser for the edge columns
 */
public class TabularEdgeParser extends TabularEntityParser implements TabularColumnParser<QbeEdge> {
    private final QueryGraph graph;

    // Cache the parsed edges, so that lookup is faster
    private final Map<String, QbeEdge> edges;

    public TabularEdgeParser(QueryGraph graph) {
        this.graph = graph;
        edges = new HashMap<>();
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

        String[] tokens = value.split("([ .])");
        if (tokens.length < 3 || (tokens[0].length() == 0) || (tokens[1].length()) == 0) {
            throw new SyntaxError("Edge entity column '%s' must include operation e.g. 'CREATE Topic.Song'.", header.name);
        }

        var edge = new QbeEdge(header.name);
        edge.type = TabularTokens.getQueryType(tokens[0]);

        // This is a problem, it creates empty relations if name cannot be parsed
        edge.tailNode = getOrCreateNode(tokens[1]);
        edge.headNode = getOrCreateNode(tokens[2]);

        if (edge.type == QueryType.COUNT) {
            parseCountAggregation(header, edge, tokens);
        }

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
        QbeData data = super.parseData(header, edge, value);
        edge.properties.put(header.name, data);

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
