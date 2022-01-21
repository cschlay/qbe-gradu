package core.exceptions;

public class QueryException extends QbeException {
    public QueryException(String template, Object... arguments) {
        super(template, arguments);
    }
}
