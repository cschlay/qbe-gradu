package core.parsers;

import core.exceptions.SyntaxError;
import utilities.GenericComparison;

import java.util.Stack;
import java.util.function.Consumer;

public class LogicalQueryParser {
    public <T> boolean evaluate(String query, T value) {
        var tokens = query.replace(",", "").replaceAll("[()]", " ").split(" ");

        var stack = new Stack<>();

        reverseIteration(tokens, token -> {
            if (TabularTokens.Comparators.contains(token.toLowerCase())) {
                var operand = stack.pop();
                boolean result = evaluateComparator(token, value, operand);
                stack.push(result);
            } else if (TabularTokens.LogicalOperators.contains(token.toLowerCase())) {
                boolean result;
                if (TabularTokens.Not.equals(token)) {
                    result = evaluateLogicalExpression(TabularTokens.And, (boolean) stack.pop(), false);
                } else {
                    result = evaluateLogicalExpression(token, (boolean) stack.pop(), (boolean) stack.pop());
                }
                stack.push(result);
            } else {
                try {
                    stack.push(castToSameType(value, token));
                } catch (SyntaxError e) {
                    e.printStackTrace();
                }
            }
        });

        return (boolean) stack.pop();
    }

    private <T> boolean evaluateComparator(String operator, T a, Object b) {
        if (TabularTokens.GreaterThan.equals(operator)) {
            return GenericComparison.isGreaterThan(a, b);
        } else if (TabularTokens.GreaterThanOrEqual.equals(operator)) {
            return GenericComparison.isGreaterThanOrEqual(a, b);
        } else if (TabularTokens.LessThan.equals(operator)) {
            return false;
        } else if (TabularTokens.LessThanOrEqual.equals(operator)) {
            return GenericComparison.isLessThanOrEqual(a, b);
        }
        return false;
    }

    private boolean evaluateLogicalExpression(String operator, boolean a, boolean b) {
        if (TabularTokens.And.equals(operator.toLowerCase())) {
            return a && b;
        } else if (TabularTokens.Or.equals(operator.toLowerCase())) {
            return a || b;
        }
        return false;
    }

    private <T> void reverseIteration(T[] iterable, Consumer<T> onEachElement) {
        for (int i = iterable.length - 1; i >= 0; i--) {
            onEachElement.accept(iterable[i]);
        }
    }

    private <T> Object castToSameType(T example, String value) throws SyntaxError {
        if (example instanceof Integer) {
            return Integer.parseInt(value);
        }

        throw new SyntaxError(String.format("Invalid token %s", value));
    }
}
