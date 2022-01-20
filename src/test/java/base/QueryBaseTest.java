package base;

import cli.Main;
import cli.QuerySession;
import core.graphs.ResultGraph;
import db.neo4j.Neo4jActions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import syntax.tabular.TabularParser;
import syntax.tabular.TabularResultWriter;

import java.util.function.Consumer;

public abstract class QueryBaseTest {
    protected static GraphDatabaseService db;
    protected static Neo4jActions dbOperations;
    private static DatabaseManagementService dbManagement;

    protected static Transaction tx;

    @BeforeAll
    public static void beforeAll() {
        dbManagement = Main.setupDatabase("data/test");
        db = Main.getDefaultDatabase(dbManagement);
        dbOperations = new Neo4jActions(db);
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        dbOperations.reset();
        tx = db.beginTx();
    }

    @AfterEach
    public void afterEach() {
        tx.close();
    }

    protected void inTransaction(Consumer<Transaction> action) {
        try (Transaction tx = db.beginTx()) {
            action.accept(tx);
        }
    }

    protected QuerySession getSession() {
        return new QuerySession(db, new TabularParser(), new TabularResultWriter());
    }

    protected ResultGraph execute(String query) throws Exception {
        var session = getSession();
        var queryGraph = session.parseQuery(query);
        System.out.printf("QueryGraph: %s%n", queryGraph);

        var resultGraph = session.executeQuery(queryGraph);
        System.out.printf("ResultGraph: %s%n", resultGraph);

        System.out.printf("== Query ==%n%s%n", query);
        System.out.printf("== Result==%n%s%n", session.toString(queryGraph, resultGraph));
        return resultGraph;
    }}
