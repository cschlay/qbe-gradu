package core.db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.QbeData;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.NotFoundException;

import java.util.HashMap;

public class Neo4jPropertyTraversal {
    public static HashMap<String, QbeData> getProperties(@NotNull Entity neo4jNode, @NotNull HashMap<String, QbeData> queryProperties) throws InvalidNodeException {
        var properties = new HashMap<String, QbeData>();

        for (String propertyName : queryProperties.keySet()) {
            QbeData qbeData = queryProperties.get(propertyName);
            try {
                Object value = neo4jNode.getProperty(propertyName);

                // Only include properties that passes constraint checks
                if (qbeData.checkConstraints(value)) {
                    if (!qbeData.isHidden) {
                        properties.put(propertyName, new QbeData(value));
                    }
                } else {
                    throw new InvalidNodeException();
                }
            } catch (NotFoundException e) {
                // Non-nullable properties must always be defined.
                if (!qbeData.isNullable) {
                    throw new InvalidNodeException();
                }
            }
        }

        return properties;
    }
}
