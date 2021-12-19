package syntax.tabular;

public interface TabularColumnParser<T> {
    T parse(TabularHeader header, String exampleData);
}
