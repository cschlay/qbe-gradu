package base;

import cli.Main;
import cli.QuerySession;
import core.graphs.Graph;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
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

import static org.junit.jupiter.api.Assertions.assertFalse;

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

    protected void run(FnTransaction action) throws Exception {
        try (Transaction tx = db.beginTx()) {
            action.accept(tx);
        }
    }

    protected QuerySession getSession() {
        return new QuerySession(db, new TabularParser(), new TabularResultWriter());
    }

    protected ResultGraph execute(String query, Object ...arguments) throws Exception {
        return execute(String.format(query, arguments));
    }

    protected ResultGraph execute(String query) throws Exception {
        var session = getSession();
        var queryGraph = session.parseQuery(query);
        System.out.printf("QueryGraph:%n%s%n", queryGraph);

        var resultGraph = session.executeQuery(queryGraph);
        System.out.printf("ResultGraph:%n%s%n", resultGraph);

        System.out.printf("== Query ==%n%s%n", query);
        System.out.printf("== Result==%n%s%n", session.toString(queryGraph, resultGraph));
        return resultGraph;
    }

    protected void assertEdge(Graph graph, FnAssert<QbeEdge> assertion) throws Exception {
        assertFalse(graph.isEmpty(), "Graph is empty!");

        int edgeCount = 0;
        for (QbeNode node : graph.values()) {
            assertFalse(node.edges.isEmpty(), "Node doesn't have edges.");
            for (QbeEdge edge : node.edges.values()) {
                try (var tx = db.beginTx()) {
                    assertion.accept(tx, edge);
                    edgeCount += node.edges.size();
                }
            }
        }

        System.out.printf("Asserted %s nodes and %s edges%n", graph.size(), edgeCount);
    }

    protected void assertNode(Graph graph, FnAssert<QbeNode> assertion) throws Exception {
        assertFalse(graph.isEmpty(), "Graph is empty!");

        for (var node : graph.values()) {
            try (var tx = db.beginTx()) {
                assertion.accept(tx, node);
            }
        }

        System.out.printf("Asserted %s nodes%n", graph.size());
    }
}
