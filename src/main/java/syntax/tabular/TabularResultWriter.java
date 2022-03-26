package syntax.tabular;

import graphs.QueryGraph;
import graphs.ResultGraph;
import interfaces.ResultWriter;
import org.jetbrains.annotations.Nullable;
import utilities.Utils;

import java.util.List;

public class TabularResultWriter implements ResultWriter {


    public String write(QueryGraph queryGraph, ResultGraph resultGraph) {
        @Nullable Object meta = queryGraph.meta;
        if (meta == null) {
            throw new IllegalStateException("The code has bug with meta attribute. It should be defined by parser.");
        }

        Headers headers = ((TabularQueryMeta) meta).headers;
        int[] widths = new int[headers.length];
        List<Object[]> rows = new TabularRowFinder(headers, widths).find(resultGraph);
        return writeHeader(headers, Utils.first(rows), widths) + writeSeparator(headers, widths) + writeRows(headers, rows, widths);
    }

    private String writeHeader(Headers th, Object[] headers, int[] columnLengths) {
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

    private String writeSeparator(Headers headers, int[] columnLengths) {
        var result = new StringBuilder();
        for (int col = 0; col < columnLengths.length; col++) {
            if (headers.get(col).selected) {
                result.append(result.length() > 0 ? "+" : "|");
                String padding = "-".repeat(columnLengths[col] + 2);
                result.append(padding);
            }
        }
        result.append("|\n");
        return result.toString();
    }

    private String writeRows(Headers headers, List<Object[]> rows, int[] widths) {
        var result = new StringBuilder();
        int columnCount = Utils.first(rows).length;

        for (int i = 1; i < rows.size(); i++) {
            for (int j = 0; j < columnCount; j++) {
                if (headers.get(j).selected) {
                    String valueString = castToString(rows.get(i)[j]);
                    int padding = widths[j] - valueString.length() + 1;
                    String whitespaces = " ".repeat(Math.max(padding, 1));
                    result.append("| ").append(valueString).append(whitespaces);
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
