package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.GraphEntity;
import core.graphs.QbeData;
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
        this.queryProperties = queryEntity.properties;
    }

    /**
     * Returns the properties of Neo4j entity that meets the query examples
     *
     * @param neo4jEntity to read properties from
     * @return a collection of properties for the result entity
     * @throws InvalidNodeException if node should be discarded
     */
    public Map<String, QbeData> getProperties(Entity neo4jEntity) throws InvalidNodeException {
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
            throws InvalidNodeException {
        if ("id".equals(propertyName)) {
            @Nullable var id = queryData.value;
            if (id != null && (Long) id != neo4jEntity.getId()) {
                throw new InvalidNodeException("Id constraint check failed %s != %s", id, neo4jEntity.getId());
            }

            return new QbeData(neo4jEntity.getId());
        }

        try {
            @Nullable Object value = neo4jEntity.getProperty(propertyName);
            if (queryData.check(value)) {
                return new QbeData(value, queryData.selected, false);
            }
        } catch (NotFoundException exception) {
            if (queryData.nullable) {
                return new QbeData(null, queryData.selected, true);
            }
        }

        throw new InvalidNodeException("Entity %s doesn't have property %s", neo4jEntity.getId(), propertyName);
    }
}
