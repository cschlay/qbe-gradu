package core.parsers;

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
}
