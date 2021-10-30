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

    @Nullable public String name;

    @NotNull public HashMap<String, @Nullable QbeData> properties;

    // Adjacency list like representation but wrapped inside a container.
    public ArrayList<QbeEdge> edges = new ArrayList<>();

    public QbeNode() {
        properties = new HashMap<>();
    }

    public QbeNode(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = name;
        properties = new HashMap<>();
    }
}
