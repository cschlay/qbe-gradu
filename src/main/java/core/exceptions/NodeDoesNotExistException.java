package core.exceptions;

public class NodeDoesNotExistException extends Exception {
    public NodeDoesNotExistException(String nodeName) {
        super(String.format("Node named %s doesn't exist!", nodeName));
    }
}
