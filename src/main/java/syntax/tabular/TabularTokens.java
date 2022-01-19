package syntax.tabular;

import core.exceptions.SyntaxError;
import core.graphs.QueryType;

import java.util.Arrays;
import java.util.List;

public class TabularTokens {
    public static String Equality = "=";
    public static String GreaterThan = ">";
    public static String GreaterThanOrEqual = ">=";
    public static String LessThan = "<";
    public static String LessThanOrEqual = "<=";

    public static String And = "and";
    public static String Or = "or";
    public static String Not = "not";

    public static List<String> Comparators = Arrays.asList(
            Equality, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual);
    public static List<String> LogicalOperators = Arrays.asList(
            And, Or, Not);

    public static QueryType getQueryType(String queryType) throws SyntaxError  {
        switch (queryType) {
            case "QUERY":
                return QueryType.QUERY;
            case "DELETE":
                return QueryType.DELETE;
            case "INSERT":
                return QueryType.CREATE;
            case "UPDATE":
                return QueryType.UPDATE;
            default:
                String message = "Query type '%s' is not supported. Use one of 'QUERY', 'DELETE', 'INSERT' or 'UPDATE'";
                throw new SyntaxError(message, queryType);
        }
    }
}
