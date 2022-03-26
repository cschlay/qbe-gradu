package parser;

import graphs.QbeNode;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syntax.tabular.Headers;
import syntax.tabular.TabularHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Parser] Header Class")
class HeaderTest {
    private static Headers headers;

    @BeforeAll
    static void setup() {
        var headerList = new String[]{ "Book", "Book.id", "Topic" };
        headers = new Headers(headerList);
    }

    @Test
    void constructor() {
        assertEquals(3, headers.length);
    }

    @Test
    void get() {
        TabularHeader header = headers.get(0);
        assertEquals("Book", header.name);
    }

    @Test
    void getDisplayName() {
        String name = headers.getDisplayName(0);
        assertEquals("Book", name);
    }

    @Test
    void getIndex() {
        var node = new QbeNode("Book");
        @Nullable Integer header = headers.getIndex(node, "id");
        assertEquals(1, header);
    }
}
