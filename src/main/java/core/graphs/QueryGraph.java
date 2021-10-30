package core.graphs;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


// TODO: Just extend HashMap<String, QbeNode> since it doesn't contain any other properties
public class QueryGraph {
    /** The key is name of the node. There may exist one anonymous node with "null" key. */
    @NotNull
    public HashMap<String, QbeNode> nodes;

    public QueryGraph() {
        nodes = new HashMap<>();
    }
}
