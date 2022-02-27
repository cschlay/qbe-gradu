package tabular.queries;

import base.QueryBaseTest;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationCountTest extends QueryBaseTest {
    private String bookId;

    @BeforeEach
    void setup() throws Exception {
        var fx = new Object() { Node book; };
        run(tx -> {
            fx.book = tx.createNode(Label.label("Book"));
            Node topic1 = tx.createNode(Label.label("Topic"));
            Node topic2 = tx.createNode(Label.label("Topic"));
            fx.book.createRelationshipTo(topic1, RelationshipType.withName("contains"));
            fx.book.createRelationshipTo(topic2, RelationshipType.withName("contains"));
            tx.commit();
        });
        bookId = String.valueOf(fx.book.getId());
    }


    @Test
    void countEdges() throws Exception {
        var query = "" +
                "| contains         |\n" +
                "|------------------|\n" +
                "| COUNT Book.Topic |\n";
        ResultGraph result = execute(query);
        QbeNode node = result.get("contains");
        assertEquals(2, node.property("_agg-count"));
    }

    @Test
    void countNodes() throws Exception {
        var query = "" +
                "| Topic |\n" +
                "|-------|\n" +
                "| COUNT |\n";
        ResultGraph result = execute(query);
        QbeNode node = result.get("Topic");
        assertEquals(2, node.property("_agg-count"));
    }

    @Test
    void countEdgesInPath() throws Exception {
        var query = "" +
                "| Book  | id* | contains              |\n" +
                "|-------+-----+-----------------------|\n" +
                "| QUERY |     | COUNT Book.Topic Book |\n";
        ResultGraph result = execute(query);

        QbeNode node = result.get(bookId);
        assertEquals(2, node.property("_agg-count"));
    }

    // Extra: Count anonymous edges

    // Extra: Count anonymous nodes
}
