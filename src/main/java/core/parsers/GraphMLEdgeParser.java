package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.QbeData;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class GraphMLEdgeParser {
    public static void parseNodeList(NodeList nodes, QueryGraph graph) throws SyntaxError {
        for (int i = 0; i < nodes.getLength(); i++) {
            QbeEdge qbeEdge = parseEdgeNode(nodes.item(i));

            // The edge is added to both source and target nodes since traversal for both direction is needed.
            if (qbeEdge.tailNodeName != null) {
                QbeNode qbeNode = graph.get(qbeEdge.tailNodeName);
                qbeNode.edges.add(qbeEdge);
            }

            if (qbeEdge.headNodeName != null) {
                QbeNode qbeNode = graph.get(qbeEdge.headNodeName);
                qbeNode.edges.add(qbeEdge);
            }
            // TODO: Handle null names
        }
    }

    public static QbeEdge parseEdgeNode(Node node) throws SyntaxError {
        @Nullable String name = GraphML.getAttribute(GraphML.NameAttribute, node);
        @Nullable String tailNodeName = GraphML.getAttribute(GraphML.SourceAttribute, node);
        @Nullable String headNodeName = GraphML.getAttribute(GraphML.TargetAttribute, node);

        HashMap<String, QbeData> properties = GraphMLDataParser.parseNodeList(node.getChildNodes());
        return new QbeEdge(name, tailNodeName, headNodeName, properties);
    }
}
