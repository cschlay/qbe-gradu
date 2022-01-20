package core.graphs;

import core.utilities.CustomStringBuilder;

import java.util.HashMap;

public class Edges extends HashMap<String, QbeEdge> {
    public String toString(int indent) {
        var ret = new CustomStringBuilder();
        for (var edge : entrySet()) {
            ret.line(indent, "%s: %s%n", edge.getKey(), edge.getValue().toString(indent + 2));
        }
        return ret.toString();
    }
}
