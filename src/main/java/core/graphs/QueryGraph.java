package core.graphs;

import core.exceptions.QbeException;
import org.jetbrains.annotations.Nullable;


public class QueryGraph extends Graph {
    public @Nullable Object meta;

    public QueryGraph() {

    }

    public QueryGraph(@Nullable Object meta) {
        this.meta = meta;
    }

    private int edgeCount;

    public void put(QbeNode node) {
        put(node.name, node);
    }

    public void put(QbeEdge edge) {
        boolean added = false;
        if (edge.headNode != null) {
            edge.headNode.edges.put(edge.name, edge);
            put(edge.headNode.name, edge.headNode);
            added = true;
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(edge.name, edge);
            put(edge.tailNode.name, edge.tailNode);
            added = true;
        }

        if (!added) {
            hangingEdges.add(edge);
        }
    }

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
