package queries.edges;

import base.QueryBaseStaticTest;
import base.TestUtils;
import enums.QueryType;
import graphs.QbeData;
import graphs.QbeEdge;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Edge - Aggregate Sum")
class AggregateSumTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx ->{
            Node course = tx.createNode(Label.label("Course"));
            Node bookA = tx.createNode(Label.label("Book"));
            Node bookB = tx.createNode(Label.label("Book"));

            RelationshipType uses = RelationshipType.withName("uses");
            Relationship edgeA = course.createRelationshipTo(bookA, uses);
            edgeA.setProperty("likes", 10);
            edgeA.setProperty("expired", false);
            Relationship edgeB = course.createRelationshipTo(bookB, uses);
            edgeB.setProperty("likes", 20);
            edgeB.setProperty("expired", true);
            tx.commit();
        });
    }

    @Test
    @DisplayName("[Graph] Sum by Name")
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        QbeEdge queryEdge = TestUtils.createTestEdge("uses", "Course", "Book");
        queryEdge.type = QueryType.SUM;
        queryEdge.aggregationProperty = "likes";
        queryEdge.properties.put("likes", new QbeData(null));
        queryGraph.put(queryEdge);

        assertEquals(30, execute(queryGraph).get("uses").addProperty("likes"));
    }

    @Test
    @DisplayName("[Table] Sum by Name")
    void asQbeTable() throws Exception {
        var query = "" +
                "| uses              | likes* |\n" +
                "|-------------------+--------|\n" +
                "| QUERY Course.Book | SUM _  |\n";
        assertEquals(30, execute(query).get("uses").addProperty("likes"));
    }

    @Test
    @DisplayName("[Table] Filter and Sum by Property")
    void filterAndSumByProperty() throws Exception {
        var query = "" +
                "| uses              | likes* | expired* |\n" +
                "|-------------------+--------+----------|\n" +
                "| QUERY Course.Book | SUM _  | false    |\n";
        assertEquals(10, execute(query).get("uses").addProperty("likes"));
    }

    // Extra: Group and Sum by Property
    // Extra: Sum Anonymous Edges
}
