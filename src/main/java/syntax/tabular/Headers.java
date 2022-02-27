package syntax.tabular;

import exceptions.SyntaxError;
import graphs.GraphEntity;

import org.jetbrains.annotations.Nullable;

public class Headers {
    public final int length;
    private final TabularHeader[] headers;

    public Headers(String[] headers) throws SyntaxError {
        length = headers.length;
        this.headers = new TabularHeader[length];

        for (int i = 0; i < length; i++) {
            var header = new TabularHeader(headers[i]);
            this.headers[i] = header;
        }
    }

    public TabularHeader get(int index) {
        return headers[index];
    }

    @Nullable public Integer getIndex(GraphEntity entity, String property) {
        for (int i = 0; i < headers.length; i++) {
            TabularHeader header = headers[i];
            if (entity.name.equals(header.entityName) && header.name.equals(property)) {
                return i;
            }
        }

        return null;
    }

    public String getDisplayName(int index) {
        return headers[index].toString();
    }
}
