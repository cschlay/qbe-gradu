package core.graphs;

import core.utilities.CustomStringBuilder;

import java.util.HashMap;

public class Edges extends HashMap<String, QbeEdge> {
    public String toString(int indent) {
        var ret = new CustomStringBuilder();
        for (var edge : entrySet()) {
            ret.line(indent, "%s%n", edge.getValue().toString(indent + 2));
        }
        return ret.toString();
    }
}
