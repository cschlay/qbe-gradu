package exceptions;

public class InvalidNodeException extends Exception {
    public InvalidNodeException() {
        super("Node is not valid");
    }

    public InvalidNodeException(String template, Object ... arguments) {
        super(String.format(template, arguments));
    }
}
