package queries.edges;

import base.QueryBaseStaticTest;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Edge - Filter by Direction")
class FilterByDirectionTest extends QueryBaseStaticTest {
    private static String edgeOut;
    private static String edgeIn;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node book = tx.createNode(Label.label("Book"));
            Node topic = tx.createNode(Label.label("Topic"));
            Relationship bookToTopic = book.createRelationshipTo(topic, RelationshipType.withName("contains"));
            Relationship topicToBook = topic.createRelationshipTo(book, RelationshipType.withName("contains"));
            tx.commit();

            edgeOut = String.valueOf(bookToTopic.getId());
            edgeIn = String.valueOf(topicToBook.getId());
        });
    }

    @Test
    @DisplayName("Direction from A to B")
    void fromBookToTopic() throws Exception {
        var queryGraph = new QueryGraph();
        var queryEdge = new QbeEdge("contains");
        queryEdge.tailNode = new QbeNode("Book");
        queryEdge.headNode = new QbeNode("Topic");
        queryGraph.put(queryEdge);
        eachEdge(execute(queryGraph), (tx, edge) -> assertEquals(edgeOut, edge.id));
    }

    @Test
    @DisplayName("Direction from B to A")
    void fromTopicToBook() throws Exception {
        var queryGraph = new QueryGraph();
        var queryEdge = new QbeEdge("contains");
        queryEdge.tailNode = new QbeNode("Topic");
        queryEdge.headNode = new QbeNode("Book");
        queryGraph.put(queryEdge);
        eachEdge(execute(queryGraph), (tx, edge) -> assertEquals(edgeIn, edge.id));
    }
}
