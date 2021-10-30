package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    @Nullable public String id;
    @Nullable public String name;
    public HashMap<String, QbeData> properties;
    public ArrayList<QbeEdge> edges;

    public QbeNode(@Nullable String name, HashMap<String, QbeData> properties) {
        this.name = name;
        this.properties = properties;
    }

    public QbeNode(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = name;
        edges = new ArrayList<>();
        properties = new HashMap<>();
    }
}
