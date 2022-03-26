package syntax.tabular;

import exceptions.SyntaxError;
import enums.QueryType;
import utilities.Utils;

import java.util.Arrays;
import java.util.List;

public class TabularTokens {
    private TabularTokens() {}

    public static final String EQUALITY = "=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUAL = "<=";

    public static final String ALIAS = "AS";
    public static final String NO_NAME = "_";
    public static final String COUNT = "COUNT";
    public static final String SUM = "SUM";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";

    public static final String AND = "and";
    public static final String OR = "or";
    public static final String NOT = "not";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final char STRING = '"';
    public static final char REGEX = '/';


    protected static final List<String> COMPARATORS = Arrays.asList(
            EQUALITY, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL);
    protected static final List<String> LOGICAL_OPERATORS = Arrays.asList(
            AND, OR, NOT);

    public static QueryType getQueryType(String type) throws SyntaxError {
        try {
            return QueryType.valueOf(type);
        } catch (IllegalArgumentException expected) {
            String message = "Query type '%s' is not supported. Use one of QUERY, COUNT, SUM, DELETE, INSERT or UPDATE";
            throw new SyntaxError(message, type);
        }
    }

    public static boolean isBoolean(String token) {
        return FALSE.equals(token) || TRUE.equals(token);
    }

    public static boolean isComparator(String token) {
        return COMPARATORS.contains(token);
    }

    public static boolean isLogicalOperator(String token) {
        return LOGICAL_OPERATORS.contains(token);
    }

    public static boolean isRegularExpression(String value) {
        return Utils.isEnclosedWith(value, TabularTokens.REGEX);
    }

    public static boolean isString(String value) {
        return Utils.isEnclosedWith(value, TabularTokens.STRING);
    }
}
