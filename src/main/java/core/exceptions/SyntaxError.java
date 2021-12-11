package core.exceptions;

public class SyntaxError extends Exception {
    public SyntaxError(String template, String ... arguments) {
        super(String.format(template, (Object[]) arguments));
    }

    public SyntaxError(String message) {
        super("Syntax error: " + message);
    }
}
