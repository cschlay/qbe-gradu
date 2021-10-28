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
    @NotNull public ArrayList<QbeConstraint> constraints;


    public boolean isInteger() {
        return value instanceof Integer;
    }

    public boolean isString() {
        return value instanceof  String;
    }

    public QbeData () {
        constraints = new ArrayList<>();
    }
}
