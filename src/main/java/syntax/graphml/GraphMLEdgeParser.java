package syntax.graphml;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QueryGraph;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class GraphMLEdgeParser {
    public static void parseNodeList(NodeList nodes, QueryGraph graph) throws SyntaxError {
        for (int i = 0; i < nodes.getLength(); i++) {
            QbeEdge qbeEdge = parseEdgeNode(nodes.item(i), graph);

            // The edge is added to both source and target nodes since traversal for both direction is needed.
            if (qbeEdge.tailNode != null) {
                qbeEdge.tailNode.edges.add(qbeEdge);
            }

            if (qbeEdge.headNode != null) {
                qbeEdge.headNode.edges.add(qbeEdge);
            }
            // TODO: Handle null names
        }
    }

    public static QbeEdge parseEdgeNode(Node node, QueryGraph graph) throws SyntaxError {
        @Nullable String name = GraphML.getAttribute(GraphML.NameAttribute, node);
        @Nullable String tailNodeName = GraphML.getAttribute(GraphML.SourceAttribute, node);
        @Nullable String headNodeName = GraphML.getAttribute(GraphML.TargetAttribute, node);

        HashMap<String, QbeData> properties = GraphMLDataParser.parseNodeList(node.getChildNodes());

        var edge = new QbeEdge(name);
        edge.properties = properties;
        edge.tailNode = graph.get(tailNodeName);
        edge.headNode = graph.get(headNodeName);
        edge.isHidden = GraphML.isHidden(node);
        edge.isTransitive = GraphML.isTransitiveEdge(node);
        return edge;
    }
}
