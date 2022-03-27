package syntax.tabular;

import graphs.GraphEntity;
import graphs.QbeEdge;
import graphs.QbeNode;
import graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;
import utilities.Utils;

import java.util.*;

/** Recursive operations for writing tabular rows. */
public class TabularRowFinder {
    private final Headers headers;
    private final int[] columnLengths;

    public TabularRowFinder(Headers headers, int[] columnLengths) {
        this.headers = headers;
        this.columnLengths = columnLengths;
    }

    /** Returns a list of rows, there may exist multiple ones because of relations. */
    public List<Object[]> find(ResultGraph graph) {
        var hashes = new HashSet<Integer>();
        var rows = new ArrayList<Object[]>();

        // Add the headers.
        var headerRow = new Object[headers.length];
        for (int col = 0; col < headers.length; col++) {
            String headerName = headers.getDisplayName(col);
            headerRow[col] = headerName;
            columnLengths[col] = headerName.length();
        }
        rows.add(headerRow);

        for (var node : graph.values()) {
            if (!node.visited && node.selected) {
                List<Object[]> rowSet = writeNode(new Object[headers.length], node);

                for (Object[] row : rowSet) {
                    int hash = Arrays.hashCode(row);
                    if (!Utils.isNullArray(row) && !hashes.contains(hash)) {
                        hashes.add(hash);
                        rows.add(row);
                    }
                }
            }
        }

        return rows;
    }

    private List<Object[]> writeEdge(Object[] template, QbeEdge edge) {
        edge.visited = true;
        writeProperties(template, edge);

        var rows = new ArrayList<Object[]>();
        if (edge.tailNode != null && !edge.tailNode.visited) {
            rows.addAll(writeNode(template.clone(), edge.tailNode));
        }
        if (edge.headNode != null && !edge.headNode.visited) {
            rows.addAll(writeNode(template.clone(), edge.headNode));
        }

        if (rows.isEmpty()) {
            rows.add(template);
        }

        return rows;
    }

    private List<Object[]> writeNode(Object[] template, QbeNode node) {
        node.visited = true;

        var rows = new ArrayList<Object[]>();
        writeProperties(template, node);
        for (QbeEdge edge : node.edges.values()) {
            if (edge.tailNode == node && !edge.visited) {
                // Only traverse correct directions to avoid duplicate rows.
                rows.addAll(writeEdge(template.clone(), edge));
            }
        }

        if (rows.isEmpty()) {
            rows.add(template);
        }

        return rows;
    }

    private void writeProperties(Object[] template, GraphEntity entity) {
        for (var property : entity.properties.entrySet()) {
            String propertyName = property.getKey();
            @Nullable Integer columnIndex = headers.getIndex(entity, propertyName);

            if (columnIndex != null) {
                @Nullable Object value = property.getValue().value;
                // The values will be null if they are not selected to ensure accuracy of null check.
                template[columnIndex] = headers.get(columnIndex).selected ? value : null;
                columnLengths[columnIndex] = getColumnLength(columnLengths, columnIndex, value);
            }
        }
    }

    private int getColumnLength(int[] columnLengths, int index, @Nullable Object value) {
        if (value == null) {
            return Math.max(4, columnLengths[index]);
        }

        int length = value instanceof String ? ((String) value).length() + 2 : value.toString().length();
        return Math.max(length, columnLengths[index]);
    }
}
