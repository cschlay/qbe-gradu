package core.graphs;

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

    /** If the field is nullable. */
    public boolean isNullable;

    /**
     * Checks a value against every constraint defined.
     * @param valueToCheck to check against constraints
     * @return true if it passes all checks
     */
    public boolean checkConstraints(Object valueToCheck) {
        if (constraints == null ||constraints.isEmpty()) {
            return value == null || valueToCheck.equals(value);
        }

        for (var constraint : constraints) {
            if (!constraint.check(valueToCheck)) {
                return false;
            }
        }

        return true;
    }

    public boolean isString() {
        return value instanceof  String;
    }


    public QbeData () {
        constraints = new ArrayList<>();
    }

    public QbeData(@NotNull Object value) {
        this.value = value;
        constraints = null;
    }
}
