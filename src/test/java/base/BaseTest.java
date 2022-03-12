package base;

import cli.Main;
import cli.QueryExecutor;
import db.neo4j.Neo4jTraversal;
import graphs.Graph;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import syntax.tabular.TabularParser;
import syntax.tabular.TabularResultWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith({ DatabaseTestExtension.class })
public abstract class BaseTest {
    protected static GraphDatabaseService db;
    protected static QueryExecutor queryExecutor;

    @BeforeAll
    public static void beforeAll() {
        db = DatabaseTestExtension.db;
        queryExecutor = new QueryExecutor(new Neo4jTraversal(db), new TabularParser(), new TabularResultWriter());
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
