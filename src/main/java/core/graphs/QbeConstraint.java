package core.graphs;

import core.exceptions.SyntaxError;
import core.parsers.GraphMLAttributes;
import org.jetbrains.annotations.NotNull;

public class QbeConstraint {
    @NotNull public ConstraintType type;
    @NotNull public Object value;

    public QbeConstraint(@NotNull String type, @NotNull Object value) throws SyntaxError {
        if (GraphMLAttributes.GreaterOrEqual.equals(type)) {
            this.type = ConstraintType.GREATER_THAN;
        } else {
            var message = String.format("Constraint type %s is not supported", type);
            throw new SyntaxError(message);
        }
        this.value = value;
    }
}
