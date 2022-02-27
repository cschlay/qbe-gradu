package cli;

import exceptions.QbeException;
import graphs.QueryGraph;
import graphs.ResultGraph;
import db.neo4j.Neo4jTraversal;
import interfaces.QueryParser;
import interfaces.ResultWriter;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the query parse and execution.
 */
public class QueryExecutor {
    private final QueryParser queryParser;
    private final ResultWriter resultWriter;
    private final Neo4jTraversal traversal;

    public QueryExecutor(Neo4jTraversal neo4jTraversal, QueryParser parser, ResultWriter writer) {
        queryParser = parser;
        resultWriter = writer;
        traversal = neo4jTraversal;
    }

    public ResultGraph execute(@NotNull String query) throws QbeException {
        QueryGraph queryGraph = queryParser.parse(query);
        ResultGraph resultGraph = traversal.executeQueryGraph(queryGraph);
        resultWriter.write(queryGraph, resultGraph);
        return resultGraph;
    }

    public ResultGraph executeVerbose(@NotNull String query) throws QbeException {
        QueryGraph queryGraph = queryParser.parse(query);
        System.out.printf("QueryGraph:%n%s%n", queryGraph);

        ResultGraph resultGraph = traversal.executeQueryGraph(queryGraph);
        System.out.printf("ResultGraph:%n%s%n", resultGraph);
        System.out.printf("== Query ==%n%s%n", query);
        System.out.printf("== Result==%n%s%n", resultWriter.write(queryGraph, resultGraph));

        return resultGraph;
    }
}
