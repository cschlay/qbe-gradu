package utilities;

import java.util.List;

public class Debug {
    public static <T> void printList(Iterable<T> list) {
        list.forEach(item -> {
            System.out.println(item.toString());
        });
    }
}
