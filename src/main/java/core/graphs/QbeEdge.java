package core.graphs;

import core.utilities.CustomStringBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an edge of a graph.
 */
public class QbeEdge extends GraphEntity {
    /** The node of where edge starts: (tail) --> (x) */
    @Nullable public QbeNode tailNode;

    /** The node where edge ends: (x) --> (head) */
    @Nullable public QbeNode headNode;

    public boolean isHidden;
    public boolean isTransitive;

    public QbeEdge(@Nullable String name) {
        super(name);
    }

    public QbeEdge(long id, @Nullable String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        var ret = new CustomStringBuilder();

        String tail = toStringNodeName(tailNode);
        String head = toStringNodeName(headNode);

        ret.line("%s: %s -> %s %s%n", name, tail, head, type);
        for (var property : properties.entrySet()) {
            ret.line(indent + 2, "%s = %s%n", property.getKey(), property.getValue());
        }
        return ret.toString();
    }

    private String toStringNodeName(@Nullable QbeNode node) {
        if (node == null) {
            return "None";
        }

        if (node.id != null) {
            return String.format("%s(id=%s)", node.name, node.id);
        }

        return node.name;
    }
}
