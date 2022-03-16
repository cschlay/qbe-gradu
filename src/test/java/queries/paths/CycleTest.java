package queries.paths;

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


@DisplayName("Paths - Cycles")
class CycleTest extends QueryBaseStaticTest {
    private static String bookIdA;
    private static String bookIdB;
    private static String courseId;
    private static String lecturerIdA;
    private static String lecturerIdB;


    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            Node bookA = tx.createNode(Label.label("Book"));
            Node bookB = tx.createNode(Label.label("Book"));
            Node lecturerA = tx.createNode(Label.label("Lecturer"));
            Node lecturerB = tx.createNode(Label.label("Lecturer"));

            course.createRelationshipTo(bookA, RelationshipType.withName("uses"));
            course.createRelationshipTo(bookB, RelationshipType.withName("uses"));
            bookA.createRelationshipTo(course, RelationshipType.withName("written_for"));
            bookA.createRelationshipTo(lecturerA, RelationshipType.withName("cites"));
            bookA.createRelationshipTo(lecturerB, RelationshipType.withName("cites"));
            bookB.createRelationshipTo(lecturerA, RelationshipType.withName("cites"));
            lecturerA.createRelationshipTo(course, RelationshipType.withName("teaches"));
            tx.commit();

            bookIdA = String.valueOf(bookA.getId());
            bookIdB = String.valueOf(bookB.getId());
            courseId = String.valueOf(course.getId());
            lecturerIdA = String.valueOf(lecturerA.getId());
            lecturerIdB = String.valueOf(lecturerB.getId());
        });
    }

    @Test
    @DisplayName("Two Nodes")
    void twoNodes() throws Exception {
        var query = "" +
                "| written_for       | uses              | Book  | id* |\n" +
                "|-------------------+-------------------+-------+-----|\n" +
                "| QUERY Book.Course | QUERY Course.Book | QUERY |     |";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(bookIdA));
        assertFalse(resultGraph.containsKey(bookIdB));
        assertTrue(resultGraph.containsKey(courseId));
        assertFalse(resultGraph.containsKey(lecturerIdA));
        assertFalse(resultGraph.containsKey(lecturerIdB));
    }

    @Test
    @DisplayName("Three Nodes")
    void threeNodes() throws Exception {
        var query = "" +
                "| teaches               | uses              | cites               | Lecturer | id* |\n" +
                "|-----------------------+-------------------+---------------------+----------+-----|\n" +
                "| QUERY Lecturer.Course | QUERY Course.Book | QUERY Book.Lecturer | QUERY    |     |\n";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(bookIdA));
        assertTrue(resultGraph.containsKey(bookIdB));
        assertTrue(resultGraph.containsKey(courseId));
        assertTrue(resultGraph.containsKey(lecturerIdA));
        assertFalse(resultGraph.containsKey(lecturerIdB));
    }

    @Test
    @DisplayName("In Path")
    void cycleInPath() throws Exception {
        var query = "" +
                "| written_for       | uses              | cites               | Lecturer | id* |\n" +
                "|-------------------+-------------------+---------------------+----------+-----|\n" +
                "| QUERY Book.Course | QUERY Course.Book | QUERY Book.Lecturer | QUERY    |     |\n";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(bookIdA));
        assertFalse(resultGraph.containsKey(bookIdB));
        assertTrue(resultGraph.containsKey(courseId));
        assertTrue(resultGraph.containsKey(lecturerIdA));
        assertTrue(resultGraph.containsKey(lecturerIdB));
    }
}
