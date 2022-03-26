package parser;

import exceptions.SyntaxError;
import graphs.QbeNode;
import graphs.QueryGraph;
import enums.QueryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import syntax.tabular.TabularHeader;
import syntax.tabular.TabularNodeParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("[Parser] TabularNodeParser Class")
class TabularNodeParserTest {
    @ParameterizedTest
    @EnumSource(QueryType.class)
    void parseEntity(QueryType type) throws Exception {
        TabularNodeParser parser = setup();
        var header = new TabularHeader("Movie");
        QbeNode node = parser.parseEntity(header, type.name());
        assertEquals("Movie", node.name);
        assertEquals(type, node.type);
    }

    @Test
    void parseEntityErrors() {
        TabularNodeParser parser = setup();
        var header = new TabularHeader("movie");
        assertThrows(SyntaxError.class, () -> parser.parseEntity(header, QueryType.QUERY.name()));
    }

    @Test
    void parsePropertyNewNode() {
        TabularNodeParser parser = setup();
        var header = new TabularHeader("Movie.name");
        QbeNode node = parser.parseProperty(header, "\"Spider-Man\"");
        assertEquals("Spider-Man", node.getProperty("name"));
    }

    @Test
    void parsePropertyExistingNode() {
        var graph = new QueryGraph();
        var node0 = new QbeNode("Movie");
        graph.put(node0);

        var parser = new TabularNodeParser(graph);
        var header = new TabularHeader("Movie.name");

        QbeNode node = parser.parseProperty(header, "\"Spider-Man 2\"");
        assertEquals(node0, node);
        assertEquals("Spider-Man 2", node0.getProperty("name"));
    }

    @Test
    void parseIdProperty() {
        TabularNodeParser parser = setup();
        var header = new TabularHeader("Movie.id");
        QbeNode node = parser.parseProperty(header, "1");
        assertEquals(1, node.getProperty("id"));
        assertEquals("1", node.id);
    }

    private TabularNodeParser setup() {
        var graph = new QueryGraph();
        return new TabularNodeParser(graph);
    }
}
