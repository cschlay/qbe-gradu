package tabular.queries.node;

import base.QueryBaseTest;
import core.graphs.QbeNode;
import db.neo4j.Neo4j;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.*;


class InsertNodeTest extends QueryBaseTest {
    @Test
    void noProperty() throws Exception {
        var query = "" +
                "| Book   |\n" +
                "|--------|\n" +
                "| INSERT |\n";
        assertQuery(query, node -> tx.getNodeById(Neo4j.id(node)));
    }

    @Test
    void oneProperty() throws Exception {
        var query = "" +
                "| Book   | title*                 |\n" +
                "|--------+------------------------|\n" +
                "| INSERT | \"Wizard Programmers\" |\n";
        assertQuery(query, node -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            Object property = neo4jNode.getProperty("title");
            assertEquals("Wizard Programmers", property);
        });
    }

    @Test
    void multipleProperties() throws Exception {
        var query = "" +
                "| Book   | title*                 | bestseller* |\n" +
                "|--------+------------------------+-------------|\n" +
                "| INSERT | \"Wizard Programmers\" | 2019        |\n";
        assertQuery(query, node -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            assertEquals("Wizard Programmers", neo4jNode.getProperty("title"));
            assertEquals(2019, neo4jNode.getProperty("bestseller"));
        });
    }

    // TODO: Add a few error cases

    @FunctionalInterface
    private interface Assertion<QbeNode> {
        void accept(QbeNode node) throws Exception;
    }

    private void assertQuery(String query, Assertion<QbeNode> assertion) throws Exception {
        var graph = execute(query);
        assertFalse(graph.isEmpty(), "Graph is empty");

        for (var node : graph.values()) {
            assertion.accept(node);
        }
    }
}
