package core.exceptions;

public class EntityNotFound extends BaseException {
    public EntityNotFound(String template, Object... arguments) {
        super(template, arguments);
    }
}
