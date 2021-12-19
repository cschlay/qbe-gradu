package syntax.tabular;

import core.exceptions.SyntaxError;
import core.utilities.Utils;
import org.jetbrains.annotations.Nullable;

/** A container for header notation, it includes parsing into entity name and display names. */
public class TabularHeader {
    public final String name;
    public final String displayName;
    public final String propertyName;
    public final boolean selected;

    // Only exists for edge instances and may change if they can be implicitly deducted.
    public @Nullable String headNodeName;
    public @Nullable String tailNodeName;

    public TabularHeader(String header) throws SyntaxError {
        selected = header.endsWith("*");

        var cleanedHeader = cleanHeader(header, selected);
        String[] parts = cleanedHeader.split("\\.");
        name = parts[0].trim();

        try {
            displayName = parseDisplayName(cleanedHeader, parts);
            propertyName = parsePropertyName(parts);
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new SyntaxError(
                    "Node header '%s' should follow syntax: Node.property", header);
        }

        if (parts.length > 2) {
            // The node is an edge and contains head and tail
            tailNodeName = parts[1];
            headNodeName = parts[2];
        }
    }

    public boolean isNode() {
        return Character.isUpperCase(name.charAt(0));
    }

    public String toString() {
        return displayName;
    }

    private String parseDisplayName(String header, String[] parts) {
        String property = Utils.last(parts);

        var propertyParts = property.split("( as | AS )");
        if (propertyParts.length == 1) {
            return header;
        }

        return Utils.last(propertyParts).trim();
    }

    private String parsePropertyName(String[] parts) {
        String property = Utils.last(parts);
        return Utils.first(property.split("( as | AS )")).trim();
    }

    private String cleanHeader(String header, boolean selected) {
        if (selected) {
            return header.substring(0, header.length() - 1).trim();
        }
        return header.trim();
    }
}
