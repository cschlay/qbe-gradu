package cli;

import core.db.neo4j.Neo4jTraversal;
import core.exceptions.SyntaxError;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import core.parsers.GraphMLParser;
import demo.CourseGraphDemo;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Scanner;

/** Session for taking user input from CLI interface. */
public class QuerySession {
    private final GraphDatabaseService db;
    private final GraphMLParser parser = new GraphMLParser();

    /**
     * Starts the query session.
     */
    public void start() {
        System.out.println("QBE for Graph Database Prototype");
        printCommands();

        var scanner = new Scanner(System.in);
        boolean run = true;

        do {
            System.out.print("\nqbe> ");
            String input = scanner.nextLine();

            if (input.equals(CLICommands.QUIT)) {
                run = false;
            }  else {
                executeCommand(input);
            }
        } while (run);
    }

    public void executeCommand(String input) {
        // Do not use switch, it could contain multiple arguments.
        if (input.equals(CLICommands.PRINT_DATABASE)) {
            printDatabaseDetails();
        } else if (input.equals(CLICommands.RESET_DATABASE)) {
            resetDatabase();
            printDatabaseDetails();
        } else if (input.equals(CLICommands.SEED_DATABASE)) {
            System.out.println("Seeding the database...");
            CourseGraphDemo.seedEducationData(this.db);
            printDatabaseDetails();
        } else {
            // Defaults to executing query
            try {
                ResultGraph resultGraph = processQuery(input);
                System.out.println();
                System.out.println(resultGraph.toGraphML());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void resetDatabase() {
        System.out.println("Resetting database...");
        try (var tx = db.beginTx()) {
            tx.getAllNodes().forEach(Node::delete);
            tx.getAllRelationships().forEach(Relationship::delete);
            tx.commit();
        }
    }

    private void printCommands() {
        System.out.println("Commands: ");
        System.out.println(CLICommands.PRINT_DATABASE + " - prints the database");
        System.out.println(CLICommands.RESET_DATABASE + " - reset the database");
        System.out.println(CLICommands.SEED_DATABASE + " - seed the database with test data");
        System.out.println(CLICommands.QUIT + " - quit the program");
    }

    private void printDatabaseDetails() {
        try (var tx = db.beginTx()){
            System.out.println("Node {");
            tx.getAllNodes().forEach(node -> {
                System.out.printf("\t %s: ", node.getId());
                node.getLabels().forEach(label -> System.out.print(label.name()));
                System.out.print("( ");
                node.getAllProperties().forEach((key, property) -> System.out.printf("%s: %s, ", key, property));
                System.out.println(")");
            });
            System.out.println("}");

            System.out.println("Edges {");
            tx.getAllRelationships().forEach(relationship -> {
                System.out.printf(
                        "\t %s: %s --> %s %s( ",
                        relationship.getId(),
                        relationship.getStartNodeId(),
                        relationship.getEndNodeId(),
                        relationship.getType()
                );
                relationship.getAllProperties().forEach((key, property) -> System.out.printf("%s: %s, ", key, property));
                System.out.println(")");
            });
            System.out.println("}");
        }
    }

    public ResultGraph processQuery(String query) throws Exception {
        // Can extend to support different query languages as long as they construct same QueryGraph.
        QueryGraph queryGraph = parser.parse(query);
        return new Neo4jTraversal(db, queryGraph).buildResultGraph();
    }

    public QuerySession(GraphDatabaseService db) throws ParserConfigurationException {
        this.db = db;
    }
}
