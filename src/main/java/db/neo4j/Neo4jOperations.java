package db.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class Neo4jOperations {
    private final GraphDatabaseService db;

    public Neo4jOperations(GraphDatabaseService database) {
        db = database;
    }

    public void reset() {
        try (Transaction transaction = db.beginTx()) {
            transaction.getAllNodes().forEach(Node::delete);
            transaction.getAllRelationships().forEach(Relationship::delete);
            transaction.commit();
        }
    }
}
