package exceptions;

public class SyntaxError extends QbeException {
    public SyntaxError(String template, Object... arguments) {
        super(template, arguments);
    }
}
