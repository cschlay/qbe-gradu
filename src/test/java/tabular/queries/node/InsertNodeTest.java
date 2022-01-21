package tabular.queries.node;

import base.QueryBaseTest;
import db.neo4j.Neo4j;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.*;


class InsertNodeTest extends QueryBaseTest {
    @Test
    void withoutProperties() throws Exception {
        var query = "" +
                "| Book   |\n" +
                "|--------|\n" +
                "| INSERT |\n";
        var graph = execute(query);
        assertNode(graph, (tx, node) -> tx.getNodeById(Neo4j.id(node)));
    }

    @Test
    void oneProperty() throws Exception {
        var query = "" +
                "| Book   | title*                 |\n" +
                "|--------+------------------------|\n" +
                "| INSERT | \"Wizard Programmers\" |\n";
        var graph = execute(query);
        assertNode(graph, (tx, node) -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            Object property = neo4jNode.getProperty("title");
            assertEquals("Wizard Programmers", property);
        });
        assertNode(graph, (tx, node) -> {
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
        var graph = execute(query);
        assertNode(graph, (tx, node) -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            assertEquals("Wizard Programmers", neo4jNode.getProperty("title"));
            assertEquals(2019, neo4jNode.getProperty("bestseller"));
        });
    }

    // TODO: Add a few error cases
}
