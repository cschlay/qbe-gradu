package db.neo4j;

import core.exceptions.BaseException;
import core.graphs.GraphEntity;
import core.graphs.QbeNode;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Iterator;

/**
 * Static functions for Neo4j -related actions such as conversion from String to Neo4j object.
 */
public class Neo4j {
    private Neo4j() {}

    public static long id(GraphEntity entity) throws BaseException {
        if (entity.id == null)  {
            throw new BaseException("Entity '%s' doesn't have id", entity.name);
        }
        return Long.parseLong(entity.id);
    }

    public static Iterator<Node> nodes(Transaction tx, QbeNode queryNode) {
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            return tx.findNodes(label);
        }

        return tx.getAllNodes().stream().iterator();
    }
}
