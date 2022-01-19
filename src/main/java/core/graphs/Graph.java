package core.graphs;

import core.exceptions.EntityNotFound;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Queue;



public abstract class Graph extends HashMap<String, QbeNode> {
    public transient Queue<QbeEdge> hangingEdges;

    public void put(QbeNode node) {
        if (node.id != null) {
            put(node.id, node);
        } else {
            put(node.name, node);
        }
    }

    /**
     * Adds an edge to the graph and links them to nodes if not already.
     * "id" is used as key if defined, otherwise "name" is used.
     * If even name is not defined, it will be put into a queue for finalization.
     *
     * @param edge to add
     */
    public void put(QbeEdge edge) {
        int links = linkEdge(edge);
        if (links == 0) {
            hangingEdges.add(edge);
        }
    }

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

    /**
     * Link the edge between multiple nodes.
     *
     * @return the number of links created
     */
    private int linkEdge(QbeEdge edge) {
        @Nullable String key = edge.id != null ? edge.id : edge.name;

        if (key == null) {
            return 0;
        }

        int links = 0;
        if (edge.headNode != null) {
            edge.headNode.edges.put(key, edge);
            put(edge.headNode.name, edge.headNode);
            links++;
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(key, edge);
            put(edge.tailNode.name, edge.tailNode);
            links++;
        }

        return links;
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
}
