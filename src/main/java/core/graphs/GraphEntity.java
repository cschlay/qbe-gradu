package core.graphs;


import core.utilities.CustomStringBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * In graph databases, edges and nodes are treated as separate "entities".
 * They share features such as "id", "name" and "properties".
 */
public abstract class GraphEntity {
    @Nullable public String id;
    public final String name;
    public final Map<String, QbeData> properties;

    public QueryType type = QueryType.QUERY;

    public boolean visited;

    protected GraphEntity(@Nullable String name) {
        this.name = constructName(name);
        id = null;
        properties = new HashMap<>();
    }

    protected GraphEntity(long id, @Nullable String name) {
        this(name);

        this.id = String.valueOf(id);
        properties.put("id", new QbeData(id));
    }

    /**
     * Simplifies the access of entity.properties.get("name").value to just entity.getProperty("name")
     *
     * @return the value of property
     */
    public @Nullable Object property(String property) {
        return properties.get(property).value;
    }

    /**
     * Puts a new property, it will replace previous value if already exist.
     *
     * @param property name
     * @param value of the property
     */
    public void property(String property, Object value) {
        if (value instanceof QbeData) {
            properties.put(property, (QbeData) value);
        } else {
            properties.put(property, new QbeData(value));
        }
    }

    public String toString() {
        return name + "(" + properties + ")";
    }

    private String constructName(@Nullable String name) {
        return name == null ? "" : name;
    }
}
