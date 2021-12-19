package syntax.tabular;

import core.exceptions.SyntaxError;

public class TabularQueryMeta {
    public final Headers headers;

    public TabularQueryMeta(String[] headers) throws SyntaxError {
        this.headers = new Headers(headers);
    }
}
