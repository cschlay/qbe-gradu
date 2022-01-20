package base;

import org.neo4j.graphdb.*;

/**
 * Reduce the boilerplate when working with test data.
 */
public class TestUtils {
    private final Transaction tx;

    public TestUtils(Transaction transaction) {
        tx = transaction;
    }

    public Node newNode(String label) {
        Node node = tx.createNode(Label.label(label));

        // TODO: Create properties
        return node;
    }

    public Iterable<Relationship> relations(String label, Node node) {
        return node.getRelationships(RelationshipType.withName("performs"));
    }
}
