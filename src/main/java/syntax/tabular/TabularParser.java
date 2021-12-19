package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import interfaces.QueryParser;

public class TabularParser implements QueryParser {
    /**
     * Parse the query from string to QueryGraph.

     * @param query as defined in the thesis
     * @return graph as described in query
     */
    public QueryGraph parse(String query) throws SyntaxError {
        var lines = query.split("\n");
        if (lines.length != 3) {
            throw new SyntaxError("Query must include header and one example row.");
        }

        // Multiple example rows could be supported by creating multiple query graphs and joining them using union.
        String[] headers = parseRow(lines[0]);
        String[] exampleRow = parseRow(lines[2]);

        var meta = new TabularQueryMeta(headers);
        var graph= parse(meta, exampleRow);

        return graph;
    }

    public QueryGraph parse(TabularQueryMeta meta, String[] exampleRow) throws SyntaxError {
        if (meta.headers.length != exampleRow.length) {
            throw new SyntaxError("The header and example row must have same number of columns.");
        }

        var graph = new QueryGraph(meta);
        var nodeParser = new TabularNodeParser(graph);
        var edgeParser = new TabularEdgeParser(graph);

        for (int i = 0; i < meta.headers.length; i++) {
            TabularHeader header = meta.headers.get(i);
            String exampleColumn = exampleRow[i].trim();

            if (header.isNode()) {
                QbeNode node = nodeParser.parse(header, exampleColumn);
                graph.put(node.name, node);
            } else {
                QbeEdge edge = edgeParser.parse(header, exampleColumn);
                graph.addEdge(edge);
            }
        }

        return graph;
    }

    private String[] parseRow(String row) {
        return row.substring(1, row.length() - 1).split("\\|");
    }
}
