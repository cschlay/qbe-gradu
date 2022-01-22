package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.utilities.Utils;
import interfaces.QueryParser;
import org.jetbrains.annotations.Nullable;

public class TabularParser implements QueryParser {
    /**
     * Parse the query from string to QueryGraph.

     * @param query as defined in the thesis
     * @return query graphs, could have multiple if more than one row is specified
     */
    public QueryGraph parse(String query) throws SyntaxError {
        String[] rows = query.split("\n");
        if (rows.length < 3) {
            throw new SyntaxError("Query must include header and at least one example row.");
        }

        String[] headers = splitRow(rows[0]);
        var queryMeta = new TabularQueryMeta(headers);
        return parseRow(queryMeta, rows[2]);
    }

    public QueryGraph parseRow(TabularQueryMeta meta, String exampleRow) throws SyntaxError {
        String[] columns = splitRow(exampleRow);
        if (meta.headers.length != columns.length) {
            throw new SyntaxError("The header and example row must have same number of columns.");
        }

        var graph = new QueryGraph(meta);
        var nodeParser = new TabularNodeParser(graph);
        var edgeParser = new TabularEdgeParser(graph);

        @Nullable String entityName = null;
        for (int i = 0; i < meta.headers.length; i++) {
            String value = columns[i].trim();
            TabularHeader header = meta.headers.get(i);
            header.type = getColumnType(header, value);

            if (TabularHeaderType.NODE == header.type) {
                entityName = header.name;
                QbeNode node = nodeParser.parseEntity(header, value);
                graph.put(node);
            } else if (TabularHeaderType.EDGE == header.type) {
                entityName = header.name;
                QbeEdge edge = edgeParser.parseEntity(header, value);
                graph.put(edge);
            } else if (entityName == null) {
                throw new SyntaxError("The entity columns must be defined before properties e.g. | Book | title |");
            } else {
                if (header.entityName == null) {
                    header.entityName = entityName;
                }
                if (Utils.startsWithUppercase(header.entityName)) {
                    QbeNode node = nodeParser.parseProperty(header, value);
                    graph.put(node);
                } else {
                    QbeEdge edge = edgeParser.parseProperty(header, value);
                    graph.put(edge);
                }
            }
        }

        return graph;
    }

    private TabularHeaderType getColumnType(TabularHeader header, String value) {
        if (Utils.startsWithUppercase(header.name)) {
            return TabularHeaderType.NODE;
        }
        if (value.matches("(UPDATE|INSERT|DELETE|QUERY) [A-Za-z]+\\.[A-Za-z]+")) {
            return TabularHeaderType.EDGE;
        }

        return TabularHeaderType.PROPERTY;
    }

    private String[] splitRow(String row) {
        return row.substring(1, row.length() - 1).split("\\|");
    }
}
