package tabular.queries.edge;

import base.QueryBaseTest;
import core.graphs.Graph;
import db.neo4j.Neo4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.*;

class InsertEdgeTest extends QueryBaseTest {
    @Test
    void withoutProperties() throws Exception {
        // Add 'performs' relation between to 'Band' and 'Song'.
        Node band = dbx.newNode("Band");
        Node song = dbx.newNode("Song");
        tx.commit();

        var query = "" +
                "| performs         | Band  | Band.id | Song  | Song.id |\n" +
                "|------------------+-------+---------+-------+---------|\n" +
                "| INSERT Band.Song | QUERY | %s      | QUERY | %s      |\n";
        query = String.format(query, band.getId(), song.getId());

        Graph graph = execute(query);
        assertFalse(graph.isEmpty());

        for (var node : graph.values()) {
            assertFalse(node.edges.isEmpty());
            for (var edge : node.edges.values()) {
                var rel = tx.getRelationshipById(Neo4j.id(edge));
                assertTrue(rel.getStartNodeId() == band.getId() || rel.getEndNodeId() == song.getId());
            }
        }
    }

    @Test
    void oneProperty() {
        fail();
    }

    @Test
    void multipleProperties() {
        fail();
    }

    @Test
    void groupInsert() {
        fail();
    }

    @Test
    void noTail() {
        fail();
    }

    @Test
    void noHead() {
        fail();
    }

    @Test
    @Tag("negative")
    void noNodes() {
        fail();
    }

    @Nested
    class GroupTest {
        @Test
        void noProperty() {
            // Add 'performs' relation between to 'Band' and 'Song'.
            var query = "" +
                    "| performs         |\n" +
                    "|------------------|\n" +
                    "| INSERT Band.Song |\n";
            fail();
        }
    }
}
