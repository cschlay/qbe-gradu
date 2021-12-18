package tabular;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import core.exceptions.SyntaxError;
import core.graphs.ResultGraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import syntax.tabular.TabularParser;
import syntax.tabular.TabularResultWriter;

@SuppressWarnings("StaticVariableUsedBeforeInitialization")
public class TabularTestClass {
    protected static QuerySession session;
    private static DatabaseManagementService dbManagement;

    @BeforeAll
    public static void beforeAll() {
        dbManagement = Main.setupDatabase("data/test");
        session = new QuerySession(getDatabase(), new TabularParser(), new TabularResultWriter());
        session.executeCommand(CLICommands.RESET_DATABASE);
        session.executeCommand(CLICommands.SEED_DATABASE);
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }

    protected ResultGraph executeQuery(String query) throws Exception {
        var queryGraph = session.parseQuery(query);
        var resultGraph = session.executeQuery(queryGraph);
        System.out.println("Query:");
        System.out.println(query);

        System.out.println("Result:");
        System.out.println(session.toString(queryGraph, resultGraph));
        return resultGraph;
    }

    protected static GraphDatabaseService getDatabase() {
        return Main.getDefaultDatabase(dbManagement);
    }
}
