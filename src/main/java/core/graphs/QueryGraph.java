package core.graphs;

import core.interfaces.Graphable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.logging.Logger;


public class QueryGraph implements Graphable {
    @NotNull
    public HashMap<String, QbeNode> nodes;
    private final Logger logger = Logger.getLogger(QueryGraph.class.getName());

    /**
     * Adds an edge to the graph. Be sure to add the nodes first!
     * @param edge to connect exactly two nodes
     */
    public void addEdge(@NotNull QbeEdge edge) {
        edge.endNode = nodes.get(edge.endNodeName);

        if (edge.startNodeName != null) {
            // The best case, where start node exists, makes it easier for traversal
            QbeNode startNode = nodes.get(edge.startNodeName);
            startNode.edges.add(edge);
        } else if (edge.endNodeName != null) {
            // If the start node is not defined, we need to lookup in reverse
        } else {
            // Both start end node is missing.
        }
        // TODO: solve this situation
    }

    /**
     * Adds node to the graph.
     * @param node the node to add, must contain unique name or no name
     */
    public void addNode(@NotNull QbeNode node) {
        if (nodes.containsKey(node.name)) {
            logger.warning(String.format("Node named '%s' is already defined, ignoring node %s", node.name, node));
        } else {
            // Only one anonymous node (null as name) can be added
            nodes.put(node.name, node);
        }
    }

    public QueryGraph() {
        nodes = new HashMap<>();
    }
}
