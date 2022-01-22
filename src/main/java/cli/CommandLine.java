package cli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Scanner;

/**
 * A collection CLI operations.
 */
public class CommandLine {
    public static final String PRINT_DATABASE = "print";
    public static final String QUERY = "query";
    public static final String QUIT = "quit";
    public static final String RESET_DATABASE = "reset";
    public static final String SEED_DATABASE = "seed";

    private final Scanner scanner;

    public CommandLine() {
        scanner = new Scanner(System.in);
    }

    public void close() {
        scanner.close();
    }

    // A helper function to print with arguments.
    public void print(@NotNull String template, Object ...arguments) {
        System.out.printf(template, arguments);
    }

    public void println(@NotNull String template, Object ...arguments) {
        String message = String.format(template, arguments);
        System.out.println(message);
    }

    public void printHelp() {
        println("Commands:");
        println("  %s - prints the database", PRINT_DATABASE);
        println("  %s - resets the database", RESET_DATABASE);
        println("  %s - starts the query", QUERY);
        println("  %s - seeds the database", SEED_DATABASE);
        println("  %s - close the session", QUIT);
    }

    /**
     * It may read multiple strings, especially when querying where consecutive newlines terminate the input

     * @return the input
     */
    public String read() {
        @Nullable StringBuilder multilineInput = null;

        while (true) {
            String line = scanner.nextLine();

            if (multilineInput != null) {
                if ("".equals(line)) {
                    return multilineInput.toString();
                } else {
                    multilineInput.append(line);
                }
            } else if (QUERY.equals(line)) {
                print("Enter your query:\n");
                multilineInput = new StringBuilder();
            } else {
                return line;
            }
        }
    }
}
