package core.graphs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents graph nodes.
 * Every known nodes have id and names.
 * For query nodes the name and id are optional.
 */
public class QbeNode extends GraphEntity {
    @NotNull public List<QbeEdge> edges;

    public QbeNode(@Nullable String name) {
        super(name);
        edges = new ArrayList<>();
    }

    public QbeNode(long id, @Nullable String name) {
        super(id, name);
        edges = new ArrayList<>();
    }

    public boolean equalByName(@Nullable QbeNode otherNode) {
        return name != null && otherNode != null && name.equals(otherNode.name);
    }
}
