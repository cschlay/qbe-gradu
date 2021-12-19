package core.utilities;

public class Utils {
    private Utils() {}

    public static <T> T first(T[] array) {
        return array[0];
    }

    public static <T> T last(T[] array) {
        return array[array.length - 1];
    }
}
