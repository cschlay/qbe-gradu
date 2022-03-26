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
        String edgeKey = getEdgeNameKey(edge);
        if (edge.headNode != null) {
            edge.headNode.edges.put(edgeKey, edge);
            put(edge.headNode.name, edge.headNode);
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(edgeKey, edge);
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

    // There is conflict with using edge.name as key, so it must be replaced with verbose name
    // name_Node.Node
    private String getEdgeNameKey(QbeEdge edge) {
        var key = new StringBuilder(edge.name);
        key.append("-");

        if (edge.tailNode != null) {
            key.append(edge.tailNode.name);
        }
        key.append(".");
        if (edge.headNode != null) {
            key.append(edge.headNode.name);
        }

        return key.toString();
    }
}
