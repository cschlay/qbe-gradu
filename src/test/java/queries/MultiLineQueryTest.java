package queries;

import base.QueryBaseStaticTest;
import graphs.ResultGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[Query] Multiline Query")
class MultiLineQueryTest extends QueryBaseStaticTest {
    private static String bookIdA;
    private static String bookIdB;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node bookA = tx.createNode(Label.label("Book"));
            bookA.setProperty("title", "Introduction to Algebra");
            bookA.setProperty("category", "Algebra");
            bookIdA = String.valueOf(bookA.getId());

            Node bookB = tx.createNode(Label.label("Book"));
            bookB.setProperty("title", "Introduction to Graph Theory");
            bookB.setProperty("category", "Graph Theory");
            bookIdB = String.valueOf(bookB.getId());

            tx.commit();
        });
    }

    @Test
    void simpleNode() throws Exception {
        var query = "" +
                "| Book  | title* | category        |\n" +
                "|-------+--------+-----------------|\n" +
                "| QUERY |        | \"Algebra\"       |\n" +
                "| QUERY |        | \"Graph Theory\"  |\n";
        ResultGraph graph = execute(query);
        assertEquals(2, graph.order());
        assertTrue(graph.containsKey(bookIdA));
        assertTrue(graph.containsKey(bookIdB));
    }

    // Only one test has been written, it should be enough to ensure that multiple result graphs get executed.
    // More could be added if DELETE, INSERT, and UPDATE is implemented.
}
