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

@DisplayName("[Query] Paths - Multiple Path")
class MultiplePathTest extends QueryBaseStaticTest {
    private static String idA;
    private static String idB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node author = tx.createNode(Label.label("Author"));
            Node book = tx.createNode(Label.label("Book"));
            Node course = tx.createNode(Label.label("Course"));
            Node paper = tx.createNode(Label.label("Paper"));
            Node topicA = tx.createNode(Label.label("Topic"));
            Node topicB = tx.createNode(Label.label("Topic"));

            course.createRelationshipTo(book, RelationshipType.withName("uses"));
            book.createRelationshipTo(topicA, RelationshipType.withName("contains"));
            author.createRelationshipTo(paper, RelationshipType.withName("writes"));
            paper.createRelationshipTo(topicA, RelationshipType.withName("includes"));
            paper.createRelationshipTo(topicB, RelationshipType.withName("includes"));
            tx.commit();

            idA = String.valueOf(topicA.getId());
            idB = String.valueOf(topicB.getId());
        });
    }

    @Test
    @DisplayName("[Table] Two Edges")
    void validateTwoEdge() throws Exception {
        var query = "" +
                "| includes          | contains         | Topic | id* |\n" +
                "|-------------------+------------------+-------+-----|\n" +
                "| QUERY Paper.Topic | QUERY Book.Topic | QUERY |     |\n";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(idA));
        assertFalse(resultGraph.containsKey(idB));
    }

    @Test
    @DisplayName("[Table] Two Paths")
    void validatesAllPaths() throws Exception {
        var query = "" +
                "| uses              | writes             | includes          | contains         | Topic | id* |\n" +
                "|-------------------+--------------------+-------------------+------------------+-------+-----|\n" +
                "| QUERY Course.Book | QUERY Author.Paper | QUERY Paper.Topic | QUERY Book.Topic | QUERY |     |\n";
        ResultGraph resultGraph = execute(query);
        assertTrue(resultGraph.containsKey(idA));
        assertFalse(resultGraph.containsKey(idB));
    }
}
