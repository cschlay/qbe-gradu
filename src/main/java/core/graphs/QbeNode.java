package core.graphs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    @Nullable
    public String id;

    @Nullable
    public String name;

    @NotNull
    public HashMap<String, Object> properties;

    // Adjacency list like representation but wrapped inside a container.
    public ArrayList<QbeEdge> edges = new ArrayList<>();

    // TODO: Remove
    public String getId() {
        return String.valueOf(id);
    }

    public void setId(long newId) {
        id = String.valueOf(newId);
    }

    public QbeNode() {
        properties = new HashMap<>();
    }
}
