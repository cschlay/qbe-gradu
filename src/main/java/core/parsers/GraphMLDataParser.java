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
                String key = GraphML.getAttribute(GraphML.KeyAttribute, node);
                if (key == null) {
                    throw new SyntaxError("Data node attribute 'key' is not defined!");
                }
                qbeDataList.put(key, parseDataNode(node));
            }
        }

        return qbeDataList;
    }

    public static QbeData parseDataNode(Node node) throws SyntaxError {
        var qbeData = new QbeData();
        qbeData.isHidden = GraphML.isHidden(node);
        assert qbeData.constraints != null;

        @Nullable String type = GraphML.getAttribute(GraphML.TypeAttribute, node);
        String dataType = type != null ? type : "text";

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (GraphML.isConstraintNode(childNode)) {
                qbeData.constraints.add(parseConstraintNode(dataType, childNode));
            }
        }

        if (qbeData.constraints.isEmpty()) {
            qbeData.value = castTextContent(dataType, node.getTextContent());
        }

        return qbeData;
    }

    public static QbeConstraint parseConstraintNode(String dataType, Node node) throws SyntaxError {
        @Nullable String constraintTypeName = GraphML.getAttribute(GraphML.TypeAttribute, node);
        @Nullable String textContent = node.getTextContent();

        if (constraintTypeName == null || textContent == null) {
            throw new SyntaxError("Constraint 'type' attribute or 'textContext' value is missing!");
        }

        ConstraintType constraintType = GraphML.getConstraintType(constraintTypeName);
        Object value = castTextContent(dataType, textContent);
        if (value == null) {
            throw new SyntaxError("Constraint 'type' and 'value' need to be defined!");
        }
        return new QbeConstraint(constraintType, value);
    }

    @Nullable private static Object castTextContent(String dataType, @Nullable String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        String value = rawValue.trim();
        if ("integer".equals(dataType)) {
            return Integer.parseInt(value);
        } else if ("boolean".equals(dataType)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }
}
