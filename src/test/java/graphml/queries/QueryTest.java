package graphml.queries;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import core.graphs.ResultGraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import syntax.graphml.GraphMLParser;
import syntax.tabular.TabularResultWriter;

/**
 * The base test class to set up the test database.
 */
@SuppressWarnings("StaticVariableUsedBeforeInitialization")
public class QueryTest {
    public static QuerySession session;
    private static DatabaseManagementService dbManagement;

    @BeforeAll
    public static void beforeAll() throws Exception {
        dbManagement = Main.setupDatabase("data/test");
        session = new QuerySession(Main.getDefaultDatabase(dbManagement), new GraphMLParser(), new TabularResultWriter());
        session.executeCommand(CLICommands.RESET_DATABASE);
        session.executeCommand(CLICommands.SEED_DATABASE);
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }

    // Utility functions for easier testing
    public ResultGraph executeQuery(String ...parts) throws Exception {
        return session.processQuery(String.join("\n", parts));
    }

    public void print(ResultGraph graph) {
        //System.out.println(graph.toGraphML());
    }

    public GraphDatabaseService getDatabase() {
        return Main.getDefaultDatabase(dbManagement);
    }
}
