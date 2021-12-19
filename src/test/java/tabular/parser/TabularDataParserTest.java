package tabular.parser;

import core.graphs.LogicalExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularDataParser;

import static org.junit.jupiter.api.Assertions.*;

class TabularDataParserTest {
    TabularDataParser parser = new TabularDataParser();

    @Test
    @DisplayName("should parse false and true into boolean")
    void parseBoolean() {
        assertEquals(false, parser.parse("false").value);
        assertEquals(true, parser.parse("true").value);
    }

    @Test
    @DisplayName("should parse empty values to null")
    void parseEmpty() {
        assertNull(parser.parse("").value);
        assertNull(parser.parse("null").value);
    }

    @Test
    @DisplayName("should parse numeric values")
    void parseNumeric() {
        assertEquals(3, parser.parse("3").value);
        assertEquals(3.0, parser.parse("3.0").value);
    }

    @Test
    @DisplayName("should parse strings")
    void parseString() {
        assertEquals("", parser.parse("\"\"").value);
        assertEquals("graph", parser.parse("\"graph\"").value);
    }

    @Test
    @DisplayName("should parse logical expressions")
    void parseLogicalExpression() {
        assertInstanceOf(LogicalExpression.class, parser.parse("> 3").value);
    }

    // The tests for syntax errors could be implemented.
}
