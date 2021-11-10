package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.ConstraintType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static core.graphs.ConstraintType.GREATER_THAN;

public class GraphML {
    public static String Edge = "edge";
    public static String Graph = "graph";
    public static String Node = "node";
    public static String Data = "data";
    public static String Constraint = "constraint";

    public static String TypeBoolean = "boolean";
    public static String TypeInteger = "integer";
    public static String TypeText = "text";
    public static String TypeGreaterThan = "gt";

    public static String HiddenAttribute = "hidden";
    public static String IdAttribute = "id";
    public static String KeyAttribute = "key";
    public static String NameAttribute = "name";
    public static String SourceAttribute = "source";
    public static String TargetAttribute = "target";
    public static String TransitiveAttribute = "transitive";
    public static String TypeAttribute = "type";

    public static boolean isConstraintNode(Node node) {
        return Constraint.equals(node.getNodeName());
    }

    public static boolean isDataNode(Node node) {
        return Data.equals(node.getNodeName());
    }

    public static boolean isHidden(Node node) {
        String hiddenAttribute = getAttribute(GraphML.HiddenAttribute, node);
        return "true".equals(hiddenAttribute);
    }
    public static boolean isTransitiveEdge(Node node) {
        String transitiveAttribute = getAttribute(GraphML.TransitiveAttribute, node);
        return "true".equals(transitiveAttribute);
    }

    public static ConstraintType getConstraintType(@NotNull String type) throws SyntaxError {
        if (TypeGreaterThan.equals(type)) {
            return GREATER_THAN;
        } else {
            var message = String.format("Constraint type %s is not supported", type);
            throw new SyntaxError(message);
        }
    }

    @Nullable
    public static String getAttribute(@NotNull String name, @NotNull Node node) {
        @Nullable NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            @Nullable Node valueNode = attributes.getNamedItem(name);
            if (valueNode != null) {
                return valueNode.getNodeValue();
            }
        }
        return null;
    }
}
