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

    public static boolean isDataNode(org.w3c.dom.Node node) {
        return "data".equals(node.getNodeName());
    }
    public static boolean isConstraintNode(Node node) { return "constraint".equals(node.getNodeName()); }

    public static ConstraintType getConstraintType(@NotNull String type) throws SyntaxError {
        if ("gt".equals(type)) {
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
