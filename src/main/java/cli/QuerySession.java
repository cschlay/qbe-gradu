package cli;

import org.neo4j.graphdb.GraphDatabaseService;

import java.util.Scanner;

public class QuerySession {
    private GraphDatabaseService db;

    public void start() {
        System.out.println("QBE for Graph Database Prototype");

        var scanner = new Scanner(System.in);


        boolean run = true;
        do {
            System.out.print("qbe>");
            String input = scanner.nextLine();
            System.out.println(input);
            if (input.equals("q")) {
                run = false;
            }
        } while (run);
    }

    public QuerySession(GraphDatabaseService db) {
        this.db = db;
    }
}
