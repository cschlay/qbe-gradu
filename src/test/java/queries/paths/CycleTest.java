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
    private static String bookId;
    private static String courseId;
    private static String lecturerIdA;
    private static String lecturerIdB;


    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            Node book = tx.createNode(Label.label("Book"));
            Node lecturerA = tx.createNode(Label.label("Lecturer"));
            Node lecturerB = tx.createNode(Label.label("Lecturer"));

            course.createRelationshipTo(book, RelationshipType.withName("uses"));
            book.createRelationshipTo(course, RelationshipType.withName("written_for"));
            book.createRelationshipTo(lecturerA, RelationshipType.withName("cites"));
            book.createRelationshipTo(lecturerB, RelationshipType.withName("cites"));
            lecturerA.createRelationshipTo(course, RelationshipType.withName("teaches"));
            tx.commit();

            bookId = String.valueOf(book.getId());
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
        assertTrue(resultGraph.containsKey(bookId));
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
                "| QUERY Lecturer.Course | QUERY Course.Book | QUERY Book.Lecturer | QUERY    |     |";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(bookId));
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
        assertTrue(resultGraph.containsKey(bookId));
        assertTrue(resultGraph.containsKey(courseId));
        assertFalse(resultGraph.containsKey(lecturerIdA));
        assertTrue(resultGraph.containsKey(lecturerIdB));

        // TODO: The problem is likely with assuming the end node to be true and failure to continue with other paths
    }
}
