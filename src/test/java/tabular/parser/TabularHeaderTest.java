package tabular.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.*;

class TabularHeaderTest {
    @Nested
    class LongEdgeHeaderTest {
        @Test
        @DisplayName("should parse the node header without display name")
        void parseWithoutDisplayName() throws Exception {
            var header = new TabularHeader("contains.Course.Topic.difficulty");

            assertEquals("contains.Course.Topic.difficulty", header.displayName);
            assertEquals("contains", header.name);
            assertEquals("difficulty", header.propertyName);
            assertEquals("Course", header.tailNodeName);
            assertEquals("Topic", header.headNodeName);
            assertFalse(header.selected);
        }

        @Test
        @DisplayName("should parse the node header with display name")
        void parseWithDisplayName() throws Exception {
            var header = new TabularHeader("contains.Course.Topic.difficulty AS Difficulty");

            assertEquals("Difficulty", header.displayName);
            assertEquals("contains", header.name);
            assertEquals("difficulty", header.propertyName);
            assertEquals("Course", header.tailNodeName);
            assertEquals("Topic", header.headNodeName);
            assertFalse(header.selected);
        }
    }

    @Nested
    class ShortEdgeHeaderTest {
        @Test
        @DisplayName("should parse the node header without display name")
        void parseWithoutDisplayName() throws Exception {
            var header = new TabularHeader("contains.difficulty");

            assertEquals("contains.difficulty", header.displayName);
            assertEquals("contains", header.name);
            assertEquals("difficulty", header.propertyName);
            assertNull(header.tailNodeName);
            assertNull(header.headNodeName);
            assertFalse(header.selected);
        }

        @Test
        @DisplayName("should parse the node header with display name")
        void parseWithDisplayName() throws Exception {
            var header = new TabularHeader("contains.difficulty AS Difficulty");

            assertEquals("Difficulty", header.displayName);
            assertEquals("contains", header.name);
            assertEquals("difficulty", header.propertyName);
            assertNull(header.tailNodeName);
            assertNull(header.headNodeName);
            assertFalse(header.selected);
        }
    }

    @Nested
    class NodeHeadersTest {
        @Test
        @DisplayName("should parse the node header without display name")
        void parseWithoutDisplayName() throws Exception {
            var header = new TabularHeader("Course.title");

            assertEquals("Course", header.name);
            assertEquals("title", header.propertyName);
            assertEquals("Course.title", header.displayName);
            assertFalse(header.selected);
        }

        @Test
        @DisplayName("should parse the node header with display name")
        void parseWithDisplayName() throws Exception {
            var header = new TabularHeader("Course.title AS Caption");

            assertEquals("Course", header.name);
            assertEquals("title", header.propertyName);
            assertEquals("Caption", header.displayName);
            assertFalse(header.selected);
        }

        // TODO: Add test for selection*
    }
}
