package tabular.parser;

import core.exceptions.SyntaxError;
import core.graphs.QbeEdge;
import core.graphs.QueryGraph;
import core.graphs.QueryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import syntax.tabular.TabularEdgeParser;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.*;

class TabularEdgeParserTest {
    @ParameterizedTest
    @EnumSource(QueryType.class)
    void parseEntity(QueryType type) throws Exception {
        TabularEdgeParser parser = setup();
        var header = new TabularHeader("writes");

        var value = String.format("%s %s.%s", type.name(), "Artist", "Score");
        QbeEdge edge = parser.parseEntity(header, value);
        assertEquals("writes", edge.name);
        assertEquals(type, edge.type);
        assert edge.tailNode != null;
        assertEquals("Artist", edge.tailNode.name);
        assert edge.headNode != null;
        assertEquals("Score", edge.headNode.name);
    }

    @Test
    void parseEntityErrors() {
        TabularEdgeParser parser = setup();
        assertThrows(SyntaxError.class, () ->
                parser.parseEntity(new TabularHeader("Writes"), QueryType.QUERY.name()));
        assertThrows(SyntaxError.class, () ->
                parser.parseEntity(new TabularHeader("writes"), "QUERY Artist"));
        assertThrows(SyntaxError.class, () ->
                parser.parseEntity(new TabularHeader("writes"), "QUERY"));
        assertThrows(SyntaxError.class, () ->
                parser.parseEntity(new TabularHeader("writes"), "QUERY Artist."));
        assertThrows(SyntaxError.class, () ->
                parser.parseEntity(new TabularHeader("writes"), "QUERY .Score"));
    }

    @Test
    void parsePropertyNewEdge() {
        TabularEdgeParser parser = setup();
        var header = new TabularHeader("writes.hours");
        QbeEdge edge = parser.parseProperty(header, "500");
        assertEquals("writes", edge.name);
        assertEquals(500, edge.getProperty("hours"));
    }

    @Test
    void parsePropertyExistingEdge() throws SyntaxError {
        var graph = new QueryGraph();
        var parser = new TabularEdgeParser(graph);

        QbeEdge edge0 = parser.parseEntity(new TabularHeader("writes"), "QUERY Author.Story");
        QbeEdge edge = parser.parseProperty(new TabularHeader("writes.hours"), "500");
        assertEquals(edge0, edge);
        assertEquals(500, edge0.getProperty("hours"));
    }

    @Test
    void parseIdProperty() {
        TabularEdgeParser parser = setup();
        var header = new TabularHeader("teaches.id");
        QbeEdge edge = parser.parseProperty(header, "1");
        assertEquals(1, edge.getProperty("id"));
        assertEquals("1", edge.id);
    }

    private TabularEdgeParser setup() {
        var graph = new QueryGraph();
        return new TabularEdgeParser(graph);
    }
}
