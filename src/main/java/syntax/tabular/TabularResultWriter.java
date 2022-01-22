package syntax.tabular;

import core.graphs.QueryGraph;
import core.graphs.ResultGraph;
import interfaces.ResultWriter;
import org.jetbrains.annotations.Nullable;

public class TabularResultWriter implements ResultWriter {
    public String write(QueryGraph queryGraph, ResultGraph resultGraph) {
        var meta = (TabularQueryMeta) queryGraph.meta;
        int[] columnLengths = new int[meta.headers.length];
        var table = toTable(meta.headers, resultGraph, columnLengths);

        return getHeaderRowAsString(meta.headers, table[0], columnLengths)
                + getHeaderSeparator(meta.headers, columnLengths)
                + getRowsAsString(meta.headers, table, columnLengths);
    }

    public Object[][] writeNative(QueryGraph queryGraph, ResultGraph resultGraph) {
        var meta = (TabularQueryMeta) queryGraph.meta;

        return toTable(meta.headers, resultGraph, new int[meta.headers.length]);
    }

    /**
     * Writes the graph into tabular result including the header.
     *
     * @return table of property values
     */
    public Object[][] toTable(Headers headers, ResultGraph graph, int[] columnLengths) {
        var rowWriter = new TabularRowWriter(headers, columnLengths);
        var rows = rowWriter.getRows(graph);

        // TODO: The rows need some kind of post processing so that duplicates don't exist and fields that are null do not get returned

        var result = new Object[rows.size()][headers.length];

        for (int i = 0; i < rows.size(); i++) {
            result[i] = rows.get(i);
        }

        return result;
    }

    private String getHeaderRowAsString(Headers th, Object[] headers, int[] columnLengths) {
        var result = new StringBuilder();
        for (int col = 0; col < headers.length; col++) {
            TabularHeader header = th.get(col);
            if (header.selected) {
                String padding = " ".repeat(columnLengths[col] - header.displayName.length() + 1);
                result.append("| ").append(header.displayName).append(padding);
            }
        }
        result.append("|\n");
        return result.toString();
    }

    private String getHeaderSeparator(Headers headers, int[] columnLengths) {
        var result = new StringBuilder("|");
        for (int col = 0; col < columnLengths.length; col++) {
            if (headers.get(col).selected) {
                String padding = "-".repeat(columnLengths[col] + 2);
                result.append(padding);
                if (col < columnLengths.length - 1) {
                    result.append("+");
                }
            }
        }
        result.append("|\n");
        return result.toString();
    }

    private String getRowsAsString(Headers headers, Object[][] table, int[] columnLengths) {
        var result = new StringBuilder();
        int columnCount = table[0].length;
        int rowCount = table.length;

        for (int row = 1; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                if (headers.get(col).selected) {
                    String valueString = castToString(table[row][col]);
                    int p = columnLengths[col] - valueString.length() + 1;

                    String padding = " ".repeat(Math.max(p, 1));
                    result.append("| ").append(valueString).append(padding);
                }
            }
            result.append("|\n");
        }

        return result.toString();
    }

    /**
     * Converts a value to its String representation.
     *
     * @param value instance
     * @return the string representation of the value, nulls are returned as null string
     */
    private String castToString(@Nullable Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        return value.toString();
    }
}
