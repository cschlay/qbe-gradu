package core.utilities;

public class Utils {
    private Utils() {}

    public static <T> T first(T[] array) {
        return array[0];
    }

    public static <T> T last(T[] array) {
        return array[array.length - 1];
    }

    public static boolean startsWithUppercase(String value) {
        if (value.length() > 0) {
            return Character.isUpperCase(value.charAt(0));
        }
        return false;
    }
}
