package interfaces;


import exceptions.SyntaxError;
import syntax.tabular.TabularHeader;

public interface TabularColumnParser<T> {
    T parseProperty(TabularHeader header, String value);
    T parseEntity(TabularHeader header, String value) throws SyntaxError;
}
