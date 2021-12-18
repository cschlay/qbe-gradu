package tabular;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.dbms.api.DatabaseManagementService;
import syntax.tabular.TabularParser;
import syntax.tabular.TabularResultWriter;

@SuppressWarnings("StaticVariableUsedBeforeInitialization")
public class TabularTestClass {
    public static QuerySession session;
    private static DatabaseManagementService dbManagement;

    @BeforeAll
    public static void beforeAll() {
        dbManagement = Main.setupDatabase("data/test");
        session = new QuerySession(Main.getDefaultDatabase(dbManagement), new TabularParser(), new TabularResultWriter());
        session.executeCommand(CLICommands.RESET_DATABASE);
        session.executeCommand(CLICommands.SEED_DATABASE);
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }
}
