package syntax.tabular;

import core.graphs.GraphEntity;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.ResultGraph;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Recursive operations for writing tabular rows. */
public class TabularRowWriter {
    private final Headers headers;
    private final int[] columnLengths;

    public TabularRowWriter(Headers headers, int[] columnLengths) {
        this.headers = headers;
        this.columnLengths = columnLengths;
    }

    /** Returns a list of rows, there may exist multiple ones because of relations. */
    public List<Object[]> getRows(ResultGraph graph) {
        var rows = new ArrayList<Object[]>();

        // Add the headers.
        var headerRow = new Object[headers.length];
        for (int col = 0; col < headers.length; col++) {
            var headerName = headers.getDisplayName(col);
            headerRow[col] = headerName;
            columnLengths[col] = headerName.length();
        }
        rows.add(headerRow);

        for (var node : graph.values()) {
            if (!node.visited && node.selected) {
                var rowSet = writeNode(new Object[headers.length], node);
                rows.addAll(rowSet);
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
            rows.addAll(writeEdge(template.clone(), edge));
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
                template[columnIndex] = value;
                columnLengths[columnIndex] = getColumnLength(columnLengths, columnIndex, value);
            }
        }
    }

    private int getColumnLength(int[] columnLengths, int index, Object value) {
        int length = value instanceof String ? ((String) value).length() + 2 : value.toString().length();
        return Math.max(length, columnLengths[index]);
    }
}
