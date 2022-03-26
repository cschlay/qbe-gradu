package queries.edges;

import base.QueryBaseStaticTest;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("[Query] Edge - Loops")
class LoopTest extends QueryBaseStaticTest {

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node bookA = tx.createNode(Label.label("Book"));
            bookA.setProperty("new", false);
            bookA.createRelationshipTo(bookA, RelationshipType.withName("reviews"));
            Node bookB = tx.createNode(Label.label("Book"));
            bookB.setProperty("new", true);
            bookA.createRelationshipTo(bookB, RelationshipType.withName("reviews"));
            tx.commit();
        });
    }

    @Nested
    @DisplayName("Loops and Edges") // Both loops and edges are included
    class LoopsAndEdgesTest {
        @Test
        void asQueryGraph() throws Exception {
            var queryGraph = new QueryGraph();
            var queryNode = new QbeNode("Book");
            var queryEdge = new QbeEdge("reviews");
            queryEdge.tailNode = queryNode;
            queryEdge.headNode = queryNode;
            queryGraph.put(queryEdge);
            assertResult(execute(queryGraph));
        }

        @Test
        void asQbeTable() throws Exception {
            var query = "" +
                    "| reviews         | reviews.id* | Book  | Book.id* | new* |\n" +
                    "|-----------------+-------------+-------+----------+------|\n" +
                    "| QUERY Book.Book |             | QUERY |          |      |\n";
            assertResult(execute(query));
        }

        private void assertResult(ResultGraph graph) { assertEquals(2, graph.order()); }
    }
}
