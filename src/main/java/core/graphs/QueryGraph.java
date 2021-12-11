package core.graphs;

import java.util.HashMap;


public class QueryGraph extends HashMap<String, QbeNode> {
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
