package tabular.queries;

import base.QueryBaseTest;
import db.neo4j.Neo4jTraversal;
import demo.CourseGraphDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import tabular.TabularTestClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeQueryTest extends QueryBaseTest {
    @Test
    @DisplayName("should query all edges")
    void queryAllEdges() throws Exception {
        var assistantLabel = Label.label("Assistant");
        var courseLabel = Label.label("Course");
        var teachesLabel = RelationshipType.withName("teaches");

        inTransaction(tx -> {
            var assistant1 = tx.createNode(assistantLabel);
            var course1 = tx.createNode(courseLabel);
            var relation = assistant1.createRelationshipTo(course1, teachesLabel);
            relation.setProperty("monday", true);

            var assistant2 = tx.createNode(assistantLabel);
            var course2 = tx.createNode(courseLabel);

            tx.commit();
        });

        var query =
                ""
                        + "| Course.id | teaches.Assistant.Course.monday |\n"
                        + "|-----------+---------------------------------|\n"
                        + "|           | true                            |\n";

        var session = getSession();
        var queryGraph = session.parseQuery(query);

        var resultGraph = session.executeQuery(queryGraph);
        System.out.println("Result:");
        System.out.println(session.toString(queryGraph, resultGraph));
    }

    /*@Test
    @DisplayName("should query all edges using explicit notation")
    void queryAllEdgesExplicit() throws Exception {
        var query =
                ""
                        + "| Course.id | teaches.Assistant.Course.monday |\n"
                        + "|-----------+---------------------------------|\n"
                        + "|           | true                            |\n";

        var graph = executeQuery(query);
        System.out.println(graph);

        assertTrue(graph.order() > 0);

        for (var node : graph.values()) {
            assertEquals(1, node.edges.size());

            for (var edge : node.edges.values()) {
                assertEquals("teaches", edge.name);
                assertEquals(true, edge.properties.get("monday").value);
            }
        }
    }*/
}
