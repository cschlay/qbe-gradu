package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class GraphMLEdgeParser {
    public static HashMap<String, QbeNode> parseNodeList(NodeList nodes, HashMap<String, QbeNode> qbeNodes) throws SyntaxError {
        for (int i = 0; i < nodes.getLength(); i++) {
            QbeEdge qbeEdge = parseEdgeNode(nodes.item(i));

            // The edge is added to both source and target nodes since traversal for both direction is needed.
            if (qbeEdge.tailNodeName != null) {
                QbeNode qbeNode = qbeNodes.get(qbeEdge.tailNodeName);
                qbeNode.edges.add(qbeEdge);
            }

            if (qbeEdge.headNodeName != null) {
                QbeNode qbeNode = qbeNodes.get(qbeEdge.headNodeName);
                qbeNode.edges.add(qbeEdge);
            }
        }

        return qbeNodes;
    }

    public static QbeEdge parseEdgeNode(Node node) throws SyntaxError {
        @Nullable String name = GraphML.getAttribute("name", node);
        @Nullable String tailNodeName = GraphML.getAttribute("source", node);
        @Nullable String headNodeName = GraphML.getAttribute("target", node);

        HashMap<String, QbeData> properties = GraphMLDataParser.parseNodeList(node.getChildNodes());
        return new QbeEdge(name, tailNodeName, headNodeName, properties);
    }
}
