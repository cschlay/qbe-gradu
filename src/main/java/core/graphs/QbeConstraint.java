package core.graphs;

import org.jetbrains.annotations.NotNull;


public class QbeConstraint {
    @NotNull public ConstraintType type;
    @NotNull public Object value;

    /**
     * Checks the value against the constraint type.
     * The data type should always be correct as it is checked when constructing the query graph.
     *
     * @param valueToCheck to check against the constraint
     * @return true if it meets the condition, datatype errors will yield false
     */
    public boolean check(Object valueToCheck) {
        switch (type) {
            case GREATER_THAN:
                // TODO: Implement decimal checks
                return (Integer) valueToCheck > (Integer) value;
            default:
                return false;
        }
    }

    public QbeConstraint(@NotNull ConstraintType type, @NotNull Object value) {
        this.type = type;
        this.value = value;
    }
}
