package syntax.tabular;

import graphs.GraphEntity;

import org.jetbrains.annotations.Nullable;


/**
 * Container class for headers, provides simplified access to headers
 */
public class Headers {
    public final int length;
    private final TabularHeader[] headerList;

    public Headers(String[] headers) {
        headerList = new TabularHeader[headers.length];
        length = headers.length;

        for (int i = 0; i < length; i++) {
            var header = new TabularHeader(headers[i]);
            headerList[i] = header;
        }
    }

    public TabularHeader get(int index) {
        return headerList[index];
    }

    public String getDisplayName(int index) {
        return headerList[index].toString();
    }

    public @Nullable Integer getIndex(GraphEntity entity, String property) {
        for (int i = 0; i < headerList.length; i++) {
            TabularHeader header = headerList[i];
            if (entity.name.equals(header.entityName) && header.name.equals(property)) {
                return i;
            }
        }

        return null;
    }
}
