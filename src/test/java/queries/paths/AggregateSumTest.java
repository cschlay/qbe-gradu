package queries.paths;

import base.QueryBaseStaticTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Path - Aggregate Sum")
class AggregateSumTest extends QueryBaseStaticTest {
    private static String idA;
    private static String idB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node courseA = tx.createNode(Label.label("Course"));
            Node courseB = tx.createNode(Label.label("Course"));
            Node bookA = tx.createNode(Label.label("Book"));
            bookA.setProperty("price", 5.00);
            Node bookB = tx.createNode(Label.label("Book"));
            bookB.setProperty("price", 60.00);

            RelationshipType edgeName = RelationshipType.withName("uses");
            Relationship likesA = courseA.createRelationshipTo(bookA, edgeName);
            likesA.setProperty("likes", 10);
            likesA.setProperty("recommended", false);
            Relationship likesB = courseA.createRelationshipTo(bookB, edgeName);
            likesB.setProperty("likes", 20);
            likesB.setProperty("recommended", true);
            Relationship likesC = courseB.createRelationshipTo(bookA, edgeName);
            likesC.setProperty("likes", 2);
            likesC.setProperty("recommended", true);

            tx.commit();
            idA = String.valueOf(courseA.getId());
            idB = String.valueOf(courseB.getId());
        });
    }

    @Test
    @DisplayName("[Table] Sum Edges by Node Group")
    void sumEdgesByNodeGroup() throws Exception {
        var query = "" +
                "| Course | id* | uses              | likes*     |\n" +
                "|--------+-----+-------------------+------------|\n" +
                "| QUERY  |     | QUERY Course.Book | SUM Course |\n";
        ResultGraph resultGraph = execute(query);
        assertEquals(30, resultGraph.get(idA).addProperty("likes"));
        assertEquals(2, resultGraph.get(idB).addProperty("likes"));
    }

    @Test
    @DisplayName("[Table] Sum Nodes by Node Group")
    void sumNodesByNodeGroup() throws Exception {
        var query = "" +
                "| Course | id* | uses              | Book  | price*     |\n" +
                "|--------+-----+-------------------+-------+------------|\n" +
                "| QUERY  |     | QUERY Course.Book | QUERY | SUM Course |\n";

        ResultGraph resultGraph = execute(query);
        assertEquals(65.00, resultGraph.get(idA).addProperty("price"));
        assertEquals(5.00, resultGraph.get(idB).addProperty("price"));
    }

    @Test
    @DisplayName("[Table] Sum Edges Filtered by Properties")
    void sumFilteredEdges() throws Exception {
        var query = "" +
                "| Course | id* | uses              | likes*     | recommended* |\n" +
                "|--------+-----+-------------------+------------+--------------|\n" +
                "| QUERY  |     | QUERY Course.Book | SUM Course | true         |\n";
        ResultGraph resultGraph = execute(query);
        assertEquals(20, resultGraph.get(idA).addProperty("likes"));
        assertEquals(2, resultGraph.get(idB).addProperty("likes"));
    }

    @Test
    @DisplayName("[Table] Sum Nodes Filtered by Edges")
    void sumNodesFilteredByEdges() throws Exception {
        var query = "" +
                "| Course   | id* | Book  | price* | uses              | likes* |\n" +
                "|----------+-----+-------|-------------------+--------|\n" +
                "| QUERY    |     | QUERY | SUM _  | QUERY Course.Book | > 10   |\n";
        ResultGraph resultGraph = execute(query);
        assertEquals(60.0, resultGraph.get("Book").addProperty("price"));
    }
}
