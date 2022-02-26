package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.QueryType;
import core.utilities.Utils;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for node columns
 */
public class TabularNodeParser implements TabularColumnParser<QbeNode> {
    private final TabularDataParser dataParser;
    private final QueryGraph graph;

    public TabularNodeParser(QueryGraph graph) {
        this.graph = graph;
        dataParser = new TabularDataParser();
    }

    /**
     * Parse the entity column into QbeNode
     *
     * @param header name is used to create a new node
     * @param value will be parsed into node QueryType
     * @return a new QbeNode
     * @throws SyntaxError if value is not QueryType or header starts with lowercase letter
     */
    public QbeNode parseEntity(TabularHeader header, String value) throws SyntaxError {
        if (!Utils.startsWithUppercase(header.name)) {
            throw new SyntaxError("Entity name for nodes must start with uppercase letter such as 'Book'.");
        }

        QbeNode node = graph.getOrDefault(header.name, new QbeNode(header.name));
        var parts = value.split(" ");
        node.type = TabularTokens.getQueryType(parts[0]);

        if (node.type == QueryType.COUNT) {
            header.name = "_agg-count";
            header.selected = true;
            header.entityName = node.name;
            header.displayName = node.name + ".count";
            // TODO: Support alias
        }

        return node;
    }

    /**
     * Parse property into a new node if not already exists.
     * If the node exists, then the property will be added to existing one.
     *
     * @param header name is used as property name and entity is read from it
     * @param value of the property
     * @return node with property attached
     */
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
