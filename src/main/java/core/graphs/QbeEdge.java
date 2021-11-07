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
    @Nullable public String tailNodeName;

    /** The node where edge ends: (x) --> (head) */
    @Nullable public String headNodeName;

    public QbeEdge(long id, long tailNodeId, long headNodeId)
    {
        this.id = String.valueOf(id);
        this.properties = new HashMap<>();
        this.tailNodeName = String.valueOf(tailNodeId);
        this.headNodeName = String.valueOf(headNodeId);
    }

    public QbeEdge(
            @Nullable String name,
            @Nullable String tailNodeName,
            @Nullable String headNodeName,
            @NotNull HashMap<String, QbeData> properties) {
        this.name = name;
        this.properties = properties;

        this.tailNodeName = tailNodeName;
        this.headNodeName = headNodeName;
    }
}
