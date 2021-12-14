package core.results;

import core.graphs.QbeData;
import core.graphs.ResultGraph;
import core.parsers.TabularHeader;
import utilities.Debug;

import java.util.ArrayList;
import java.util.Arrays;
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
        var result = new String[graph.size()][headers.length];

        int i = 0;
        for (var node : graph.values()) {

            for (var property : node.properties.entrySet()) {
                String propertyName = property.getKey();
                int columnIndex = headerIndices.get(node.name).get(propertyName);

                QbeData data = property.getValue();
                String value = data.value.toString();
                result[i][columnIndex] = value;

                if (value.length() > columnLengths[columnIndex]) {
                    columnLengths[columnIndex] = value.length();
                }
            }

            i++;
        }
        return result;
    }

    public String toString() {
        String[][] table = toTable();
        var result = new StringBuilder();


        System.out.println(table.length);
        for (int row = 0; row < table.length; row++) {
            for (int col = 0; col < headers.length; col++) {
                String padding = " ".repeat(columnLengths[col] - table[row][col].length());
                System.out.println(columnLengths[col] - table[row][col].length());
            }

        }
        // Add rows

        return result.toString();
    }
}
