package base;

import cli.Main;
import cli.QueryExecutor;
import db.neo4j.Neo4j;
import graphs.Graph;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.ResultGraph;
import db.neo4j.Neo4jTraversal;
import org.junit.jupiter.api.AfterAll;
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
    private static DatabaseManagementService dbManagement;
    private static QueryExecutor queryExecutor;

    @BeforeAll
    public static void beforeAll() {
        dbManagement = Main.setupDatabase("data/test");
        db = Main.getDefaultDatabase(dbManagement);
        queryExecutor = new QueryExecutor(new Neo4jTraversal(db), new TabularParser(), new TabularResultWriter());
    }

    @AfterAll
    public static void afterAll() {
        dbManagement.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        Neo4j.resetDatabase(db);
    }

    protected void run(FnTransaction action) throws Exception {
        try (Transaction tx = db.beginTx()) {
            action.accept(tx);
        }
    }

    protected ResultGraph execute(String query, Object ...arguments) throws Exception {
        String formattedQuery = String.format(query, arguments);
        return queryExecutor.executeVerbose(formattedQuery);
    }

    protected void eachEdge(Graph graph, FnAssert<QbeEdge> assertion) throws Exception {
        assertFalse(graph.isEmpty(), "Graph is empty!");
        for (QbeNode node : graph.values()) {
            assertFalse(node.edges.isEmpty(), "Node doesn't have edges.");
            for (QbeEdge edge : node.edges.values()) {
                try (var tx = db.beginTx()) {
                    assertion.accept(tx, edge);
                }
            }
        }
    }

    protected void eachNode(Graph graph, FnAssert<QbeNode> assertion) throws Exception {
        assertFalse(graph.isEmpty(), "Graph is empty!");
        for (var node : graph.values()) {
            try (var tx = db.beginTx()) {
                assertion.accept(tx, node);
            }
        }
    }
}
