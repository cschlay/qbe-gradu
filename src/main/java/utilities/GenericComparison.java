package utilities;

public class GenericComparison {
    public static boolean isGreaterThan(Object a, Object b) {
        if (a instanceof Integer && b instanceof Integer) {
            return (int) a > (int) b;
        }
        return false;    }

    public static boolean isGreaterThanOrEqual(Object a, Object b) {
        if (a instanceof Integer && b instanceof Integer) {
            return (int) a >= (int) b;
        }
        return false;
    }

    public static boolean isLessThanOrEqual(Object a, Object b) {
        if (a instanceof Integer && b instanceof Integer) {
            return (int) a <= (int) b;
        }
        return false;
    }

}
