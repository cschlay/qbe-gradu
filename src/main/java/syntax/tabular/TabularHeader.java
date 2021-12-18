package syntax.tabular;

public class TabularHeader {
    public final String name;
    public final String displayName;
    public final String propertyName;

    public TabularHeader(String header) {
        String[] parts = header.split("\\.");
        name = parts[0].trim();

        var property = parts[1].split("as");
        propertyName = property[0].trim();
        displayName = property.length > 1 ? property[1].trim() : header.trim();
    }

    public boolean isNode() {
        return Character.isUpperCase(name.charAt(0));
    }

    public String toString() {
        return displayName;
    }
}
