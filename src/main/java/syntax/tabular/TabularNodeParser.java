package syntax.tabular;

import core.graphs.LogicalExpression;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.jetbrains.annotations.Nullable;

public class TabularNodeParser {
    private final QueryGraph graph;

    public TabularNodeParser(QueryGraph graph) {
        this.graph = graph;
    }

    public QbeNode parse(TabularHeader header, String exampleData) {
        @Nullable QbeNode node = graph.get(header.name);
        if (node == null) {
            node = new QbeNode(header.name);
        }
        node.properties.put(header.propertyName, parseData(exampleData));

        return node;
    }

    // These could be moved into QbeData parser.

    private QbeData parseData(String value) {
        var instance = parseIntoJavaObject(value);
        return new QbeData(instance);
    }

    @Nullable private Object parseIntoJavaObject(String value) {
        if ("".equals(value)) {
            return null;
        }

        if ("false".equals(value) || "true".equals(value)) {
            return Boolean.parseBoolean(value);
        } else if (value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
            return value.substring(1, value.length()-1);
        }
        try {
            return parseIntoNumeric(value);
        } catch (NumberFormatException ignored) {
            return new LogicalExpression(value);
        }
    }

    private Object parseIntoNumeric(String value) {
        if (value.contains(".")) {
            return Double.parseDouble(value);
        }
        return Integer.parseInt(value);
    }
}
