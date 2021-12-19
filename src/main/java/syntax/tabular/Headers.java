package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeNode;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Headers {
    public final int length;
    private final TabularHeader[] names;
    private final HashMap<String, HashMap<String, Integer>> indices;

    public Headers(String[] headers) throws SyntaxError {
        indices = new HashMap<>();
        length = headers.length;
        names = new TabularHeader[length];

        for (int i = 0; i < length; i++) {
            var header = new TabularHeader(headers[i]);
            names[i] = header;

            HashMap<String, Integer> index = indices.computeIfAbsent(header.name, key -> new HashMap<>());
            index.put(header.propertyName, i);
        }
    }
    public TabularHeader get(int index) {
        return names[index];
    }

    @Nullable public Integer getIndex(QbeNode node, String property) {
        return indices.get(node.name).get(property);
    }

    public String getDisplayName(int index) {
        return names[index].toString();
    }
}