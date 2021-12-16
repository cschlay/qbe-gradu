package core.utilities;

import java.util.List;

public class Debug {
    public static <T> void printList(Iterable<T> list) {
        list.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    public static <T> void printTable(T[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.printf("%s %n", table[i][j]);
            }
        }
    }
}
