package db.neo4j;

import core.exceptions.IdConstraintException;
import core.exceptions.InvalidNodeException;
import core.graphs.*;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Neo4jNodeTraversal {
    private final GraphDatabaseService db;

    public Neo4jNodeTraversal(GraphDatabaseService database) {
        db = database;
    }

    public ResultGraph traverse(QueryGraph queryGraph) {
        var resultGraph = new ResultGraph();
        var edgeTraversal = new Neo4jEdgeTraversal(resultGraph);

        try (Transaction transaction = db.beginTx()) {
            // TODO: should a queue be used instead, so that all insert and update is performed first?
            for (var queryNode : queryGraph.values()) {
                Map<String, QbeNode> resultNodes = visitQueryNode(edgeTraversal, transaction, queryNode);
                if (!resultNodes.isEmpty()) {
                    resultGraph.putAll(resultNodes);
                    resultGraph.unvisitedEdges.addAll(queryNode.edges.values());
                }
            }

            transaction.commit();
        }

        return resultGraph;
    }

    public QbeNode createNode(Transaction tx, QbeNode queryNode)  {
        Label label = Label.label(queryNode.name);
        Node neo4jNode = tx.createNode(label);

        var resultNode = new QbeNode(neo4jNode.getId(), queryNode.name);
        new Neo4jPropertyTraversal(queryNode).mutableCopyProperties(neo4jNode, resultNode);

        return resultNode;
    }

    private HashMap<String, QbeNode> visitQueryNode(Neo4jEdgeTraversal edgeTraversal, Transaction tx, QbeNode queryNode) {
        var resultNodes = new HashMap<String, QbeNode>();

        if (queryNode.type == QueryType.INSERT) {
            QbeNode resultNode =  createNode(tx, queryNode);
            resultNodes.put(resultNode.id, resultNode);
            return resultNodes;
        }

        Iterator<Node> neo4jNodes = Neo4j.nodes(tx, queryNode);

        while (neo4jNodes.hasNext()) {
            Node neo4jNode = neo4jNodes.next();
            try {
                QbeNode resultNode = visitNeo4jNode(neo4jNode, queryNode);
                edgeTraversal.query(tx, neo4jNode, queryNode, resultNode);
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
