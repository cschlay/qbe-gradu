package utilities;


/**
 * Provides comparators for Object -data type that are used in store dynamic values
 * in Neo4j database and graph entities.
 *
 * The implementation is minimalistic and only support "double" and "int" data types.
 * The comparison uses "double" as a common data type so that comparison is possible between double and int.
 */
public class Comparison {
    private Comparison() {}

    public static boolean greaterThan(Object left, Object right) {
        return normalize(left) > normalize(right);
    }

    public static boolean greaterThanOrEqualTo(Object left, Object right) {
        return normalize(left) >= normalize(right);
    }

    public static boolean lessThan(Object left, Object right) {
        return normalize(left) < normalize(right);
    }

    public static boolean lessThanOrEqualTo(Object left, Object right) {
        return normalize(left) <= normalize(right);
    }

    /**
     * Normalizes Object numbers so that they can be compared.
     *
     * @param number to normalize
     * @return number in normalized format
     */
    private static double normalize(Object number) {
        if (number instanceof Integer) {
            return (int) number;
        }

        return (double) number;
    }
}
