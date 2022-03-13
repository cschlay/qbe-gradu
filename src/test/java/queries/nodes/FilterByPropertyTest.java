package queries.nodes;

import base.QueryBaseStaticTest;
import graphs.LogicalExpression;
import graphs.QbeData;
import graphs.QbeNode;
import graphs.QueryGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Node - Filter by Property")
class FilterByPropertyTest extends QueryBaseStaticTest {
    private QueryGraph queryGraph;
    private QbeNode queryNode;

    @BeforeAll
    static void setup() throws Exception {
        run(tx -> {
            Label label = Label.label("Book");
            Node n1 = tx.createNode(label);
            n1.setProperty("title", "Logic");
            n1.setProperty("year", 2022);
            n1.setProperty("used", false);
            n1.setProperty("price", 20.99);

            Node n2 = tx.createNode(label);
            n2.setProperty("title", "Graphs");
            n2.setProperty("year", 2019);
            n2.setProperty("used", true);
            n2.setProperty("price", 30.99);

            Node n3 = tx.createNode(label);
            n3.setProperty("title", "Algebra");
            n3.setProperty("year", 2010);
            n3.setProperty("used", false);
            n3.setProperty("price", 70.99);
            tx.commit();
        });
    }

    @BeforeEach
    void setupQueryGraph() {
        queryGraph = new QueryGraph();
        queryNode = new QbeNode("Book");
        queryGraph.put(queryNode);
    }

    @ParameterizedTest()
    @ArgumentsSource(PropertyProvider.class)
    void fixedValues(PropertyArg arg) throws Exception {
        queryNode.properties.put(arg.property, new QbeData(arg.value));
        eachNode(execute(queryGraph), (tx, node) -> assertEquals(arg.value, node.property(arg.property)));

        var query = "" +
                "| Book  | %s* |\n" +
                "|-------+----|\n" +
                "| QUERY | %s |\n";
        Object value = arg.value instanceof String ? String.format("\"%s\"", arg.value): arg.value;
        eachNode(execute(query, arg.property, value), (tx, node) -> assertEquals(arg.value, node.property(arg.property)));
    }


    @Test
    void logicalExpression() throws Exception {
        queryNode.properties.put("price", new QbeData(new LogicalExpression("< 50.0")));
        eachNode(execute(queryGraph), ((tx, node) -> {
            Object property = node.property("price");
            assert property != null;
            assertTrue((double) property < 50.0);
        }));

        var query = "" +
                "| Book  | price* |\n" +
                "|-------+--------|\n" +
                "| QUERY | < 50.0 |\n";
        eachNode(execute(query), ((tx, node) -> {
            Object property = node.property("price");
            assert property != null;
            assertTrue((double) property < 50.0);
        }));
    }

    @Test
    void regularExpression() throws Exception {
        queryNode.properties.put("title", new QbeData("/Alg.*/"));
        eachNode(execute(queryGraph), ((tx, node) -> assertEquals(node.property("title"), "Algebra")));

        var query = "" +
                "| Book  | title*  |\n" +
                "|-------+---------|\n" +
                "| QUERY | /Alg.*/ |\n";
        eachNode(execute(query), ((tx, node) -> assertEquals(node.property("title"), "Algebra")));
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
                new PropertyArg("Boolean", "used", false),
                new PropertyArg("Double", "price", 20.99),
                new PropertyArg("Integer", "year", 2022),
                new PropertyArg("String", "title", "Algebra")
        ).map(Arguments::of);
    }
}
