package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * In graph databases, edges and nodes are treated as separate "entities".
 * They share features such as "id", "name" and "properties".
 */
public abstract class GraphEntity {
    @Nullable public final String id;
    public final String name;
    public Map<String, QbeData> properties;

    protected GraphEntity(@Nullable String name) {
        this.name = constructName(name);
        id = null;
        properties = new HashMap<>();
    }

    protected GraphEntity(long id, @Nullable String name) {
        this.id = String.valueOf(id);
        this.name = constructName(name);

        properties = new HashMap<>();
        properties.put("id", new QbeData(id));
    }

    public String toString() {
        return name + "(" + properties + ")";
    }

    private String constructName(@Nullable String name) {
        return name == null ? "" : name;
    }
}
