package core.exceptions;

public class SyntaxError extends Exception {
    public SyntaxError(String template, Object ... arguments) {
        super(String.format(template, arguments));
    }

    public SyntaxError(String message) {
        super("Syntax error: " + message);
    }
}
