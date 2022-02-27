package syntax.tabular;

import exceptions.SyntaxError;
import enums.QueryType;

import java.util.Arrays;
import java.util.List;

public class TabularTokens {
    private TabularTokens() {}

    public static String Equality = "=";
    public static String GreaterThan = ">";
    public static String GreaterThanOrEqual = ">=";
    public static String LessThan = "<";
    public static String LessThanOrEqual = "<=";

    public static final String NO_NAME = "_";
    public static final String COUNT = "COUNT";
    public static final String SUM = "SUM";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";

    public static String And = "and";
    public static String Or = "or";
    public static String Not = "not";

    public static List<String> Comparators = Arrays.asList(
            Equality, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual);
    public static List<String> LogicalOperators = Arrays.asList(
            And, Or, Not);

    public static QueryType getQueryType(String type) throws SyntaxError {
        try {
            return QueryType.valueOf(type);
        } catch (IllegalArgumentException expected) {
            String message = "Query type '%s' is not supported. Use one of QUERY, COUNT, SUM, DELETE, INSERT or UPDATE";
            throw new SyntaxError(message, type);
        }
    }
}
