package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.ConstraintType;
import core.graphs.QbeConstraint;
import core.graphs.QbeData;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class GraphMLDataParser {
    public static HashMap<String, QbeData> parseNodeList(NodeList nodes) throws SyntaxError {
        var qbeDataList = new HashMap<String, QbeData>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (GraphML.isDataNode(node)) {
                String key = GraphML.getAttribute("key", node);
                if (key == null) {
                    throw new SyntaxError("<data> nodes must have attribute 'key'");
                }
                qbeDataList.put(key, parseDataNode(node));
            }
        }

        return qbeDataList;
    }

    public static QbeData parseDataNode(Node node) throws SyntaxError {
        var qbeData = new QbeData();
        assert qbeData.constraints != null;

        @Nullable String type = GraphML.getAttribute("type", node);
        String dataType = type != null ? type : "text";

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (GraphML.isConstraintNode(childNode)) {
                qbeData.constraints.add(parseConstraintNode(dataType, childNode));
            }
        }

        if (qbeData.constraints.isEmpty()) {
            @Nullable String textContent = node.getTextContent();
            if (textContent != null && !textContent.isBlank()) {
                qbeData.value = textContent.trim();
            }
        }

        return qbeData;
    }

    public static QbeConstraint parseConstraintNode(String dataType, Node node) throws SyntaxError {
        @Nullable String constraintTypeName = GraphML.getAttribute("type", node);
        @Nullable String textContent = node.getTextContent();

        if (constraintTypeName == null || textContent == null) {
            throw new SyntaxError("<constraint> nodes should have 'type' attribute and 'textContext'");
        }

        ConstraintType constraintType = GraphML.getConstraintType(constraintTypeName);
        if ("integer".equals(dataType)) {
            return new QbeConstraint(constraintType, Integer.parseInt(textContent.trim()));
        }

        throw new SyntaxError(String.format("Datatype '%s' for <data> nodes is not supported", dataType));
    }
}
