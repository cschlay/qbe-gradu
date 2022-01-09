package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neo4jNodeTraversal {
    private static final Logger logger = Logger.getLogger(Neo4jNodeTraversal.class.getName());
    private final GraphDatabaseService db;

    public Neo4jNodeTraversal(GraphDatabaseService database) {
        db = database;
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        var resultGraph = new ResultGraph();
        var edgeTraversal = new Neo4jEdgeTraversal(resultGraph);

        try (Transaction transaction = db.beginTx()) {
            for (var queryNode : queryGraph.values()) {
                var resultNodes = visitQueryNode(edgeTraversal, transaction, queryNode);
                if (!resultNodes.isEmpty()) {
                    resultGraph.putAll(resultNodes);
                    resultGraph.unvisitedEdges.addAll(queryNode.edges.values());
                }
            }
        }

        return resultGraph;
    }

    private Iterator<Node> getNeo4jNodes(Transaction transaction, QbeNode queryNode) {
        if (queryNode.name != null) {
            Label label = Label.label(queryNode.name);
            return transaction.findNodes(label);
        }

        return transaction.getAllNodes().stream().iterator();
    }

    private HashMap<String, QbeNode> visitQueryNode(Neo4jEdgeTraversal edgeTraversal, Transaction transaction, QbeNode queryNode) {
        Iterator<Node> neo4jNodes = getNeo4jNodes(transaction, queryNode);
        var resultNodes = new HashMap<String, QbeNode>();

        while (neo4jNodes.hasNext()) {
            Node neo4jNode = neo4jNodes.next();
            try {
                QbeNode resultNode = visitNeo4jNode(neo4jNode, queryNode);
                edgeTraversal.query(neo4jNode, queryNode, resultNode);
                resultNodes.put(resultNode.id, resultNode);
            } catch (InvalidNodeException exception) {
                // Discard node because a property check failed
            }
        }

        return resultNodes;
    }

    private QbeNode visitNeo4jNode(Node neo4jNode, QbeNode query) throws InvalidNodeException {
        try {
            Map<String, QbeData> properties = new Neo4jPropertyTraversal(query).getProperties(neo4jNode);
            var resultNode = new QbeNode(neo4jNode.getId(), query.name);
            resultNode.properties.putAll(properties);

            return resultNode;
        } catch (IdConstraintException exception) {
            throw new InvalidNodeException(exception.getMessage());
        }
    }
}
