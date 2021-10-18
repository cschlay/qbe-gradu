package cli;

import core.graphs.QueryGraph;
import core.parsers.GraphMLParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

/** Session for taking user input from CLI interface. */
public class QuerySession {
    private GraphDatabaseService db;
    private final GraphMLParser parser = new GraphMLParser();

    /**
     * Starts the query session.
     */
    public void start() {
        System.out.println("QBE for Graph Database Prototype");

        var scanner = new Scanner(System.in);
        boolean run = true;

        do {
            System.out.print("qbe>");
            String input = scanner.nextLine();
            if (input.equals("q")) {
                run = false;
            } else {
                try {
                    processQuery(input);
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        } while (run);
    }

    private void processQuery(String query) throws ParserConfigurationException, IOException, SAXException {
        // Can extend to support different query languages as long as they construct same QueryGraph.
        QueryGraph graph = parser.parse(query);
    }

    public QuerySession(GraphDatabaseService db) throws ParserConfigurationException {
        this.db = db;
    }
}
