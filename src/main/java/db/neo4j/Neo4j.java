package db.neo4j;

import exceptions.QueryException;
import graphs.GraphEntity;
import graphs.QbeEdge;
import graphs.QbeNode;
import org.neo4j.graphdb.*;

import java.util.Iterator;

/**
 * Static functions for Neo4j -related actions such as conversion from String to Neo4j object.
 */
public class Neo4j {
    private Neo4j() {}

    public static long id(GraphEntity entity) throws QueryException {
        if (entity.id == null)  {
            throw new QueryException("Entity '%s' doesn't have id", entity.name);
        }
        return Long.parseLong(entity.id);
    }

    public static class Edge {
        private Edge() {}

        public static Relationship create(String label, Node tail, Node head) {
            return tail.createRelationshipTo(head, RelationshipType.withName(label));
        }
    }

    public static Relationship relationById(Transaction tx, QbeEdge edge) throws QueryException {
        return tx.getRelationshipById(id(edge));
    }

    public static Node createNode(Transaction tx, String label) {
        return tx.createNode(Label.label(label));
    }


    public static Node findNode(Transaction tx, QbeNode node) throws QueryException {
        long id = id(node);
        return tx.getNodeById(id);
    }

    public static Iterator<Node> nodes(Transaction tx, QbeNode queryNode) {
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            return tx.findNodes(label);
        }

        return tx.getAllNodes().stream().iterator();
    }

    public static void resetDatabase(GraphDatabaseService databaseService) {
        try (Transaction transaction = databaseService.beginTx()) {
            transaction.getAllNodes().forEach(Node::delete);
            transaction.getAllRelationships().forEach(Relationship::delete);
            transaction.commit();
        }
    }
}
