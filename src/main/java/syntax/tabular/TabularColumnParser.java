package syntax.tabular;


import core.exceptions.SyntaxError;

public interface TabularColumnParser<T> {
    T parseProperty(TabularHeader header, String value);
    T parseEntity(TabularHeader header, String value) throws SyntaxError;
}
