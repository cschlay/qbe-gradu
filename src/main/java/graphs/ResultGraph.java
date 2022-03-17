package graphs;

import exceptions.QbeException;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class ResultGraph extends Graph {
    public final transient Set<QbeEdge> unvisitedEdges;

    public ResultGraph() {
        unvisitedEdges = new HashSet<>();
    }

    public int order() {
        return super.size();
    }

    public ResultGraph put(QbeNode node) {
        String key = node.id == null ? node.name : node.id;
        put(key, node);

        return this;
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
            put(edge.tailNode.id, edge.tailNode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResultGraph that = (ResultGraph) o;
        return Objects.equals(unvisitedEdges, that.unvisitedEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unvisitedEdges);
    }
}
