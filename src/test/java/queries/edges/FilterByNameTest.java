package queries.edges;

import base.QueryBaseStaticTest;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.QueryGraph;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Query] Edge - Find by Name")
class FilterByNameTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node course = tx.createNode(Label.label("Course"));
            Node book = tx.createNode(Label.label("Book"));
            Node topic = tx.createNode(Label.label("Topic"));
            course.createRelationshipTo(book, RelationshipType.withName("uses"));
            book.createRelationshipTo(topic, RelationshipType.withName("contains"));
            tx.commit();
        });
    }

    @Test
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        var queryEdge = new QbeEdge("uses");
        queryEdge.tailNode = new QbeNode("Course");
        queryEdge.headNode = new QbeNode("Book");
        queryGraph.put(queryEdge);
        assertResult(execute(queryGraph));
    }

    @Test
    void asQbeTable() throws Exception {
        var query = "" +
                "| uses              | id* |\n" +
                "|-------------------+-----|\n" +
                "| QUERY Course.Book |     |\n";
        assertResult(execute(query));
    }

    private void assertResult(ResultGraph graph) throws Exception {
        eachEdge(graph, (tx, edge) -> assertEquals("uses", edge.name));
    }
}
