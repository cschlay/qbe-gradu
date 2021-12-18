package syntax.graphml;

import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import interfaces.ResultWriter;

public class GraphMLResultWriter implements ResultWriter {
    public String write(QueryGraph queryGraph, ResultGraph resultGraph) {
        return null;
    }

    public Object writeNative(QueryGraph queryGraph, ResultGraph resultGraph) {
        return null;
    }

    public static String getValueType(Object value) {
        if (value instanceof Boolean) {
            return GraphML.TypeBoolean;
        } else if (value instanceof Integer) {
            return GraphML.TypeInteger;
        }

        return GraphML.TypeText;
    }
}
