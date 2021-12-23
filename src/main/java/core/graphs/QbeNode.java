package core.graphs;

import org.jetbrains.annotations.Nullable;

/**
 * Represents graph nodes.
 * Every known nodes have id and names.
 * For query nodes the name and id are optional.
 */
public class QbeNode extends GraphEntity {
    public Edges edges;

    public QbeNode(@Nullable String name) {
        super(name);
        edges = new Edges();
    }

    public QbeNode(long id, @Nullable String name) {
        super(id, name);
        edges = new Edges();
    }

    public boolean equalByName(@Nullable QbeNode otherNode) {
        return name != null && otherNode != null && name.equals(otherNode.name);
    }

    @Override public String toString() {
        return name + "({" + properties + "}," + edges +")";
    }
}
