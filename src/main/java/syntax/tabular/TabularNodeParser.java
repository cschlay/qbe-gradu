package syntax.tabular;

import exceptions.SyntaxError;
import graphs.QbeData;
import graphs.QbeNode;
import graphs.QueryGraph;
import enums.QueryType;
import interfaces.TabularColumnParser;
import utilities.Utils;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for node columns
 */
public class TabularNodeParser extends TabularEntityParser implements TabularColumnParser<QbeNode> {
    private final QueryGraph graph;

    public TabularNodeParser(QueryGraph graph) {
        this.graph = graph;
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
        QbeNode node = getOrCreateNode(header.entityName);
        QbeData data = super.parseData(header, node, value);
        node.properties.put(header.name, data);

        return node;
    }

    private QbeNode getOrCreateNode(String name) {
        @Nullable QbeNode node = graph.get(name);
        return node == null ? new QbeNode(name) : node;
    }
}
