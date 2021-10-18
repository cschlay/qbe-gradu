package core.graphs;

import java.util.ArrayList;

/**
 * Used to represent nodes for queries. The results use different node.
 */
public class QbeNode {
    public String name = null;

    // Adjacency list like representation but wrapped inside a container.
    public ArrayList<QbeEdge> edges = new ArrayList<>();
}
