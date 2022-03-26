package queries.edges;

import base.QueryBaseStaticTest;
import base.TestUtils;
import enums.QueryType;
import graphs.QbeEdge;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Edge - Aggregate Count")
class AggregateCountTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node book = tx.createNode(Label.label("Book"));
            Node topicA = tx.createNode(Label.label("Topic"));
            Node topicB = tx.createNode(Label.label("Topic"));
            book.createRelationshipTo(topicA, RelationshipType.withName("contains")).setProperty("theory", true);
            book.createRelationshipTo(topicB, RelationshipType.withName("contains")).setProperty("theory", false);
            book.createRelationshipTo(topicB, RelationshipType.withName("includes"));
            tx.commit();
        });
    }

    @Test
    @DisplayName("[Graph] Count by Name")
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        QbeEdge queryEdge = TestUtils.createTestEdge("contains", "Book", "Topic");
        queryEdge.type = QueryType.COUNT;
        queryGraph.put(queryEdge);
        assertEquals(2, execute(queryGraph).get("contains").getProperty("_agg-count"));
    }

    @Test
    @DisplayName("[Table] Count by Name")
    void asQbeTable() throws Exception {
        var query = "" +
                "| contains         |\n" +
                "|------------------|\n" +
                "| COUNT Book.Topic |\n";
        assertEquals(2, execute(query).get("contains").getProperty("_agg-count"));
    }

    @Test
    @DisplayName("[Table] Filter and Count by Property")
    void filterAndCountByProperty() throws Exception {
        var query = "" +
                "| contains         | theory |\n" +
                "|------------------+--------|\n" +
                "| COUNT Book.Topic | true   |\n";
        assertEquals(1, execute(query).get("contains").getProperty("_agg-count"));
    }

    // Extra: Group and Count by Property
    // Extra: Count Anonymous Edges
}
