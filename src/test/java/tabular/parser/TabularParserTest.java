package tabular.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularParserTest {
    TabularParser parser = new TabularParser();

    // TODO: Test complex cases where both nodes and edges are included
    @Test
    @DisplayName("should parse a node and an edge")
    void parseNodeAndEdge() throws Exception {
        var query = "" +
                "| Book.title | uses.Course.Book.edition |\n" +
                "|------------+--------------------------|\n" +
                "| \"Logic\"  | 3                        |";
        var graph = parser.parse(query);

        var book = graph.get("Book");
        assertEquals("title", book.properties.get("title").value);
        assertEquals(3, book.findEdge("uses").properties.get("edition"));
    }
}
