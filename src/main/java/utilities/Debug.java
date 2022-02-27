package utilities;

/**
 * A collection of debugging tools.
 */
public class Debug {
    private Debug() {}

    public static <T> void printList(Iterable<T> list) {
        list.forEach(item -> System.out.println(item.toString()));
    }

    public static <T> void printTable(T[][] table) {
        for (T[] ts : table) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.printf("%s %n", ts[j]);
            }
        }
    }
}
