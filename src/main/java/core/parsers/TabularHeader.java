package core.parsers;

public class TabularHeader {
    public final String name;
    public final String displayName;
    public final String propertyName;

    public TabularHeader(String header) {
        String[] parts = header.split("\\.");
        name = parts[0];

        var property = parts[1].split(" as ");
        propertyName = property[0];
        displayName = property.length > 1 ? property[1] : header;
    }

    public boolean isNode() {
        return Character.isUpperCase(name.charAt(0));
    }

    public String toString() {
        return displayName;
    }
}
