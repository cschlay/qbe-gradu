package graphs;

import utilities.CustomStringBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents graph nodes.
 * Every known nodes have id and names.
 * For query nodes the name and id are optional.
 */
public class QbeNode extends GraphEntity {
    public Edges edges;

    public QbeNode(@Nullable String name) {
        super(name);
        edges = new Edges();
    }

    public QbeNode(long id, @Nullable String name) {
        super(id, name);
        edges = new Edges();
    }

    public boolean equalByName(@Nullable QbeNode otherNode) {
        return name != null && otherNode != null && name.equals(otherNode.name);
    }

    /**
     * Merges the node properties and edges to current instance.
     *
     * @param nodeB to merge
     * @return merged instance
     */
    public QbeNode merge(QbeNode nodeB) {
        super.mergeProperties(nodeB);

        for (QbeEdge edgeB : nodeB.edges.values()) {
            @Nullable QbeEdge edgeA = edges.get(edgeB.id);
            if (edgeA != null) {
                edges.put(edgeA.id, edgeA.merge(edgeB));
            } else {
                edges.put(edgeB.id, edgeB);
                // WARNING: The tail or head node might not exist, so it works only if merge is used with graph merge.
            }
        }

        return this;
    }

    @Override public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        var ret = new CustomStringBuilder();
        ret.line(indent, "%s %s%s\n", name, type, selected ? "*" : "");
        for (var property : properties.entrySet()) {
            ret.line(indent + 2, "%s = %s%s\n", property.getKey(), property.getValue(), property.getValue().selected ? "*" : "");
        }
        ret.line(edges.toString(indent + 2));
        return ret.toString();
    }
}
