package queries.paths;

import base.QueryBaseStaticTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Query] Paths - Simple")
class SimplePathTest extends QueryBaseStaticTest {
    private static String idA;
    private static String idB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            Node bookA = tx.createNode(Label.label("Book"));
            Node bookB = tx.createNode(Label.label("Book"));
            Node topicA = tx.createNode(Label.label("Topic"));
            topicA.setProperty("name", "Graphs");
            Node topicB = tx.createNode(Label.label("Topic"));
            course.createRelationshipTo(bookA, RelationshipType.withName("uses"));
            bookA.createRelationshipTo(topicA, RelationshipType.withName("contains"));
            bookB.createRelationshipTo(topicB, RelationshipType.withName("contains"));
            tx.commit();

            idA = String.valueOf(topicA.getId());
            idB = String.valueOf(topicB.getId());
        });
    }

    @Test
    @DisplayName("[Table] Check Existence")
    void checkExistence() throws Exception {
        var query = "" +
                "| uses              | contains         | Topic | name* |\n" +
                "|-------------------+------------------+-------+-------|\n" +
                "| QUERY Course.Book | QUERY Book.Topic | QUERY |       |\n";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(idA));
        assertFalse(resultGraph.containsKey(idB));
    }
}
