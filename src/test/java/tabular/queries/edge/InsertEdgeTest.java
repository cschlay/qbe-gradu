package tabular.queries.edge;

import base.QueryBaseTest;
import db.neo4j.Neo4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import static org.junit.jupiter.api.Assertions.*;

class InsertEdgeTest extends QueryBaseTest {
    Node band;
    Node song;

    @BeforeEach
    void setup() throws Exception {
        run(tx -> {
            band = Neo4j.createNode(tx, "Band");
            song = Neo4j.createNode(tx, "Song");
            tx.commit();
        });
    }

    @Test
    void withoutProperties() throws Exception {
        // Add 'performs' relation between to 'Band' and 'Song'.
        var query = "" +
                "| performs         | Band  | Band.id* | Song  | Song.id* |\n" +
                "|------------------+-------+----------+-------+----------|\n" +
                "| INSERT Band.Song | QUERY | %s       | QUERY | %s       |\n";
        var graph = execute(query, band.getId(), song.getId());

        assertEdge(graph, (tx, edge) -> {
            Relationship relation = Neo4j.relationById(tx, edge);
            assertTrue(relation.getStartNodeId() == band.getId() || relation.getEndNodeId() == song.getId());
        });
    }

    @Test
    void oneProperty() throws Exception {
        // Adds property 'feat' with 'performs' relation between 'Band' and 'Song' to indicate guest artists of the song.
        var query = "" +
                "| performs          | performs.feat* | Band  | Band.id* | Song  | Song.id* |\n" +
                "|-------------------+----------------+-------+----------+-------+----------|\n" +
                "| INSERT Band.Song  | true           | QUERY | %s       | QUERY | %s       |\n";
        var graph = execute(query, band.getId(), song.getId());

        assertEdge(graph, (tx, edge) -> {
            assertEquals(true, edge.property("feat"));
            Relationship relation = Neo4j.relationById(tx, edge);
            assertEquals(true, relation.getProperty("feat"));
        });
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
