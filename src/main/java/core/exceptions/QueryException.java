package core.exceptions;

public class QueryException extends BaseException {
    public QueryException(String template, Object... arguments) {
        super(template, arguments);
    }
}
