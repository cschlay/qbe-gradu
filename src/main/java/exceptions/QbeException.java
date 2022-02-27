package exceptions;

public class QbeException extends Exception {
    public QbeException(String template, Object ... arguments) {
        super(String.format(template, arguments));
    }
}
