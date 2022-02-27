package tabular.parser;

import graphs.QbeEdge;
import graphs.QbeNode;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularEntityParser;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.*;


class TabularEntityParserTest {
    private final TabularEntityParser parser = new TabularEntityParser();

    @Test
    void parseCountAggregationEdge() {
        var header = new TabularHeader("contains");
        var edge = new QbeEdge("contains");
        var tokens = new String[] { "COUNT", "Node", "Node", "Topic" };
        parser.parseCountAggregation(header, edge, tokens);

        assertEquals("Topic", edge.aggregationGroup);
        assertTrue(header.selected);
        assertEquals("Topic",header.entityName);
        assertEquals("contains.count", header.displayName);
    }

    @Test
    void parseCountAggregationEdgeWithAlias() {
        var header = new TabularHeader("contains");
        var edge = new QbeEdge("contains");
        var tokens = new String[] { "COUNT", "Node", "Node", "Topic", "AS", "topics" };
        parser.parseCountAggregation(header, edge, tokens);

        assertEquals("Topic", edge.aggregationGroup);
        assertTrue(header.selected);
        assertEquals("Topic",header.entityName);
        assertEquals("topics", header.displayName);
    }

    @Test
    void parseCountAggregationNode() {
        var header = new TabularHeader("Book");
        var edge = new QbeNode("Book");
        var tokens = new String[] { "COUNT" };
        parser.parseCountAggregation(header, edge, tokens);

        assertNull(edge.aggregationGroup);
        assertTrue(header.selected);
        assertEquals("Book",header.entityName);
        assertEquals("Book.count", header.displayName);
    }

    @Test
    void parseCountAggregationNodeWithAlias() {
        var header = new TabularHeader("Book");
        var edge = new QbeNode("Book");
        var tokens = new String[] { "COUNT", "AS", "books" };
        parser.parseCountAggregation(header, edge, tokens);

        assertNull(null, edge.aggregationGroup);
        assertTrue(header.selected);
        assertEquals("Book",header.entityName);
        assertEquals("books", header.displayName);
    }
}
