package core.parsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class GraphMLAttributes {
    public static String Id = "id";
    public static String NodeName = "name";

    /** Attribute name i.e. <data key="" /> */
    public static String Key = "key";

    /** Attribute type i.e. <data type="" /> */
    public static String Type = "type";

    /** Comparison operators */
    public static String GreaterThan = "gt";
    public static String GreaterOrEqual = "gte";
    public static String LessThan = "lt";
    public static String LessOrEqual = "lte";

    public static String getNodeName(Node node) {
        return readAttribute("name", node);
    }

    @Nullable
    private static String readAttribute(@NotNull String attributeName, @NotNull Node node) {
        @Nullable NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            @Nullable Node attribute = attributes.getNamedItem(attributeName);
            if (attribute != null) {
                return attribute.getNodeValue();
            }
        }
        return null;
    }
}
