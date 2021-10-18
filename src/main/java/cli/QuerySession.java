package cli;

import core.graphs.QueryGraph;
import core.parsers.GraphMLParser;
import demo.DemoSeeder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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

        var scanner = new Scanner(System.in);
        boolean run = true;

        do {
            System.out.print("\nqbe> ");
            String input = scanner.nextLine();
            if (input.equals("q")) {
                run = false;
            } else if (input.equals("reset")) {
                resetDatabase();
                printDatabaseDetails();
            } else if (input.equals("print")) {
                printDatabaseDetails();
            } else if (input.contains("seed")) {
                System.out.println("Seeding the database...");
                DemoSeeder.seedEducationData(this.db);
                printDatabaseDetails();
            } else {
                try {
                    processQuery(input);
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        } while (run);
    }

    private void resetDatabase() {
        System.out.println("Resetting database...");
        try (var tx = db.beginTx()) {
            tx.getAllNodes().forEach(Node::delete);
            tx.getAllRelationships().forEach(Relationship::delete);
            tx.commit();
        }
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

    private void processQuery(String query) throws IOException, SAXException {
        // Can extend to support different query languages as long as they construct same QueryGraph.
        QueryGraph graph = parser.parse(query);
    }

    public QuerySession(GraphDatabaseService db) throws ParserConfigurationException {
        this.db = db;
    }
}
