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

    /**
     * Applies graph union to the graph. The conflicts are resolved by using the latter values.
     *
     * @param graph to merge with
     * @return the completely merged graph
     */
    protected Graph union(Graph graph) {
        for (QbeNode nodeB : graph.values()) {
            @Nullable QbeNode nodeA = get(nodeB.id);
            if (nodeA != null) {
                put(getHashTableKey(nodeA), nodeA.merge(nodeB));
            } else {
                put(getHashTableKey(nodeB), nodeB);
            }
        }

        // The edges should be implicitly linked when merging nodes.
        return this;
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

    protected String getHashTableKey(GraphEntity entity) {
        if (entity.id != null) {
            return entity.id;
        }
        return entity.name;
    }
}
