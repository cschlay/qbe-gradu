package graphs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path to perform aggregations and validate elements in the path.
 */
public class QbePath {
    private final List<GraphEntity> elements;

    public QbePath()
    {
        elements = new ArrayList<>();
    }

    public QbePath(List<GraphEntity> initialPath)
    {
        elements = initialPath;
    }

    /**
     * Add a node or edge to the path as last element.
     *
     * @param entity to add
     */
    public void add(GraphEntity entity) {
        elements.add(entity);
    }

    /**
     * Find the entity stored in the path by its name.
     *
     * @param name of the node or edge
     * @return the entity found or null
     */
    public @Nullable GraphEntity find(String name) {
        for (GraphEntity entity: elements) {
            if (name.equals(entity.name)) {
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
        return new QbePath(elements);
    }
}
