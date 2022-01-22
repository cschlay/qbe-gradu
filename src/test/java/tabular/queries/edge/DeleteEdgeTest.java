package tabular.queries.edge;

import base.QueryBaseTest;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.*;

import static org.junit.jupiter.api.Assertions.*;

class DeleteEdgeTest extends QueryBaseTest {
    @Test
    void byId() throws Exception {
        var fx = new Object() { Relationship relation; };
        run(tx -> {
            var tail = tx.createNode(Label.label("Artist"));
            var head = tx.createNode(Label.label("Guitar"));
            fx.relation = tail.createRelationshipTo(head, RelationshipType.withName("owns"));
            tx.commit();
        });
        long id = fx.relation.getId();

        var query = "" +
                "| owns                 | id* |\n" +
                "|----------------------+-----|\n" +
                "| DELETE Artist.Guitar | %s  |\n";
        var graph = execute(query, id);
        assertTrue(graph.isEmpty());

        run(tx -> assertThrows(NotFoundException.class, () -> tx.getRelationshipById(id)));
    }

    @Test
    void byName() throws Exception {
        // Delete all unused 'is' relations as 'is_member' is used
        var fx = new Object() { Relationship isRelation; Relationship isMemberRelation; };
        run(tx -> {
            var artist = tx.createNode(Label.label("Artist"));
            var user = tx.createNode(Label.label("User"));
            fx.isRelation = user.createRelationshipTo(artist, RelationshipType.withName("is"));
            fx.isMemberRelation = user.createRelationshipTo(artist, RelationshipType.withName("is_member"));
            tx.commit();
        });
        long isId = fx.isRelation.getId();
        long isMemberId = fx.isMemberRelation.getId();

        var query = "" +
                "| is                 |\n" +
                "|--------------------|\n" +
                "| DELETE User.Artist |\n";
        var graph = execute(query);
        assertTrue(graph.isEmpty());

        run(tx -> {
            tx.getRelationshipById(isMemberId);
            assertThrows(NotFoundException.class, () -> tx.getRelationshipById(isId));
        });
    }

    @Test
    void byProperties() throws Exception {
        // Delete inactive 'is_member' relations
        var fx = new Object() { Relationship relationActive; Relationship relationInactive; };
        run(tx -> {
            var user1 = tx.createNode(Label.label("User"));
            var user2 = tx.createNode(Label.label("User"));
            var artist = tx.createNode(Label.label("Artist"));

            fx.relationActive = user1.createRelationshipTo(artist, RelationshipType.withName("is_member"));
            fx.relationActive.setProperty("active", true);

            fx.relationInactive = user2.createRelationshipTo(artist, RelationshipType.withName("is_member"));
            fx.relationInactive.setProperty("active", false);
            tx.commit();
        });
        long activeId = fx.relationActive.getId();
        long inactiveId = fx.relationInactive.getId();

        var query = "" +
                "| is_member          | active |\n" +
                "|--------------------+--------|\n" +
                "| DELETE User.Artist | false  |\n";
        var graph = execute(query);
        assertTrue(graph.isEmpty());

        run (tx -> {
            tx.getRelationshipById(activeId);
            assertThrows(NotFoundException.class, () -> tx.getRelationshipById(inactiveId));
        });
    }
}
