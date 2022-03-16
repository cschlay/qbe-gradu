package queries.paths;

import base.QueryBaseStaticTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Path - Aggregate Count")
class AggregateCountTest extends QueryBaseStaticTest {
    private static String idA;
    private static String idB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node bookA = tx.createNode(Label.label("Book"));
            Node bookB = tx.createNode(Label.label("Book"));
            Node topicA = tx.createNode(Label.label("Topic"));
            Node topicB = tx.createNode(Label.label("Topic"));

            RelationshipType edgeName = RelationshipType.withName("contains");
            bookA.createRelationshipTo(topicA, edgeName).setProperty("theory", false);
            bookA.createRelationshipTo(topicB, edgeName).setProperty("theory", true);
            bookB.createRelationshipTo(topicA, edgeName).setProperty("theory", false);
            tx.commit();

            idA = String.valueOf(bookA.getId());
            idB = String.valueOf(bookB.getId());
        });
    }

    @Test
    @DisplayName("[Table] Count Edges by Node Group")
    void countEdgesByNodeGroup() throws Exception {
        var query = "" +
                "| Book  | id* | contains              |\n" +
                "|-------+-----+-----------------------|\n" +
                "| QUERY |     | COUNT Book.Topic Book |\n";

        ResultGraph resultGraph = execute(query);
        assertEquals(2, resultGraph.get(idA).property("_agg-count"));
        assertEquals(1, resultGraph.get(idB).property("_agg-count"));
    }

    @Test
    @DisplayName("[Table] Count Edges Filtered by Properties")
    void countFilteredEdges() throws Exception {
        var query = "" +
                "| Book  | id* | contains              | theory* |\n" +
                "|-------+-----+-----------------------+---------|\n" +
                "| QUERY |     | COUNT Book.Topic Book | true    |";
        assertEquals(1, execute(query).get(idA).property("_agg-count"));
    }

    @Test
    @DisplayName("[Table] Count Nodes Filtered by Edges")
    void countNodesFilteredByEdges() throws Exception {
        var query = "" +
                "| Book  | contains         | theory* |\n" +
                "|-------+------------------+---------|\n" +
                "| COUNT | QUERY Book.Topic | false    |";
        assertEquals(2, execute(query).get("Book").property("_agg-count") );
    }
}
