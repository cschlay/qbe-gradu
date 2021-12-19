package core.graphs;

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
}
