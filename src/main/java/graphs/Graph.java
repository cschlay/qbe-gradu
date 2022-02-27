package graphs;

import exceptions.EntityNotFound;
import utilities.CustomStringBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * The main graph data structure for query and result graphs.
 */
public abstract class Graph extends HashMap<String, QbeNode> {
    /**
     * A helper function to obtain edge by their keys
     *
     * @param nodeKey the name or id of node
     * @param edgeKey the name or id of edge
     * @return the edge
     * @throws EntityNotFound if node or edge does not exist
     */
    public QbeEdge getEdge(String nodeKey, String edgeKey) throws EntityNotFound {
        @Nullable QbeNode node = get(nodeKey);
        if (node == null) {
            throw new EntityNotFound("Node '%s' not found, when looking for edge '%s'.", nodeKey, edgeKey);
        }

        @Nullable QbeEdge edge = node.edges.get(edgeKey);
        if (edge != null) {
            return edge;
        }

        throw new EntityNotFound("Node '%s' doesn't have edge '%s'.", nodeKey, edgeKey);
    }

    // Functions below are not part of the implementation
    @Override
    public boolean equals(Object graph) {
        return super.equals(graph);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        var ret = new CustomStringBuilder();
        for (var node : entrySet()) {
            ret.line("%s", node.getValue().toString());
        }
        return ret.toString();
    }
}
