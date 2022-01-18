package syntax.tabular;

import core.utilities.Utils;
import org.jetbrains.annotations.Nullable;

/** A container for header notation, it includes parsing into entity name and display names. */
public class TabularHeader {
    @Nullable public TabularHeaderType type;

    /** Entity to associate header with */
    @Nullable public String entityName;
    /** Name used in database */
    public final String name;
    /** Name to return in final result */
    public final String displayName;
    public final boolean selected;

    // Only exists for edge instances and may change if they can be implicitly deducted.
    @Nullable public String headNodeName;
    @Nullable public String tailNodeName;

    public TabularHeader(String header) {
        selected = header.lastIndexOf('*') != -1;

        String cleanedHeader = cleanHeader(header, selected);
        String[] parts = cleanedHeader.split("\\.");

        name = parsePropertyName(parts);
        displayName = parseDisplayName(parts, cleanedHeader);
        if (parts.length == 2) {
            entityName = Utils.first(parts);
        }
    }

    public String toString() {
        return displayName;
    }

    private String cleanHeader(String header, boolean selected) {
        if (selected) {
            return header.replace("*", "").trim();
        }
        return header.trim();
    }

    private String parseDisplayName(String[] parts, String header) {
        String property = Utils.last(parts);
        String[] propertyParts = property.split("( as | AS )");
        if (propertyParts.length == 1) {
            return header;
        }

        return Utils.last(propertyParts).trim();
    }

    private String parsePropertyName(String[] parts) {
        String property = Utils.last(parts);
        return Utils.first(property.split("( as | AS )")).trim();
    }
}
