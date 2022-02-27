package tabular.queries;

import base.QueryBaseTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationSumTest extends QueryBaseTest {
    private String courseId;

    @BeforeEach
    void setup() throws Exception {
        var fx = new Object() { Node course; };
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            fx.course = course;

            Node book1 = tx.createNode(Label.label("Book"));
            book1.setProperty("price", 5.00);

            Node book2 = tx.createNode(Label.label("Book"));
            book2.setProperty("price", 60.00);

            Relationship uses1 = course.createRelationshipTo(book1, RelationshipType.withName("uses"));
            uses1.setProperty("votes", 10);

            Relationship uses2 = course.createRelationshipTo(book2, RelationshipType.withName("uses"));
            uses2.setProperty("votes", 20);

            tx.commit();
        });
        courseId = String.valueOf(fx.course.getId());
    }

    @Test
    void sumNodes() throws Exception {
        var query = "" +
                "| Book  | price* |\n" +
                "|-------+--------|\n" +
                "| QUERY | SUM    |\n";
        ResultGraph result = execute(query);
        assertEquals(65.0, result.get("Book").property("price"));
    }

    @Test
    void sumEdges() throws Exception {
        var query = "" +
                "| uses              | votes* |\n" +
                "|-------------------+--------|\n" +
                "| QUERY Course.Book | SUM    |\n";
        ResultGraph result = execute(query);
        assertEquals(30, result.get("uses").property("votes"));
    }

    @Test
    void sumEdgesInPath() throws Exception {
        var query = "" +
                "| Course | id* | uses              | Book  | price*     |\n" +
                "|--------+-----+-------------------+-------+------------|\n" +
                "| QUERY  |     | QUERY Course.Book | QUERY | SUM Course |\n";
        ResultGraph result = execute(query);
        assertEquals(65.0, result.get(courseId).property("price"));
    }
}
