package graphs;

import org.jetbrains.annotations.Nullable;
import utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path to perform aggregations and validate elements in the path.
 */
public class QbePath {
    private final List<GraphEntity> entities;
    // The direction is always outgoing (v1 -> v2 -> v3 -> v4)
    private boolean valid = true;

    public QbePath()
    {
        entities = new ArrayList<>();
    }

    public QbePath(List<GraphEntity> initialPath)
    {
        entities = new ArrayList<>();
        entities.addAll(initialPath);
    }

    /**
     * Add a node or edge to the path as last element.
     *
     * @param entity to add
     */
    public void add(GraphEntity entity) {
        if (entity instanceof QbeEdge && ((QbeEdge) entity).headNode == Utils.last(entities)) {
            valid = false;
        }
        entities.add(entity);
    }

    /**
     * Find the entity stored in the path by its name.
     *
     * @param name of the node or edge
     * @return the entity found or null
     */
    public @Nullable GraphEntity find(String name) {
        // NOTE: There may could exist multiple nodes with same name. In that case, the last one is probably the choice.
        for (int i = entities.size() - 1; i >= 0; i--) {
            GraphEntity entity = entities.get(i);
            if (name.equals(entities.get(i).name)) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Create a shallow copy of the path, useful for branching.
     *
     * @return the new path independent of the previous one
     */
    public QbePath copy() {
        return new QbePath(entities);
    }

    public boolean isValid() {
        return valid;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GraphEntity entity : entities) {
            stringBuilder.append(String.format("%s(%s)", entity.name, entity.id));
        }
        return stringBuilder.toString();
    }
}
