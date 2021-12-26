package base;

import cli.Main;
import db.neo4j.Neo4jOperations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.util.function.Consumer;

public class QueryBaseTest {
    protected static GraphDatabaseService db;
    protected static Neo4jOperations dbOperations;
    private static DatabaseManagementService dbManagement;

    @BeforeAll
    public static void beforeAll() {
        dbManagement = Main.setupDatabase("data/test");
        db= Main.getDefaultDatabase(dbManagement);
        dbOperations = new Neo4jOperations(db);
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        dbOperations.reset();
    }

    protected void inTransaction(Consumer<Transaction> action) {
        try (Transaction tx = db.beginTx()) {
            action.accept(tx);
            tx.rollback();
        }
    }
}
