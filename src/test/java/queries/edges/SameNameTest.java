package queries.edges;

import base.QueryBaseStaticTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[Query] Edge - Same Name")
class SameNameTest extends QueryBaseStaticTest {
    private static String idA;
    private static String idB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            Node bookA = tx.createNode(Label.label("Book"));
            Node bookB = tx.createNode(Label.label("Book"));
            Node student = tx.createNode(Label.label("Student"));
            course.createRelationshipTo(bookA, RelationshipType.withName("uses"));
            student.createRelationshipTo(bookA, RelationshipType.withName("uses"));
            student.createRelationshipTo(bookB, RelationshipType.withName("uses"));
            tx.commit();

            idA = String.valueOf(bookA.getId());
            idB = String.valueOf(bookB.getId());
        });
    }

    @Test
    @DisplayName("[Table] Distinguish by node names")
    void bothEdges() throws Exception {
        var query = "" +
                "| uses              | uses               | Book  | id* |\n" +
                "|-------------------+--------------------+-------+-----|\n" +
                "| QUERY Course.Book | QUERY Student.Book | QUERY |     |\n";

        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(idA));
        assertFalse(resultGraph.containsKey(idB));
    }
}
