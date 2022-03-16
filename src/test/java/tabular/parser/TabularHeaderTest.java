package tabular.parser;

import exceptions.SyntaxError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Parser] TabularHeader Class")
class TabularHeaderTest {
    @Nested
    class UnknownNameTest {
        @Test
        void simpleName() throws SyntaxError {
            var header = new TabularHeader("Book");
            assertEquals("Book", header.name);
            assertEquals("Book", header.displayName);
        }

        @Test
        void withDisplayName() throws SyntaxError {
            var header = new TabularHeader("contains AS includes");
            assertEquals("contains", header.name);
            assertEquals("includes", header.displayName);
        }

        @Test
        void withSelect() throws SyntaxError {
            var header = new TabularHeader("price*");
            assertEquals("price", header.name);
            assertEquals("price", header.displayName);
            assertTrue(header.selected);
        }

        @Test
        void withDisplayNameAndSelect() throws SyntaxError {
            var header = new TabularHeader("price AS cost*");
            assertEquals("price", header.name);
            assertEquals("cost", header.displayName);
            assertTrue(header.selected);
        }
    }

    @Nested
    class EdgeHeaderTest {
        @Test
        void simpleName() throws Exception {
            var header = new TabularHeader("contains.difficulty");
            assertEquals("contains", header.entityName);
            assertEquals("contains.difficulty", header.displayName);
            assertEquals("difficulty", header.name);
        }

        @Test
        void withDisplayName() throws Exception {
            var header = new TabularHeader("contains.difficulty AS Level");
            assertEquals("contains", header.entityName);
            assertEquals("Level", header.displayName);
            assertEquals("difficulty", header.name);
        }
    }

    @Nested
    class NodeHeadersTest {
        @Test
        void simpleName() throws Exception {
            var header = new TabularHeader("Course.title");
            assertEquals("Course", header.entityName);
            assertEquals("title", header.name);
            assertEquals("Course.title", header.displayName);
        }

        @Test
        void withDisplayName() throws Exception {
            var header = new TabularHeader("Course.title AS Caption");
            assertEquals("Course", header.entityName);
            assertEquals("title", header.name);
            assertEquals("Caption", header.displayName);
        }
    }
}
