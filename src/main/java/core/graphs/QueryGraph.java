package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class QueryGraph extends HashMap<String, QbeNode> {
    public @Nullable Object meta;

    private int edgeCount;

    /**
     * Returns the number of nodes.
     */
    public int order() {
        return super.size();
    }

    /**
     * Returns the number of edges.
     */
    @Override
    public int size() {
        return this.edgeCount;
    }
}
