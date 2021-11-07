package queries;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import core.graphs.ResultGraph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.dbms.api.DatabaseManagementService;

/**
 * The base test class to set up the test database.
 */
@SuppressWarnings("StaticVariableUsedBeforeInitialization")
public class QueryTest {
    public static QuerySession session;
    private static DatabaseManagementService dbManagement;

    @BeforeClass
    public static void beforeAll() throws Exception {
        dbManagement = Main.setupDatabase("data/test");
        session = new QuerySession(Main.getDefaultDatabase(dbManagement));
        session.executeCommand(CLICommands.RESET_DATABASE);
        session.executeCommand(CLICommands.SEED_DATABASE);
    }

    @AfterClass
    public static void afterAll() {
        dbManagement.shutdown();
    }

    // Utility functions for easier testing
    public ResultGraph executeQuery(String ...parts) throws Exception {
        return session.processQuery(String.join("\n", parts));
    }

    public void print(ResultGraph graph) {
        System.out.println(graph.toGraphML());
    }
}
