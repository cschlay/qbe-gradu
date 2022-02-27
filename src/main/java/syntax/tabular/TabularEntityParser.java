package syntax.tabular;

import enums.QueryType;
import graphs.GraphEntity;
import graphs.QbeData;

public class TabularEntityParser {
    private static final TabularDataParser dataParser = new TabularDataParser();

    protected QbeData parseData(TabularHeader header, GraphEntity entity, String value) {
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
