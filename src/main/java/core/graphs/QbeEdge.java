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

        var tail = tailNode != null ? String.format("%s(%s)", tailNode.name, tailNode.id) : "None";
        var head = headNode != null ? String.format("%s(%s)", headNode.name, headNode.id) : "None";

        ret.line("%s %s %s -> %s%n", name, type, tail, head);
        for (var property : properties.entrySet()) {
            ret.line(indent + 2, "%s = %s%n", property.getKey(), property.getValue());
        }
        return ret.toString();
    }
}
