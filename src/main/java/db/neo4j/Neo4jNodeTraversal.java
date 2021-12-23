package db.neo4j;

import core.exceptions.InvalidNodeException;
import core.graphs.QbeData;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
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

        try (Transaction transaction = db.beginTx()) {
            for (var queryNode : queryGraph.values()) {
                var resultNodes = visitQueryNode(transaction, queryNode);
                resultGraph.putAll(resultNodes);
                resultGraph.unvisitedEdges.addAll(queryNode.edges.values());
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

    private HashMap<String, QbeNode> visitQueryNode(Transaction transaction, QbeNode queryNode) {
        Iterator<Node> neo4jNodes = getNeo4jNodes(transaction, queryNode);
        var resultNodes = new HashMap<String, QbeNode>();

        while (neo4jNodes.hasNext()) {
            try {
                Node neo4jNode = neo4jNodes.next();
                QbeNode resultNode = visitNeo4jNode(neo4jNode, queryNode);
                resultNodes.put(resultNode.id, resultNode);
            } catch (InvalidNodeException exception) {
                // The node is discarded
            }
        }

        return resultNodes;
    }

    private QbeNode visitNeo4jNode(Node neo4jNode, QbeNode query) throws InvalidNodeException {
        Map<String, QbeData> properties = new Neo4jPropertyTraversal(query).getProperties(neo4jNode);
        var resultNode = new QbeNode(neo4jNode.getId(), query.name);
        resultNode.properties.putAll(properties);

        return resultNode;
    }
}
