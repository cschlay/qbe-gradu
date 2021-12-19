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

        return getHeaderRowAsString(table[0], columnLengths)
                + getHeaderSeparator(columnLengths)
                + getRowsAsString(table, columnLengths);
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
        int rowCount = graph.size() + 1;
        int colCount = headers.length;
        var result = new Object[rowCount][colCount];

        // Add the headers.
        for (int col = 0; col < headers.length; col++) {
            var headerName = headers.getDisplayName(col);
            result[0][col] = headerName;
            columnLengths[col] = headerName.length();
        }

        // Add rows
        int rowIndex = 1;
        for (var node : graph.values()) {
            for (var property : node.properties.entrySet()) {
                String propertyName = property.getKey();
                @Nullable Integer columnIndex = headers.getIndex(node, propertyName);

                if (columnIndex != null) {
                    Object value = property.getValue().value;
                    result[rowIndex][columnIndex] = value;
                    columnLengths[columnIndex] = getColumnLength(columnLengths, columnIndex, value);
                }
            }
            rowIndex++;
        }

        return result;
    }

    private String getHeaderRowAsString(Object[] headers, int[] columnLengths) {
        var result = new StringBuilder();
        for (int col = 0; col < headers.length; col++) {
            String header = (String) headers[col];
            String padding = " ".repeat(columnLengths[col] - header.length() + 1);
            result.append("| ").append(header).append(padding);
        }
        result.append("|\n");
        return result.toString();
    }

    private String getHeaderSeparator(int[] columnLengths) {
        var result = new StringBuilder("|");
        for (int col = 0; col < columnLengths.length; col++) {
            String padding = "-".repeat(columnLengths[col] + 2);
            result.append(padding);
            if (col < columnLengths.length - 1) {
                result.append("+");
            }
        }
        result.append("|\n");
        return result.toString();
    }

    private String getRowsAsString(Object[][] table, int[] columnLengths) {
        var result = new StringBuilder();
        int columnCount = table[0].length;
        int rowCount = table.length;

        for (int row = 1; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                String valueString = castToString(table[row][col]);
                String padding = " ".repeat(columnLengths[col] - valueString.length() + 1);
                result.append("| ").append(valueString).append(padding);
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

    private int getColumnLength(int[] columnLengths, int index, Object value) {
        int length = value instanceof String ? ((String) value).length() + 2 : value.toString().length();
        return Math.max(length, columnLengths[index]);
    }
}
