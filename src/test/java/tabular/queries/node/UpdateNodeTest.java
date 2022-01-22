package tabular.queries.node;

import base.QueryBaseTest;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateNodeTest extends QueryBaseTest {
    @Test
    void byId() throws Exception {
        // Fix typos in the 'Book' 'title'
        var fx = new Object() { Node node; };
        run(tx -> {
            fx.node = tx.createNode(Label.label("Book"));
            fx.node.setProperty("title", "Undr the Dome");
            tx.commit();
        });
        long id = fx.node.getId();

        var query = "" +
                "| Book   | id* | title*                     |\n" +
                "|--------+-----+----------------------------|\n" +
                "| UPDATE | %s  | UPDATE \"Under the Dome\" |\n";
        var graph = execute(query, id);

        eachNode(graph, (tx, node) -> {
            var title = "Under the Dome";
            assertEquals(title, node.property("title"));

            var neo4jNode = tx.getNodeById(node.longId());
            assertEquals(title, neo4jNode.getProperty("title"));
        });
    }

    @Test
    void byName() throws Exception {
        var fx = new Object() { Node course; };
        run(tx -> {
            tx.createNode(Label.label("Book"));
            tx.createNode(Label.label("Book"));
            fx.course = tx.createNode(Label.label("Course"));
            fx.course.setProperty("quantity", 1);
            tx.commit();;
        });
        long courseId = fx.course.getId();

        var query = "" +
                "| Book   | id* | quantity* |\n" +
                "|--------+-----+-----------|\n" +
                "| UPDATE |     | UPDATE 0  |\n";
        var graph = execute(query);

        eachNode(graph, (tx, node) -> {
            assertEquals(0, node.property("quantity"));
        });
        run(tx -> {
            var node = tx.getNodeById(courseId);
            assertEquals(1, node.getProperty("quantity"));
        });
    }

    @Test
    void byProperties() throws Exception {
        // Update books that are not published yet
        var fx = new Object() { long id; };
        run(tx -> {
            var book1 = tx.createNode(Label.label("Book"));
            book1.setProperty("year", 2031);
            book1.setProperty("published", true);

            var book2 = tx.createNode(Label.label("Book"));
            book2.setProperty("year", 2019);
            book2.setProperty("published", true);
            tx.commit();

            fx.id = book2.getId();
        });

        var query = "" +
                "| Book   | year*  | published*   |\n" +
                "|--------+--------+--------------|\n" +
                "| UPDATE | > 2030 | UPDATE false |\n";
        var graph = execute(query);

        eachNode(graph, (tx, node) -> {
            assertEquals(false, tx.getNodeById(node.longId()).getProperty("published"));
            assertEquals(false, node.property("published"));
        });
        // Ensure the other node is not changed.
        run(tx -> assertEquals(true, tx.getNodeById(fx.id).getProperty("published")));
    }
}
