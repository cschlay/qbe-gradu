package tabular.queries.node;

import base.QueryBaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.*;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class DeleteNodeTest extends QueryBaseTest {
    @Test
    void byId() throws Exception {
        var fx = new Object() { Node node; };
        run(tx -> {
            fx.node = tx.createNode(Label.label("Topic"));
            tx.commit();
        });
        long id = fx.node.getId();

        var query = "" +
                "| Topic  | id* |\n" +
                "|--------+-----|\n" +
                "| DELETE | %s  |\n";
        var graph = execute(query, id);
        assertTrue(graph.isEmpty());
        run(tx -> assertThrows(NotFoundException.class, () -> tx.getNodeById(id)));
    }

    @Test
    void byName() throws Exception {
        run(tx -> {
            tx.createNode(Label.label("Topic"));
            tx.createNode(Label.label("Book"));
            tx.createNode(Label.label("Course"));
            tx.commit();
        });

        var query = "" +
                "| Topic  |\n" +
                "|--------|\n" +
                "| DELETE |\n";
        var graph = execute(query);
        assertTrue(graph.isEmpty());
        run(tx -> {
            assertEquals(0, tx.findNodes(Label.label("Topic")).stream().count());
            assertEquals(1, tx.findNodes(Label.label("Book")).stream().count());
            assertEquals(1, tx.findNodes(Label.label("Course")).stream().count());
        });
    }

    @Test
    void byProperties() throws Exception {
        var fx = new Object() { Node n1; Node n2; };
        run(tx -> {
            fx.n1 = tx.createNode(Label.label("Book"));
            fx.n1.setProperty("price", 10.00);
            fx.n2 = tx.createNode(Label.label("Book"));
            fx.n2.setProperty("price", 12.00);
            tx.commit();
        });
        long id1 = fx.n1.getId();
        long id2 = fx.n2.getId();

        var query = "" +
                "| Book   | price* |\n" +
                "|--------+--------|\n" +
                "| DELETE | < 11.0 |\n";
        var graph = execute(query);
        assertTrue(graph.isEmpty());

        run(tx -> {
            assertThrows(NotFoundException.class, () -> tx.getNodeById(id1));
            // Ensure that, the other one is not deleted
            tx.getNodeById(id2);
        });
    }

    @Test
    void cascadeEdges() throws Exception {
        // When deleting nodes, the related edges should also be deleted
        var fx = new Object() { Relationship relation; };
        run(tx -> {
            Node tail = tx.createNode(Label.label("Course"));
            Node head = tx.createNode(Label.label("Book"));
            fx.relation = tail.createRelationshipTo(head, RelationshipType.withName("uses"));
        });
        long id = fx.relation.getId();

        var query = "" +
                "| Book   |\n" +
                "|--------|\n" +
                "| DELETE |\n";
        var graph = execute(query);
        assertTrue(graph.isEmpty());

        run(tx -> assertThrows(NotFoundException.class, () -> tx.getRelationshipById(id)));
    }
}
