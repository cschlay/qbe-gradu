package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;

public class TabularParser {
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

        return parse(headers, exampleRow);
    }

    public QueryGraph parse(String[] headers, String[] exampleRow) throws SyntaxError {
        if (headers.length != exampleRow.length) {
            throw new SyntaxError("The header and example row must have same number of columns.");
        }

        var graph = new QueryGraph();
        var nodeParser = new TabularNodeParser(graph);

        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            String exampleColumn = exampleRow[i].trim();

            // Headers that start with uppercase are nodes and lowercase are edges.
            if (isNode(header)) {
                QbeNode node = nodeParser.parse(header, exampleColumn);
                graph.put(node.name, node);
            }
        }

        return graph;
    }

    private boolean isNode(String value) {
        return Character.isUpperCase(value.charAt(0));
    }

    private String[] parseRow(String row) {
        return row.substring(1, row.length() - 1).split("\\|");
    }
}
