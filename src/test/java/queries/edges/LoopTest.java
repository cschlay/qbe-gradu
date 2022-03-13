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

@DisplayName("Edge - Loops")
class LoopTest extends QueryBaseStaticTest {
    private static String loopId;
    private static String edgeId;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node bookA = tx.createNode(Label.label("Book"));
            bookA.setProperty("new", false);
            Relationship loop = bookA.createRelationshipTo(bookA, RelationshipType.withName("reviews"));
            Node bookB = tx.createNode(Label.label("Book"));
            bookB.setProperty("new", true);
            Relationship edge = bookA.createRelationshipTo(bookB, RelationshipType.withName("reviews"));
            tx.commit();

            loopId = String.valueOf(loop.getId());
            edgeId = String.valueOf(edge.getId());
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
                    "| reviews         | id* | Book  | new* |\n" +
                    "|-----------------+-----+-------+------|\n" +
                    "| QUERY Book.Book |     | QUERY |      |\n";
            assertResult(execute(query));
        }

        private void assertResult(ResultGraph graph) { assertEquals(2, graph.order()); }
    }

    @Nested
    @DisplayName("Indexed Loop") // Loops without edges
    class IndexedLoopTest {
        @Test
        void asQueryGraph() throws Exception {
            var queryGraph = new QueryGraph();
            var queryNode = new QbeNode("Book:1");
            var queryEdge = new QbeEdge("reviews");
            queryEdge.tailNode = queryNode;
            queryEdge.headNode = queryNode;
            queryGraph.put(queryEdge);
            assertResult(execute(queryGraph));
        }

        @Test
        void asQbeTable() throws Exception {
            var query = "" +
                    "| reviews             | id* |\n" +
                    "|---------------------+-----|\n" +
                    "| QUERY Book:1.Book:1 |     |\n";
            assertResult(execute(query));
        }

        private void assertResult(ResultGraph graph) {
            assertEquals(1, graph.order());
            assertNotNull(graph.get(loopId));
        }
    }

    @Nested
    @DisplayName("Indexed Edge") // Edges without loops
    class IndexedEdgeTest {
        @Test
        void asQueryGraph() throws Exception {
            var queryGraph = new QueryGraph();
            var queryEdge = new QbeEdge("reviews");
            queryEdge.tailNode = new QbeNode("Book:1");;
            queryEdge.headNode = new QbeNode("Book:2");;
            queryGraph.put(queryEdge);
            assertResult(execute(queryGraph));
        }

        @Test
        void asQbeTable() throws Exception {
            var query = "" +
                    "| reviews             | id* |\n" +
                    "|---------------------+-----|\n" +
                    "| QUERY Book:1.Book:2 |     |\n";
            assertResult(execute(query));
        }

        private void assertResult(ResultGraph graph) {
            assertEquals(1, graph.order());
            assertNotNull(graph.get(edgeId));
        }
    }
}
