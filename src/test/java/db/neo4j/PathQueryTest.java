package db.neo4j;

import base.QueryBaseTest;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathQueryTest extends QueryBaseTest {
    Neo4jTraversal traversal;

    @BeforeEach
    void setup() {
        traversal = new Neo4jTraversal(db);
    }

    @Test
    void simplePath() throws Exception {
        // o -> o, where the head is doesn't exist
        run(tx -> {
            var v1 = tx.createNode(Label.label("v1"));
            var v2 = tx.createNode(Label.label("v2"));
            v1.createRelationshipTo(v2, RelationshipType.withName("e1"));
            tx.commit();
        });

        // TODO: Finish result already known.
    }

    @Test
    void simpleCycle() throws Exception {
        // Query graph has cycle
    }

    /**
     * Db: (v1, e1, v2) and (v2, e2, v3) without (e1, v2, e2)
     * Query: (v1, e1, v2, e2, v3)
     * Result: None
     */
    @Test
    void excludePartialPaths() throws Exception {
        run(tx -> {
            var v1 = tx.createNode(Label.label("v1"));
            var v2a = tx.createNode(Label.label("v2"));
            var v2b = tx.createNode(Label.label("v2"));
            var v3 = tx.createNode(Label.label("v3"));

            v1.createRelationshipTo(v2a, RelationshipType.withName("e1"));
            v2b.createRelationshipTo(v3, RelationshipType.withName("e2"));
            tx.commit();
        });

        var v1 = new QbeNode("v1");
        var v2 = new QbeNode("v2");
        var v3 = new QbeNode("v3");

        var e1 = new QbeEdge("e1");
        e1.tailNode = v1;
        e1.headNode = v2;

        var e2 = new QbeEdge("e2");
        e2.tailNode = v2;
        e2.headNode = v3;

        var queryGraph = new QueryGraph();
        queryGraph.put(v1);
        queryGraph.put(v2);
        queryGraph.put(v3);
        queryGraph.put(e1);
        queryGraph.put(e2);
        System.out.println(queryGraph);

        run(tx -> {
           var tr = new QueryGraphTraversal(tx);
           var resultGraph = tr.traverse(queryGraph);
            System.out.println(resultGraph);
           assertTrue(resultGraph.isEmpty(), "Graph is not empty!");
        });
    }

    /**
     * Db: (v1, e1, v2) and (v3, e2, v2) such as o -> o <- o
     * Query: ()
     */
    @Test
    void twoPaths() throws Exception {
        run(tx -> {

        });
    }
}
