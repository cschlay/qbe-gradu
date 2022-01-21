package core.graphs;

import core.exceptions.EntityNotFound;
import core.utilities.CustomStringBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Queue;



public abstract class Graph extends HashMap<String, QbeNode> {
    public transient Queue<QbeEdge> hangingEdges;

    public QbeEdge edge(String nodeId, String edgeId) throws EntityNotFound {
        @Nullable QbeNode node = get(nodeId);
        if (node == null) {
            throw new EntityNotFound("Node '%s' not found, when looking for edge '%s'.", nodeId, edgeId);
        }

        @Nullable QbeEdge edge = node.edges.get(edgeId);
        if (edge != null) {
            return edge;
        }

        throw new EntityNotFound("Node '%s' doesn't have edge '%s'.", nodeId, edgeId);
    }

    // Not part of the implementation
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
            ret.line("%s: %s", node.getKey(), node.getValue().toString());
        }
        return ret.toString();
    }
}
