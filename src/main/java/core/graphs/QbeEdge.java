package core.graphs;

import core.interfaces.PropertyQueryable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents an edge of a graph.
 */
public class QbeEdge implements PropertyQueryable {
    @Nullable public String id;
    @Nullable public String name;
    public HashMap<String, QbeData> properties;

    /** The node of where edge starts: (tail) --> (x) */
    @Nullable public QbeNode tailNode;

    /** The node where edge ends: (x) --> (head) */
    @Nullable public QbeNode headNode;

    public boolean isHidden;
    public boolean isTransitive;

    public QbeEdge(long id)
    {
        this.id = String.valueOf(id);
        this.properties = new HashMap<>();
    }

    public QbeEdge(
            @Nullable String name,
            @NotNull HashMap<String, QbeData> properties) {
        this.name = name;
        this.properties = properties;
    }
}
