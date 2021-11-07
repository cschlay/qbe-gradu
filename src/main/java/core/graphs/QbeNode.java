package core.graphs;

import core.interfaces.PropertyQueryable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    @Nullable public String id;
    @Nullable public String name;
    @NotNull public HashMap<String, QbeData> properties;
    @NotNull public ArrayList<QbeEdge> edges;
    public boolean isHidden;

    public QbeNode(@Nullable String name, @NotNull HashMap<String, QbeData> properties) {
        this.name = name;
        this.properties = properties;
        edges = new ArrayList<>();
    }

    public QbeNode(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = name;
        edges = new ArrayList<>();
        properties = new HashMap<>();
    }

    public QbeNode(@NotNull String id, @Nullable String name) {
        this.id = id;
        this.name = name;
        edges = new ArrayList<>();
        properties = new HashMap<>();
    }
}
