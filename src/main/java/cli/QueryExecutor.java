package cli;

import exceptions.QbeException;
import graphs.QueryGraph;
import graphs.ResultGraph;
import db.neo4j.Neo4jTraversal;
import interfaces.QueryParser;
import interfaces.ResultWriter;
import org.jetbrains.annotations.NotNull;
import utilities.Utils;

/**
 * Handles the query parse and execution.
 */
public class QueryExecutor {
    private final QueryParser queryParser;
    private final ResultWriter resultWriter;
    private final Neo4jTraversal traversal;
    private final boolean printEnabled;

    public QueryExecutor(Neo4jTraversal neo4jTraversal, QueryParser parser, ResultWriter writer) {
        queryParser = parser;
        resultWriter = writer;
        traversal = neo4jTraversal;

        printEnabled = !"1".equals(System.getProperty("noprint"));
    }

    public ResultGraph execute(@NotNull String query) throws QbeException {
        QueryGraph[] queryGraphs = queryParser.parse(query);

        ResultGraph resultGraph = new ResultGraph();
        for (var queryGraph : queryGraphs) {
            ResultGraph result = traversal.executeQueryGraph(queryGraph);
            resultGraph = resultGraph.union(result);
        }

        if (printEnabled) System.out.println(resultWriter.write(Utils.first(queryGraphs), resultGraph));

        return resultGraph;
    }

    public ResultGraph executeVerbose(@NotNull String query) throws QbeException {
        QueryGraph[] queryGraphs = queryParser.parse(query);

        ResultGraph resultGraph = new ResultGraph();
        for (int i = 0; i < queryGraphs.length; i++) {
            QueryGraph queryGraph = queryGraphs[i];
            if (printEnabled) System.out.printf("QueryGraph %s:%n%s%n", i, queryGraph);

            ResultGraph result = traversal.executeQueryGraph(queryGraph);
            resultGraph = resultGraph.union(result);
            if (printEnabled) System.out.printf("ResultGraph %s:%n%s%n", i, resultGraph);
            if (printEnabled) System.out.printf("== Query ==%n%s%n", query);
        }

        if (printEnabled) System.out.printf("== Result==%n%s%n", resultWriter.write(Utils.first(queryGraphs), resultGraph));
        return resultGraph;
    }
}
