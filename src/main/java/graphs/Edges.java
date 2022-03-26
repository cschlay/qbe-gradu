package graphs;

import utilities.CustomStringBuilder;

import java.util.HashMap;

/**
 * A container class for edges, almost same as HashMap.
 */
public class Edges extends HashMap<String, QbeEdge> {
    public QbeEdge get(String name, String tail, String head) {
        return get(String.format("%s-%s.%s", name, tail, head));
    }

    public String toString(int indent) {
        var ret = new CustomStringBuilder();
        for (var edge : entrySet()) {
            ret.line(indent, "%s%n", edge.getValue().toString(indent + 2));
        }
        return ret.toString();
    }
}
