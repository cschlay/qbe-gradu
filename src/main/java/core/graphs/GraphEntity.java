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
    public final Map<String, QbeData> properties;
    public boolean visited;

    private long _id;

    protected GraphEntity(@Nullable String name) {
        this.name = constructName(name);
        id = null;
        properties = new HashMap<>();
    }

    protected GraphEntity(long id, @Nullable String name) {
        _id = id;
        this.id = String.valueOf(id);
        this.name = constructName(name);

        properties = new HashMap<>();
        properties.put("id", new QbeData(id));
    }

    public long getId() {
        return _id;
    }

    /**
     * Simplifies the access of entity.properties.get("name").value to just entity.getProperty("name")

     * @return the value of property
     */
    public @Nullable Object getProperty(String property) {
        return properties.get(property).value;
    }

    public String toString() {
        return name + "(" + properties + ")";
    }

    private String constructName(@Nullable String name) {
        return name == null ? "" : name;
    }
}
