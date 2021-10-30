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


    public boolean isInteger() {
        return value instanceof Integer;
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
