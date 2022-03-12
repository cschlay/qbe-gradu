package base;

import cli.Main;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Implements a global database setup based on the answers:
 * https://stackoverflow.com/questions/43282798/in-junit-5-how-to-run-code-before-all-tests
 */
public class DatabaseTestExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource  {
    public static GraphDatabaseService db;
    private static @Nullable DatabaseManagementService dbManagement;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (dbManagement == null) {
            dbManagement = Main.setupDatabase("data/test");
            db = Main.getDefaultDatabase(dbManagement);
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("database", this);
            System.out.println("Test database initialized.");
        }
    }

    @Override
    public void close() {
        if (dbManagement != null) {
            dbManagement.shutdown();
            dbManagement = null;
            System.out.println("Test database closed.");
        }
    }
}
