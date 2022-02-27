package exceptions;

public class IdConstraintException extends Exception {
    public IdConstraintException(Object expected, Object actual) {
        super(String.format("Id constraint failed: %s != %s", expected, actual));
    }
}
