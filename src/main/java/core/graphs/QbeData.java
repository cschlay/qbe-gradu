package core.graphs;

import core.parsers.GraphML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Data node which is a container for values and metadata.
 */
public class QbeData {
    /** The value can be null if constraints are defined. */
    @Nullable public Object value;
    @Nullable public ArrayList<QbeConstraint> constraints;

    public boolean isHidden;

    /**
     * Checks a value against every constraint defined.
     * @param valueToCheck to check against constraints
     * @return true if it passes all checks
     */
    public boolean checkConstraints(Object valueToCheck) {
        if (constraints == null ||constraints.isEmpty()) {
            return value == null || checkEquality(valueToCheck);
        }

        for (var constraint : constraints) {
            if (!constraint.check(valueToCheck)) {
                return false;
            }
        }

        return true;
    }

    public String getType()
    {
        if (value instanceof Boolean) {
            return GraphML.TypeBoolean;
        } else if (value instanceof Integer) {
            return GraphML.TypeInteger;
        }

        return GraphML.TypeText;
    }

    private boolean checkEquality(Object valueToCheck) {
        assert value != null;
        // Checks if values are equal after casting them into respective data types.
        if (valueToCheck instanceof String) {
            return ((String) valueToCheck).matches((String) value);
        }
        // Default to built-in equality, works with primitives at least.
        return valueToCheck.equals(value);
    }

    public QbeData () {
        constraints = new ArrayList<>();
    }

    public QbeData(@NotNull Object value) {
        this.value = value;
        constraints = null;
    }
}
