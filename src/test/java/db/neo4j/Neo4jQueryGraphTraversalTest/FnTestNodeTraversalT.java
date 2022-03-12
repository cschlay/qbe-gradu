package db.neo4j.Neo4jQueryGraphTraversalTest;

import base.QueryBaseResetEachTest;

class FnTestNodeTraversalT extends QueryBaseResetEachTest {
    /*Neo4jNodeTraversal traversal;

    @BeforeEach
    void setup() {
        traversal = new Neo4jNodeTraversal(db);
    }

    @Test
    @DisplayName("should filter nodes by name and properties")
    void filterByNameAndProperties() throws Exception {
        var fx = new Object() { long validId; long invalidId; };
        run(tx -> {
            var valid = tx.createNode(Label.label("Course"));
            valid.setProperty("title", "Introduction to Graph Theory");
            valid.setProperty("difficulty", 4);
            var invalid = tx.createNode(Label.label("Course"));
            invalid.setProperty("title", "UI Design");
            invalid.setProperty("difficulty", 4);
            tx.commit();
            fx.validId = valid.getId();
            fx.invalidId = invalid.getId();}
        );

        var queryNode = new QbeNode("Course");
        queryNode.properties.put("title", new QbeData("Introduction to .*"));
        queryNode.properties.put("difficulty", new QbeData(4));

        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);
        assertNotNull(resultGraph.get(String.valueOf(fx.validId)));
        assertNull(resultGraph.get(String.valueOf(fx.invalidId)));
    }

    @Test
    @DisplayName("should retrieve nodes by name")
    void retrieveNodesByName() throws Exception {
        run(tx -> {
            tx.createNode(Label.label("Course"));
            tx.createNode(Label.label("Book"));
            tx.createNode(Label.label("Music"));
            tx.commit();
        });

        var queryNode = new QbeNode("Course");
        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);
        eachNode(resultGraph, (tx, node) -> assertEquals("Course", node.name));
    }

    @Test
    @DisplayName("should retrieve node by id")
    void retrieveNodeById() throws Exception {
        var fx = new Object() { long id; };
        run(tx -> {
            var node = tx.createNode(Label.label("Course"));
            fx.id = node.getId();
            tx.commit();
        });

        var queryNode = new QbeNode(fx.id, "Course");
        var queryGraph = new QueryGraph();
        queryGraph.put(queryNode.name, queryNode);

        var resultGraph = traversal.traverse(queryGraph);
        assertNotNull(resultGraph.get(String.valueOf(fx.id)));
    }

    // There may be case where you put an id but there are multiple relations?
    // TODO: INSERT, UPDATE, and DELETE*/
}
