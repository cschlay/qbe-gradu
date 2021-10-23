package cli;

import core.db.neo4j.Neo4jTraversal;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import core.parsers.GraphMLParser;
import demo.DemoSeeder;
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
    private final Neo4jTraversal neo4jTraversal;

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

    private void executeCommand(String input) {
        // Do not use switch, it could contain multiple arguments.
        if (input.equals(CLICommands.PRINT_DATABASE)) {
            printDatabaseDetails();
        } else if (input.equals(CLICommands.RESET_DATABASE)) {
            resetDatabase();
            printDatabaseDetails();
        } else if (input.equals(CLICommands.SEED_DATABASE)) {
            System.out.println("Seeding the database...");
            DemoSeeder.seedEducationData(this.db);
            printDatabaseDetails();
        } else {
            // Defaults to executing query
            try {
                processQuery(input);
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
                node.getLabels().forEach(label -> System.out.println(label.name()));
            });
            System.out.println("}");

            System.out.println("Edges {");
            tx.getAllRelationships().forEach(relationship -> {
                System.out.printf(
                        "\t %s: %s --> %s (%s)%n",
                        relationship.getId(),
                        relationship.getStartNodeId(),
                        relationship.getEndNodeId(),
                        relationship.getType()
                );
            });
            System.out.println("}");
        }
    }

    private void processQuery(String query) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        // Can extend to support different query languages as long as they construct same QueryGraph.
        QueryGraph queryGraph = parser.parse(query);
        ResultGraph resultGraph = neo4jTraversal.traverse(queryGraph);
        System.out.println();
        System.out.println(resultGraph.toGraphML());
    }

    public QuerySession(GraphDatabaseService db) throws ParserConfigurationException {
        this.db = db;
        this.neo4jTraversal = new Neo4jTraversal(db);
    }
}
