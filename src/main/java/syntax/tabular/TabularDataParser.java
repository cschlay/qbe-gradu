package syntax.tabular;

import enums.QueryType;
import graphs.LogicalExpression;
import graphs.QbeData;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for values used in tabular queries.
 */
public class TabularDataParser {
    public static class Token {
        public QueryType type;
        /** Operation argument such as update value. */
        public @Nullable String argument;
        public String queryValue;
    }

    /**
     * Parse tabular value in the cell into processable object
     *
     * @param value defined in the cell
     * @return QbeData instance
     */
    public QbeData parse(String value) {
        Token token = tokenize(value);

        var instance = new QbeData(parseValue(token.queryValue));
        instance.type = token.type;
        instance.operationArgument = token.argument;

        return instance;
    }

    /**
     * Tokenizes a value by separating query values, operations, and its arguments.
     *
     * @param value to tokenize
     * @return the token
     */
    public Token tokenize(String value) {
        var token = new Token();
        if (value.startsWith(TabularTokens.DELETE)) {
            token.type = QueryType.DELETE;
            token.queryValue = value.replaceFirst(TabularTokens.DELETE, "").trim();
        } else if (value.startsWith(TabularTokens.UPDATE)) {
            token.type = QueryType.UPDATE;
            token.queryValue = "";
            token.argument = value.replaceFirst(TabularTokens.UPDATE, "").trim();
            // Can be extended to parse "UPDATE x TO y" syntax. In that case token.value = x and token.update = y
        } else if (value.startsWith(TabularTokens.SUM)) {
            return tokenizeSum(value);
        } else {
            token.queryValue = value;
        }
        return token;
    }

    /**
     * Parse values into Java object.
     * The conversions rules are:
     * - empty values to null
     * - null to null
     * - integers to int
     * - decimal numbers to double
     * - false and true to boolean
     * - values between quotation marks to String
     * - logical expression to LogicalExpression
     *
     * @param value to parse
     * @return Java equivalent of the value
     */
    private @Nullable Object parseValue(@Nullable String value) {
        if (value == null || "".equals(value) || "null".equals(value)) {
            return null;
        }

        if ("false".equals(value) || "true".equals(value)) {
            return Boolean.parseBoolean(value);
        } else if (value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
            return value.substring(1, value.length()-1);
        }

        try {
            return parseNumeric(value);
        } catch (NumberFormatException exception) {
            return new LogicalExpression(value);
        }
    }

    /**
     * Parse string representation of a numeric value in to Java object.
     * Decimals are always double and integers are int.
     *
     * @param value string representation of numeric value
     * @return numeric instance of the value
     */
    private Object parseNumeric(String value) {
        if (value.contains(".")) {
            return Double.parseDouble(value);
        }
        return Integer.parseInt(value);
    }

    private Token tokenizeSum(String value) {
        // "SUM entity [value]" is safe to split.
        String[] parts = value.split(" ", 3);

        var token = new Token();
        token.type = QueryType.SUM;
        if (!parts[1].equals(TabularTokens.NO_NAME)) {
            token.argument = parts[1];
        }
        token.queryValue = parts.length == 3 ? parts[2] : "";

        return token;
    }
}
