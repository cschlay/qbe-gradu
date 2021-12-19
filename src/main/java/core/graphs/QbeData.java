package core.graphs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data node which is a container for property values.
 * It is also used to verify constraints.
 */
public class QbeData {
    // A list of check constraints. https://en.wikipedia.org/wiki/Check_constraint
    public List<QbeConstraint> constraints;
    @Nullable public Object value;

    public final boolean nullable;
    public final boolean selected;

    public QbeData(@Nullable Object value) {
        constraints = new ArrayList<>();
        this.nullable = false;
        this.selected = true;
        this.value = value;
    }

    public QbeData (@Nullable Object value, boolean selected, boolean nullable) {
        constraints = new ArrayList<>();
        this.nullable = nullable;
        this.selected = selected;
        this.value = value;
    }

    /**
     * Checks a value against constraints.
     *
     * @param value to check against constraints
     * @return true if it passes all checks
     */
    public boolean check(@Nullable Object value) {
        if (value == null) {
            return nullable;
        }

        if (constraints.isEmpty()) {
            return checkEquality(value);
        }

        for (var constraint : constraints) {
            if (!constraint.check(value)) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        return "Data(" + value + ")";
    }

    private boolean checkEquality(Object valueToCheck) {
        if (value == null) {
            return true;
        }

        if (value instanceof String && valueToCheck instanceof String) {
            return ((String) valueToCheck).matches((String) value);
        }

        try {
            return valueToCheck.equals(value);
        } catch (ClassCastException exception) {
            return false;
        }
    }
}
