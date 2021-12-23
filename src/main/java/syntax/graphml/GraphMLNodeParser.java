package syntax.graphml;

import core.exceptions.SyntaxError;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GraphMLNodeParser {
    public static QueryGraph parseNodeList(NodeList nodes) throws SyntaxError {
        var result = new QueryGraph();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            QbeNode queryNode = parseQueryNode(node);
            result.put(queryNode.name, queryNode);
        }
        return result;
    }

    private static QbeNode parseQueryNode(Node node) throws SyntaxError {
        String name = GraphML.getAttribute(GraphML.NameAttribute, node);

        NodeList childNodes = node.getChildNodes();
        var qbeNode = new QbeNode(name);
        qbeNode.properties.putAll(GraphMLDataParser.parseNodeList(childNodes));

        return qbeNode;
    }
}
