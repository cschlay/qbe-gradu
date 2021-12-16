package core.results;

import core.graphs.QbeData;
import core.graphs.ResultGraph;
import core.parsers.TabularHeader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class TabularResultWriter {
    private final ResultGraph graph;
    private final TabularHeader[] headers;
    private final HashMap<String, HashMap<String, Integer>> headerIndices;
    private final int[] columnLengths;

    public TabularResultWriter(String[] headers, ResultGraph graph) {
        this.columnLengths = new int[headers.length];
        this.graph = graph;

        this.headers = new TabularHeader[headers.length];
        headerIndices = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            var header = new TabularHeader(headers[i]);
            this.headers[i] = header;

            var headerIndex = headerIndices.computeIfAbsent(header.name, key -> new HashMap<>());
            headerIndex.put(header.propertyName, i);
        }
    }

    public String[][] toTable() {
        var result = new String[graph.size() + 1][headers.length];

        //
        for (int i = 0; i < headers.length; i++) {
            result[0][i] = headers[i].toString();
            columnLengths[i] = headers[i].toString().length();
        }

        int i = 1;
        for (var node : graph.values()) {
            for (var property : node.properties.entrySet()) {
                String propertyName = property.getKey();

                var headerIndex = headerIndices.get(node.name);
                if (headerIndex != null) {
                    @Nullable Integer columnIndex = headerIndex.get(propertyName);

                    if (columnIndex != null) {
                        QbeData data = property.getValue();
                        String value = data.value.toString();
                        result[i][columnIndex] = value;

                        if (value.length() > columnLengths[columnIndex]) {
                            columnLengths[columnIndex] = value.length();
                        }
                    }
                }
            }

            i++;
        }
        return result;
    }

    public String toString() {
        String[][] table = toTable();
        var result = new StringBuilder();

        for (int col = 0; col < headers.length; col++) {
            result.append("| ");
            String padding = " ".repeat(columnLengths[col] - table[0][col].length()+1);
            result.append(table[0][col]).append(padding);
        }
        result.append("|\n");

        result.append("|");
        for (int col = 0; col < headers.length; col++) {
            String padding = "-".repeat(columnLengths[col] + 2);
            result.append(padding);
            if (col < headers.length -1) {
                result.append("+");
            }
        }
        result.append("|\n");

        for (int row = 1; row < table.length; row++) {
            for (int col = 0; col < headers.length; col++) {
                String padding = " ".repeat(columnLengths[col] - table[row][col].length()+ 1);
                result.append("| ").append(table[row][col]).append(padding);
            }
            result.append("|\n");
        }

        return result.toString();
    }
}
