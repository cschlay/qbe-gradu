package syntax.tabular;

import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;

public class TabularResultWriter {
    private final ResultGraph graph;
    private final Headers headers;
    private final int[] columnLengths;

    public TabularResultWriter(String[] headers, ResultGraph graph) {
        this.columnLengths = new int[headers.length];
        this.graph = graph;
        this.headers = new Headers(headers);
    }

    /**
     * Writes the graph into tabular result including the header.
     *
     * @return table of property values
     */
    public Object[][] toTable() {
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
                @Nullable Integer columnIndex = headers.get(node, propertyName);

                if (columnIndex != null) {
                    Object value = property.getValue().value;
                    result[rowIndex][columnIndex] = value;
                    updateColumnLengths(columnIndex, value);
                }
            }
            rowIndex++;
        }

        return result;
    }

    public String toString() {
        Object[][] table = toTable();
        return getHeaderRowAsString(table[0]) + getHeaderSeparator() + getRowsAsString(table);
    }

    private String getHeaderRowAsString(Object[] headers) {
        var result = new StringBuilder();
        for (int col = 0; col < headers.length; col++) {
            String header = (String) headers[col];
            String padding = " ".repeat(columnLengths[col] - header.length() + 1);
            result.append("| ").append(header).append(padding);
        }
        result.append("|\n");
        return result.toString();
    }

    private String getHeaderSeparator() {
        var result = new StringBuilder("|");
        for (int col = 0; col < headers.length; col++) {
            String padding = "-".repeat(columnLengths[col] + 2);
            result.append(padding);
            if (col < headers.length -1) {
                result.append("+");
            }
        }
        result.append("|\n");
        return result.toString();
    }

    private String getRowsAsString(Object[][] table) {
        var result = new StringBuilder();
        for (int row = 1; row < table.length; row++) {
            for (int col = 0; col < headers.length; col++) {
                Object value = table[row][col];
                String valueString = value instanceof String ? '"' + (String) value + '"' : value.toString();
                String padding = " ".repeat(columnLengths[col] - valueString.length()+ 1);
                result.append("| ").append(valueString).append(padding);
            }
            result.append("|\n");
        }

        return result.toString();
    }

    private void updateColumnLengths(int index, Object value) {
        int length = value instanceof String ? ((String) value).length() + 2 : value.toString().length();
        if (length > columnLengths[index]) {
            columnLengths[index] = length;
        }
    }
}
