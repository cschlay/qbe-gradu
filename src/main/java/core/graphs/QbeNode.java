package core.graphs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    @Nullable public String id;
    // Do not use null, use empty string "" instead
    public final String name;
    @NotNull public HashMap<String, QbeData> properties;
    @NotNull public ArrayList<QbeEdge> edges;
    public boolean isHidden;

    public QbeNode(@Nullable String name) {
        this.name = name == null ? "" : name;
        properties = new HashMap<>();
        edges = new ArrayList<>();

    }

    // Remove this
    public QbeNode(@Nullable String name, @NotNull HashMap<String, QbeData> properties) {
        this.name = name;
        this.properties = properties;
        edges = new ArrayList<>();
    }

    public QbeNode(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = name;
        edges = new ArrayList<>();
        properties = new HashMap<>();

        // id is considered a property too
        properties.put("id", new QbeData(id));
    }

    public boolean hasSameName(@Nullable QbeNode otherNode) {
        return name != null && otherNode != null && name.equals(otherNode.name);
    }

    public String toString() {
        if (name != null && id != null) {
            return name + "(" + id + ")";
        }
        if (name == null) {
            return "(" + id + ")";
        }
        return name;
    }
}
