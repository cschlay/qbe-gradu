package core.utilities;

/**
 * Java does not implement arithmetics for object types, so each type must be checked first.
 */
public class Numbers {
    private Numbers() {}

    /**
     * Performs simple addition "a + b" where both are unknown objects.
     *
     * @param a the left-side value
     * @param b the right-side value
     * @return the value of a + b
     */
    public static Object plus(Object a, Object b) throws IllegalArgumentException {
        if (a instanceof Integer && b instanceof Integer) {
            return (int) a + (int) b;
        }

        if (a instanceof Number && b instanceof Number) {
            // Otherwise, cast all into doubles
            double aDouble = ((Number) a).doubleValue();
            double bDouble = ((Number) b).doubleValue();
            return aDouble + bDouble;
        }

        throw new IllegalArgumentException(String.format("Cannot perform addition %s + %s", a, b));
    }

    // TODO: Implement other arithmetic operators.
}
