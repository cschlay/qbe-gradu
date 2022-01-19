package core.exceptions;

public class BaseException  extends Exception {
    public BaseException(String template, Object ... arguments) {
        super(String.format(template, arguments));
    }
}
