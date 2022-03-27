package cli;

import db.neo4j.Neo4jTraversal;
import interfaces.QueryParser;
import interfaces.ResultWriter;
import demo.CourseGraphDemo;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/** Session for taking user input from CLI interface. */
public class QuerySession {
    private final CommandLine cli;
    private final GraphDatabaseService db;
    private final QueryExecutor queryExecutor;

    public QuerySession(GraphDatabaseService databaseService, QueryParser parser, ResultWriter writer) {
        db = databaseService;
        cli = new CommandLine();
        queryExecutor = new QueryExecutor(new Neo4jTraversal(databaseService), parser, writer);
    }

    /**
     * Starts the query session. Command is executed after two consecutive new line characters '\n'.
     */
    public void start() {
        cli.print("QBE for Graph Database Prototype\n");
        cli.printHelp();

        String input;
        do {
            cli.print("\nqbe> ");
            input = cli.read();
            executeCommand(input);
        } while (!CommandLine.QUIT.equals(input));

        cli.close();
    }

    public void executeCommand(@NotNull String input) {
        // Do not use switch, it could contain multiple arguments.
        switch (input) {
            case CommandLine.PRINT_DATABASE:
                printDatabaseDetails();
                break;
            case CommandLine.RESET_DATABASE:
                resetDatabase();
                printDatabaseDetails();
                break;
            case CommandLine.SEED_DATABASE:
                CourseGraphDemo.seed(this.db);
                printDatabaseDetails();
                break;
            case "":
                break;
            default:
                // Defaults to executing query
                try {
                    queryExecutor.execute(input);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
        }
    }

    private void resetDatabase() {
        cli.println("Resetting database...");
        try (Transaction tx = db.beginTx()) {
            tx.getAllNodes().forEach(Node::delete);
            tx.getAllRelationships().forEach(Relationship::delete);
            tx.commit();
        }
    }

    private void printDatabaseDetails() {
        try (Transaction tx = db.beginTx()){
            cli.println("Node {");
            tx.getAllNodes().forEach(node -> cli.print("\t%s(%s) %s%n",
                node.getLabels().iterator().next(),
                node.getId(),
                node.getAllProperties()
            ));
            cli.println("}");

            cli.println("Edges {");
            tx.getAllRelationships().forEach(relationship -> cli.print("\t%s(%s): %s --> %s %s%n",
                relationship.getType(),
                relationship.getId(),
                relationship.getStartNodeId(),
                relationship.getEndNodeId(),
                relationship.getAllProperties()
            ));
            cli.print("}");
        }
    }
}
