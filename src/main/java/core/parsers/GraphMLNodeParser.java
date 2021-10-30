package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.QbeConstraint;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class GraphMLNodeParser {
    public static HashMap<String, QbeNode> parseNodeList(NodeList nodes) throws SyntaxError {
        var result = new HashMap<String, QbeNode>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            QbeNode queryNode = parseQueryNode(node);
            result.put(queryNode.name, queryNode);
        }
        return result;
    }

    private static QbeNode parseQueryNode(Node xmlNode) throws SyntaxError {
        var qbeNode = new QbeNode();
        qbeNode.name = GraphML.getAttribute("name", xmlNode);

        NodeList childNodes = xmlNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (GraphML.isDataNode(node)) {
                String key = GraphML.getAttribute("key", node);
                if (key == null) {
                    throw new SyntaxError("<data> nodes must have attribute 'key'");
                }
                QbeData dataNode = parseDataNode(node);
                qbeNode.properties.put(key, dataNode);
            }
        }

        return qbeNode;
    }

    private static QbeData parseDataNode(Node node) throws SyntaxError {
        var qbeData = new QbeData();
        @Nullable String type = GraphML.getAttribute("type", node);
        String dataType = type != null ? type : "text";

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (GraphML.isConstraintNode(node)) {
                QbeConstraint qbeConstraint = parseConstraintNode(dataType, node);
                qbeData.constraints.add(qbeConstraint);
            }
        }

        if (qbeData.constraints.isEmpty()) {
            @Nullable String textContent = node.getTextContent();
            qbeData.value = textContent != null && textContent.isEmpty() ? null : textContent;
        }

        return qbeData;
    }

    private static QbeConstraint parseConstraintNode(String dataType, Node node) throws SyntaxError {
        @Nullable String constraintType = GraphML.getAttribute("type", node);
        @Nullable String textContent = node.getTextContent();

        if (constraintType == null || textContent == null) {
            throw new SyntaxError("<constraint> nodes should have 'type' attribute and 'textContext'");
        }

        if ("integer".equals(dataType)) {
            return new QbeConstraint(constraintType, Integer.parseInt(textContent));
        }

        throw new SyntaxError(String.format("Datatype '%s' for <data> nodes is not supported", dataType));
    }

}
