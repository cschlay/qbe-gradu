package tabular.queries.node;

import base.QueryBaseResetEachTest;
import db.neo4j.Neo4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class InsertNodeTest extends QueryBaseResetEachTest {
    @Test
    void withoutProperties() throws Exception {
        var query = "" +
                "| Book   |\n" +
                "|--------|\n" +
                "| INSERT |\n";
        var graph = execute(query);
        eachNode(graph, (tx, node) -> tx.getNodeById(Neo4j.id(node)));
    }

    @Test
    void oneProperty() throws Exception {
        var query = "" +
                "| Book   | title*                 |\n" +
                "|--------+------------------------|\n" +
                "| INSERT | \"Wizard Programmers\" |\n";
        var graph = execute(query);
        eachNode(graph, (tx, node) -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            Object property = neo4jNode.getProperty("title");
            assertEquals("Wizard Programmers", property);
        });
        eachNode(graph, (tx, node) -> {
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
        eachNode(graph, (tx, node) -> {
            Node neo4jNode = tx.getNodeById(Neo4j.id(node));
            assertEquals("Wizard Programmers", neo4jNode.getProperty("title"));
            assertEquals(2019, neo4jNode.getProperty("bestseller"));
        });
    }
}
