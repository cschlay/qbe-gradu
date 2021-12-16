package core.results;

import core.graphs.QbeNode;
import core.parsers.TabularHeader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Headers {
    public final int length;
    private final TabularHeader[] names;
    private final HashMap<String, HashMap<String, Integer>> indices;

    public Headers(String[] headers) {
        indices = new HashMap<>();
        length = headers.length;
        names = new TabularHeader[length];

        for (int i = 0; i < length; i++) {
            var header = new TabularHeader(headers[i]);
            names[i] = header;

            var index = indices.computeIfAbsent(header.name, key -> new HashMap<>());
            index.put(header.propertyName, i);
        }
    }

    @Nullable public Integer get(QbeNode node, String property) {
        return indices.get(node.name).get(property);
    }

    public String getDisplayName(int index) {
        return names[index].toString();
    }
}
