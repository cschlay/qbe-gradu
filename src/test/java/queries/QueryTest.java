package queries;

import cli.CLICommands;
import cli.Main;
import cli.QuerySession;
import core.graphs.ResultGraph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

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

    // Utility functions for easier testing
    public ResultGraph executeQuery(String ...parts) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        return session.processQuery(String.join("\n", parts));
    }

    public void print(ResultGraph graph) {
        System.out.println(graph.toGraphML());
    }
}
