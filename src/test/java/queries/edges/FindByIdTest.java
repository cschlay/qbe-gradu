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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Edge - Find by Id")
class FindByIdTest extends QueryBaseStaticTest {
    private static long id;

    @BeforeAll
    static void setup() throws Exception {
        var fx = new Object() { long id; };
        run(tx -> {
            Node tail = tx.createNode(Label.label("Book"));
            Node head = tx.createNode(Label.label("Topic"));
            Relationship edge = tail.createRelationshipTo(head, RelationshipType.withName("contains"));
            tx.commit();
            fx.id = edge.getId();
        });
        id = fx.id;
    }

    @Test
    void asQueryGraph() throws Exception {
        var queryGraph = new QueryGraph();
        var queryEdge = new QbeEdge(id, "contains");
        queryEdge.tailNode = new QbeNode("Book");
        queryEdge.headNode = new QbeNode("Topic");
        queryGraph.put(queryEdge);
        assertResult(execute(queryGraph));
    }

    @Test
    void asQbeTable() throws Exception {
        var query = "" +
                "| contains         | id* |\n" +
                "|------------------+-----|\n" +
                "| QUERY Book.Topic | %s  |\n";
        assertResult(execute(query, id));
    }

    private void assertResult(ResultGraph graph) throws Exception {
        eachEdge(graph, (tx, edge) -> assertEquals(id, edge.longId()));
    }
}
