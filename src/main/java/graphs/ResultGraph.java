package graphs;

import exceptions.QbeException;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;


public class ResultGraph extends Graph {
    public final transient Set<QbeEdge> unvisitedEdges;

    public ResultGraph() {
        unvisitedEdges = new HashSet<>();
    }

    public int order() {
        return super.size();
    }

    public void put(QbeNode node) {
        String key = node.id == null ? node.name : node.id;
        put(key, node);
    }

    public void put(@NotNull QbeEdge edge) throws QbeException {
        if (edge.id == null) {
            throw new QbeException("Edge '%s' doesn't have id!", edge.name);
        }

        if (edge.headNode != null) {
            edge.headNode.edges.put(edge.id, edge);
            put(edge.headNode.id, edge.headNode);
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(edge.id, edge);
            put(edge.tailNode.id, edge.headNode);
        }
    }
}
