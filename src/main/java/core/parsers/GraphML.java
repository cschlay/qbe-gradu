package core.parsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
