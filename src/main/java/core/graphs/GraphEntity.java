package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Nullable public String aggregationProperty;
    @Nullable public String aggregationGroup;
    public List<String> aggregatedIds;

    public boolean visited;
    public boolean selected;

    protected GraphEntity(@Nullable String name) {
        this.name = constructName(name);
        id = null;
        properties = new HashMap<>();

        aggregatedIds = new ArrayList<>();
    }

    protected GraphEntity(long id, @Nullable String name) {
        this(name);

        this.id = String.valueOf(id);
        properties.put("id", new QbeData(id));
    }

    public long longId()  {
        if (id == null) {
            throw new NullPointerException(String.format("Cannot read null id of node '%s' as long", name));
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Cannot parse id '%s' of node '%s' into long, maybe your ID is string or uuid", id, name));
        }
    }
    /**
     * Simplifies the access of entity.properties.get("name").value to just entity.getProperty("name")
     *
     * @return the value of property
     */
    public @Nullable Object property(String property) {
        @Nullable QbeData data = properties.get(property);
        return data != null ? data.value : null;
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
