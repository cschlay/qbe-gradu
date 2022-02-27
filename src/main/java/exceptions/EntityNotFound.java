package exceptions;

public class EntityNotFound extends QbeException {
    public EntityNotFound(String template, Object... arguments) {
        super(template, arguments);
    }
}
