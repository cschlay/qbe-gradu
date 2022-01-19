package core.exceptions;

public class SyntaxError extends BaseException {
    public SyntaxError(String template, Object... arguments) {
        super(template, arguments);
    }
}
