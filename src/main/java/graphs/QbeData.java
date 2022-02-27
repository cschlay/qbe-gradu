package graphs;

import enums.QueryType;
import org.jetbrains.annotations.Nullable;

/**
 * Data node which is a container for property values.
 * It is also used to verify constraints.
 */
public class QbeData {
    public @Nullable QueryType type;
    public @Nullable Object operationArgument;
    public @Nullable Object value;

    public final boolean nullable;
    public boolean selected;
    /** Value to update node */
    public @Nullable Object update;

    public QbeData(@Nullable Object value) {
        this.nullable = false;
        this.selected = true;
        this.value = value;
    }

    public QbeData (@Nullable Object value, boolean selected, boolean nullable) {
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

        if (this.value instanceof LogicalExpression) {
            return ((LogicalExpression) this.value).evaluate(value);
        }

        return checkEquality(value);
    }

    public String toString() {
        return "" + value;
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