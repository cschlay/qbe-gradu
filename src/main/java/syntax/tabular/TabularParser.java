package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.utilities.Debug;
import interfaces.QueryParser;

import java.util.Arrays;

public class TabularParser implements QueryParser {
    /**
     * Parse the query from string to QueryGraph.

     * @param query as defined in the thesis
     * @return query graphs, could have multiple if more than one row is specified
     */
    public QueryGraph parse(String query) throws SyntaxError {
        String[] rows = query.split("\n");
        if (rows.length < 3) {
            throw new SyntaxError("Query must include header and at least one example row.");
        }

        String[] headers = splitRow(rows[0]);
        var queryMeta = new TabularQueryMeta(headers);

        Debug.printList(Arrays.asList(rows));
        // Support for multiple query rows, by iterating each row by header.
        int exampleRowCount = rows.length - 2;
        var queryGraphs = new QueryGraph[exampleRowCount];
        for (int i = 0; i < exampleRowCount; i++) {
            queryGraphs[i] = parseRow(queryMeta, rows[i]);
        }

        // TODO:
        return queryGraphs[0];
    }

    public QueryGraph parseRow(TabularQueryMeta meta, String exampleRow) throws SyntaxError {
        String[] columns = splitRow(exampleRow);
        if (meta.headers.length != columns.length) {
            throw new SyntaxError("The header and example row must have same number of columns.");
        }

        var graph = new QueryGraph(meta);
        var nodeParser = new TabularNodeParser(graph);
        var edgeParser = new TabularEdgeParser(graph);

        for (int i = 0; i < meta.headers.length; i++) {
            TabularHeader header = meta.headers.get(i);
            String exampleColumn = columns[i].trim();

            if (TabularHeaderType.NODE == header.type) {
                QbeNode node = nodeParser.parse(header, exampleColumn);
                graph.put(node.name, node);
            } else {
                QbeEdge edge = edgeParser.parse(header, exampleColumn);
                graph.addEdge(edge);
            }
        }

        return graph;
    }

    private String[] splitRow(String row) {
        return row.substring(1, row.length() - 1).split("\\|");
    }
}
