package syntax.tabular;

public class TabularQueryMeta {
    public final Headers headers;

    public TabularQueryMeta(String[] headers) {
        this.headers = new Headers(headers);
    }
}
