package syntax.tabular;

import exceptions.SyntaxError;

public class TabularQueryMeta {
    public final Headers headers;

    public TabularQueryMeta(TabularHeader[] headers) {
        this.headers = new Headers(headers);
    }

    public TabularQueryMeta(String[] headers) throws SyntaxError {
        this.headers = new Headers(headers);
    }
}
