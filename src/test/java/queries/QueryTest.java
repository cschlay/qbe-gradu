package queries;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.dbms.api.DatabaseManagementService;

/**
 * The base test class to setup the database.
 *
 */
public class QueryTest {
    public static QuerySession session;
    private static final DatabaseManagementService dbManagement = Main.setupDatabase("data/test");

    @BeforeClass
    public static void beforeAll() throws Exception {
        session = new QuerySession(Main.getDefaultDatabase(dbManagement));
        session.executeCommand(CLICommands.RESET_DATABASE);
        session.executeCommand(CLICommands.SEED_DATABASE);
    }

    @AfterClass
    public static void afterAll() {
        dbManagement.shutdown();
    }
}
