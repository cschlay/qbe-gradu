package core.utilities;

// Improved alternative to StringBuilder with formatting.
public class CustomStringBuilder {
    private final StringBuilder sb;

    public CustomStringBuilder() {
        sb = new StringBuilder();
    }

    public void line(String template, Object ... arguments) {
        sb.append(String.format(template, arguments));
    }

    public void line(int indent, String template, Object ... arguments) {
        sb.append(" ".repeat(indent));
        line(template, arguments);
    }


    @Override
    public String toString() {
        return sb.toString();
    }
}
