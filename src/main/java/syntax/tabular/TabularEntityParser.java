package syntax.tabular;

import enums.QueryType;
import graphs.GraphEntity;
import graphs.QbeData;
import graphs.QbeEdge;

public class TabularEntityParser {
    private static final TabularDataParser dataParser = new TabularDataParser();

    public void parseCountAggregation(TabularHeader header, GraphEntity entity, String[] tokens)  {
        header.name = "_agg-count";
        header.selected = true;
        header.displayName = String.format("%s.count", entity.name);
        header.entityName = entity.name;

        if (entity instanceof QbeEdge) {
            if (tokens.length >= 4) {
                // ["COUNT", "Node", "Node", "AggregationGroup"]
                entity.aggregationGroup = tokens[3];
                header.entityName = entity.aggregationGroup;
            }
            if (tokens.length == 6) {
                // ["COUNT", "Node", "Node", "AggregationGroup", "AS", "alias"]
                header.displayName = tokens[5];
            }
        }

        if (tokens.length == 3) {
            // ["COUNT", "AS", "alias"]
            header.displayName = tokens[tokens.length - 1];
        }
    }

    public QbeData parseData(TabularHeader header, GraphEntity entity, String value) {
        QbeData data = dataParser.parse(value);

        if ("id".equals(header.name)) {
            entity.id = value;
        } else if (data.type == QueryType.SUM) {
            entity.type = data.type;
            if (data.operationArgument != null) {
                entity.aggregationGroup = (String) data.operationArgument;
                header.entityName = entity.aggregationGroup;
            }
            entity.aggregationProperty = header.name;
        }

        return data;
    }
}
