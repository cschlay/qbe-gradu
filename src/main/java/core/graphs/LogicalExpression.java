package core.graphs;

import core.exceptions.SyntaxError;
import syntax.tabular.TabularTokens;
import core.utilities.Comparison;

import java.util.ArrayDeque;
import java.util.function.Consumer;

/**
 * Represents a logical expression parsed from tabular query.
 */
public class LogicalExpression {
    public final String expression;

    public LogicalExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Evaluates a value against the expression.
     *
     * @param value to evaluate
     * @param <T> the type of value
     * @return true if the expression returns true with the value
     */
    public <T> boolean evaluate(T value) {
        return evaluate(expression, value);
    }

    /**
     * Evaluates value against any arbitrary query.
     * Prefer using .evaluate(T value).
     *
     * @param expression to use
     * @param value to evaluate
     * @param <T> type of value
     * @return true if the expression returns true with the value
     */
    public static <T> boolean evaluate(String expression, T value) {
        var tokens = expression.replace(",", "").replaceAll("[()]", " ").split(" ");

        var stack = new ArrayDeque<>(); // Deque is an implementation of Stack

        reverseIteration(tokens, token -> {
            var lowerCaseToken = token.toLowerCase();

            if (TabularTokens.Comparators.contains(lowerCaseToken)) {
                boolean result = evaluateComparator(token, value, stack.pop());
                stack.push(result);
            } else if (TabularTokens.LogicalOperators.contains(lowerCaseToken)) {
                if (TabularTokens.Not.equals(lowerCaseToken)) {
                    stack.push(!(boolean) stack.pop());
                } else {
                    var result = evaluateLogicalExpression(token, (boolean) stack.pop(), (boolean) stack.pop());
                    stack.push(result);
                }
            } else {
                try {
                    stack.push(castToSameType(value, token));
                } catch (SyntaxError e) {
                    e.printStackTrace();
                }
            }
        });

        var result = stack.pop();
        if (result instanceof Boolean) {
            return (boolean) result;
        }

        return result.equals(value);
    }

    public String toString() {
        return String.format("LogicalExpression(%s)", expression);
    }

    private static <T> boolean evaluateComparator(String operator, T a, Object b) {
        if (TabularTokens.GreaterThan.equals(operator)) {
            return Comparison.greaterThan(a, b);
        } else if (TabularTokens.GreaterThanOrEqual.equals(operator)) {
            return Comparison.greaterThanOrEqualTo(a, b);
        } else if (TabularTokens.LessThan.equals(operator)) {
            return Comparison.lessThan(a, b);
        } else if (TabularTokens.LessThanOrEqual.equals(operator)) {
            return Comparison.lessThanOrEqualTo(a, b);
        } else if (operator.equals(TabularTokens.Equality)) {
            return a.equals(b);
        }
        return false;
    }

    private static boolean evaluateLogicalExpression(String operator, boolean a, boolean b) {
        if (TabularTokens.And.equals(operator.toLowerCase())) {
            return a && b;
        } else if (TabularTokens.Or.equals(operator.toLowerCase())) {
            return a || b;
        }
        return false;
    }

    private static <T> void reverseIteration(T[] iterable, Consumer<T> onEachElement) {
        for (int i = iterable.length - 1; i >= 0; i--) {
            onEachElement.accept(iterable[i]);
        }
    }

    private static  <T> Object castToSameType(T example, String value) throws SyntaxError {
        if (example instanceof Integer) {
            return Integer.parseInt(value);
        }
        if (example instanceof Double) {
            return Double.parseDouble(value);
        }

        throw new SyntaxError(String.format("Invalid token %s", value));
    }
}
