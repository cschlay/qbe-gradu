package core.graphs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents graph nodes.
 * Every known nodes have id and names.
 * For query nodes the name and id are optional.
 */
public class QbeNode {
    @Nullable public final String id;
    @Nullable public final String name;
    @NotNull public List<QbeEdge> edges;
    @NotNull public Map<String, QbeData> properties;

    public QbeNode(@Nullable String name) {
        this.name = name == null ? "" : name;

        id = null;
        properties = new HashMap<>();
        edges = new ArrayList<>();

    }

    public QbeNode(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = name;

        edges = new ArrayList<>();
        properties = new HashMap<>();
        properties.put("id", new QbeData(id));
    }

    public boolean equalByName(@Nullable QbeNode otherNode) {
        return name != null && otherNode != null && name.equals(otherNode.name);
    }

    public String toString() {
        return name + "(" + properties + ")";
    }
}
