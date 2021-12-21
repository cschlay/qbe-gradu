package core.graphs;

import java.util.HashMap;
import java.util.List;

public class Edges extends HashMap<String, QbeEdge> {
    public void addAllById(List<QbeEdge> edges) {
        for (QbeEdge edge : edges) {
            put(edge.id, edge);
        }
    }
}
