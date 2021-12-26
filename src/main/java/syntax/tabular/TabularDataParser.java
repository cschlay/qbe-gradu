package syntax.tabular;

import core.graphs.LogicalExpression;
import core.graphs.QbeData;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for values used in tabular queries.
 */
public class TabularDataParser {
    /**
     * Parse tabular value in the cell into processable object
     *
     * @param value defined in the cell
     * @return QbeData instance
     */
    public QbeData parse(String value) {
        var data = parseValue(value);
        return new QbeData(data);
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
    private @Nullable Object parseValue(String value) {
        if ("".equals(value) || "null".equals(value)) {
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
}