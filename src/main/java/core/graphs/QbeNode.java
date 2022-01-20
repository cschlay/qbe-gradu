package core.graphs;

import core.utilities.CustomStringBuilder;
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
        return toString(0);
    }

    public String toString(int indent) {
        var ret = new CustomStringBuilder();
        ret.line(indent, "%s %s\n", name, type);
        for (var property : properties.entrySet()) {
            ret.line(indent + 2, "%s = %s\n", property.getKey(), property.getValue());
        }
        ret.line(edges.toString(indent + 2));
        return ret.toString();
    }
}
