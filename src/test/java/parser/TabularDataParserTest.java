package parser;

import enums.QueryType;
import graphs.LogicalExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularDataParser;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Parser] TabularDataParser Class")
class TabularDataParserTest {
    TabularDataParser parser = new TabularDataParser();

    @Nested
    class TokenizationTest {
        @Test
        void deleteExpression() {
            var token = parser.tokenize("DELETE");
            assertEquals(QueryType.DELETE, token.type);
            assertEquals("", token.queryValue);
        }

        @Test
        void sumExpression() {
            var token = parser.tokenize("SUM _");
            assertEquals(QueryType.SUM, token.type);
            assertNull(token.argument);
            assertEquals("", token.queryValue);
        }

        @Test
        void sumWithExampleValue() {
            var token = parser.tokenize("SUM _ example value");
            assertEquals(QueryType.SUM, token.type);
            assertNull(token.argument);
            assertEquals("example value", token.queryValue);
        }

        @Test
        void updateExpression() {
            var token = parser.tokenize("UPDATE any thing");
            assertEquals(QueryType.UPDATE, token.type);
            assertEquals("", token.queryValue);
            assertEquals("any thing", token.argument);
        }
    }

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
}
