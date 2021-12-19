package core.graphs;

import java.util.HashMap;

public abstract class Graph extends HashMap<String, QbeNode> {
    public void addEdge(QbeEdge edge) {
        if (edge.headNode != null) {
            edge.headNode.edges.add(edge);
            put(edge.headNode.name, edge.headNode);
        }

        if (edge.tailNode != null) {
            edge.tailNode.edges.add(edge);
            put(edge.tailNode.name, edge.tailNode);
        }
    }
}
