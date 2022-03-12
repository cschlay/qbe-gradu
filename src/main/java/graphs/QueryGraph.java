package graphs;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class QueryGraph extends Graph  {
    public transient @Nullable Object meta;

    public QueryGraph() {}

    public QueryGraph(@Nullable Object meta) {
        this.meta = meta;
    }

    public void put(QbeNode node) {
        put(node.name, node);
    }

    public void put(QbeEdge edge) {
        if (edge.headNode != null) {
            edge.headNode.edges.put(edge.name, edge);
            put(edge.headNode.name, edge.headNode);
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(edge.name, edge);
            put(edge.tailNode.name, edge.tailNode);
        }
    }

    /**
     * Returns the number of nodes.
     */
    public int order() {
        return super.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        QueryGraph that = (QueryGraph) o;
        return Objects.equals(meta, that.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), meta);
    }
}
