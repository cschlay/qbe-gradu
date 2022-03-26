package queries.edges;

import base.QueryBaseStaticTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[Query] Edge - Filter by Property")
class FilterByPropertyTest extends QueryBaseStaticTest {
    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Node n1 = tx.createNode(Label.label("Author"));
            Node n2 = tx.createNode(Label.label("Book"));
            Relationship e1 = n1.createRelationshipTo(n2, RelationshipType.withName("writes"));
            e1.setProperty("code", "box");
            e1.setProperty("started", 2021);
            e1.setProperty("reviewed", false);
            e1.setProperty("hours", 39.40);

            Node n3 = tx.createNode(Label.label("Author"));
            Node n4 = tx.createNode(Label.label("Book"));
            Relationship e2 = n3.createRelationshipTo(n4, RelationshipType.withName("writes"));
            e2.setProperty("code", "thread");
            e2.setProperty("started", 2020);
            e2.setProperty("reviewed", true);
            e2.setProperty("hours", 200.0);
            tx.commit();
        });
    }

    @ParameterizedTest
    @ArgumentsSource(PropertyProvider.class)
    void fixedValues(PropertyArg arg) throws Exception {
        var query = "" +
                "| writes            | %s* |\n" +
                "|-------------------+-----|\n" +
                "| QUERY Author.Book | %s  |\n";
        Object value = arg.value instanceof String ? String.format("\"%s\"", arg.value): arg.value;
        eachEdge(execute(query, arg.property, value), (tx, edge) -> assertEquals(arg.value, edge.getProperty(arg.property)));
    }

    @Test
    void logicalExpression() throws Exception {
        var query = "" +
                "| writes            | started* |\n" +
                "|-------------------+----------|\n" +
                "| QUERY Author.Book | >= 2019  |\n";
        eachEdge(execute(query), (tx, edge) -> {
            Object property = edge.getProperty("started");
            assert property != null;
            assertTrue((int) property >= 2019);
        });
    }

    @Test
    void regularExpression() throws Exception {
        var query = "" +
                "| writes            | code*   |\n" +
                "|-------------------+---------|\n" +
                "| QUERY Author.Book | /b.*/   |\n";
        eachEdge(execute(query), (tx, edge) -> assertEquals("box", edge.getProperty("code")));
    }
}

class PropertyArg {
    public final String name;
    public final String property;
    public final Object value;

    public PropertyArg(String name, String property, Object value) {
        this.name = name;
        this.property = property;
        this.value = value;
    }

    public String toString() {
        return name;
    }
}

class PropertyProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                new PropertyArg("Boolean", "reviewed", false),
                new PropertyArg("Double", "hours", 200.0),
                new PropertyArg("Integer", "started", 2021),
                new PropertyArg("String", "code", "box")
        ).map(Arguments::of);
    }
}
