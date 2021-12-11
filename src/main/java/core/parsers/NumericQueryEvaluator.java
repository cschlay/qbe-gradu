package core.parsers;

import core.exceptions.SyntaxError;
import utilities.GenericComparison;

import java.util.Stack;
import java.util.function.Consumer;

public class NumericQueryEvaluator {
    public static <T> boolean evaluate(String query, T value) {
        var tokens = query.replace(",", "").replaceAll("[()]", " ").split(" ");

        var stack = new Stack<>();

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

    private static <T> boolean evaluateComparator(String operator, T a, Object b) {
        if (TabularTokens.GreaterThan.equals(operator)) {
            return GenericComparison.isGreaterThan(a, b);
        } else if (TabularTokens.GreaterThanOrEqual.equals(operator)) {
            return GenericComparison.isGreaterThanOrEqual(a, b);
        } else if (TabularTokens.LessThan.equals(operator)) {
            return GenericComparison.isLessThan(a, b);
        } else if (TabularTokens.LessThanOrEqual.equals(operator)) {
            return GenericComparison.isLessThanOrEqual(a, b);
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

        throw new SyntaxError(String.format("Invalid token %s", value));
    }
}
