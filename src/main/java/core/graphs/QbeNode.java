package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    @Nullable
    public String id;

    @Nullable
    public String name;

    // Adjacency list like representation but wrapped inside a container.
    public ArrayList<QbeEdge> edges = new ArrayList<>();

    public String getId() {
        return String.valueOf(id);
    }
}
