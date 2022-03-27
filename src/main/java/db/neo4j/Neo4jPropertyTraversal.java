package db.neo4j;

import exceptions.IdConstraintException;
import exceptions.InvalidNodeException;
import graphs.GraphEntity;
import graphs.QbeData;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.NotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides query methods for a set of properties.
 */
public class Neo4jPropertyTraversal {
    private final Map<String, QbeData> queryProperties;

    public Neo4jPropertyTraversal(GraphEntity queryEntity) {
        queryProperties = queryEntity.properties;
    }

    /**
     * Copy the properties of query entity into a Neo4j entity and result entity
     *
     * @param neo4jEntity to copy properties to
     * @param resultEntity to copy properties to
     */
    public void mutableCopyProperties(Entity neo4jEntity, GraphEntity resultEntity) {
        for (var property : queryProperties.entrySet()) {
            String name = property.getKey();
            Object value = property.getValue().value;
            if (!"id".equals(name)) {
                neo4jEntity.setProperty(name, value);
            }
            resultEntity.properties.put(name, new QbeData(value));
        }

        resultEntity.properties.put("id", new QbeData(neo4jEntity.getId()));
    }

    public void mutableUpdate(Entity neo4jEntity, GraphEntity resultEntity) {
        for (var property : queryProperties.entrySet()) {
            String name = property.getKey();
            QbeData data = property.getValue();

            if (data.update != null) {
                neo4jEntity.setProperty(name, data.update);
                resultEntity.properties.put(name, new QbeData(data.update));
            }
        }
    }

    /**
     * Returns the properties of Neo4j entity that meets the query examples
     *
     * @param neo4jEntity to read properties from
     * @return a collection of properties for the result entity
     * @throws InvalidNodeException if node should be discarded
     */
    public Map<String, QbeData> getProperties(Entity neo4jEntity) throws InvalidNodeException, IdConstraintException {
        var properties = new HashMap<String, QbeData>();

        for (var entry : queryProperties.entrySet()) {
            String propertyName = entry.getKey();
            QbeData value = readProperty(neo4jEntity, propertyName, entry.getValue());
            if (value.selected) {
                properties.put(propertyName, value);
            }
        }

        return properties;
    }

    /**
     * Reads and checks if property value is valid.
     *
     * @param neo4jEntity to read property from
     * @param propertyName of the property
     * @param queryData to validate the property
     * @return the property instance
     * @throws InvalidNodeException if the property is invalid
     */
    private QbeData readProperty(Entity neo4jEntity, String propertyName, QbeData queryData)
            throws InvalidNodeException, IdConstraintException {
        if ("id".equals(propertyName)) {
            if (!checkId(queryData, neo4jEntity)) {
                throw new IdConstraintException(queryData.value, neo4jEntity.getId());
            }

            return new QbeData(neo4jEntity.getId());
        }

        if (queryData.update != null) {
            return new QbeData(queryData.update);
        }

        @Nullable Object value = null;
        try {
            value = neo4jEntity.getProperty(propertyName);
        } catch (NotFoundException exception) {
            if (queryData.value == null) {
                return new QbeData(null, queryData.selected);
            }
        }
        if (queryData.check(value)) {
            return new QbeData(value, queryData.selected);
        }

        throw new InvalidNodeException("Entity %s doesn't have property %s", neo4jEntity, propertyName);
    }

    // Helper to check id's whether int or long.
    private boolean checkId(QbeData data, Entity neo4jEntity) {
        @Nullable var id = data.value;

        long neo4jId = neo4jEntity.getId();
        if (id instanceof Integer) {
            return (int) id == neo4jId;
        }
        if (id instanceof Long) {
            return (long) id == neo4jId;
        }

        return true;
    }
}
