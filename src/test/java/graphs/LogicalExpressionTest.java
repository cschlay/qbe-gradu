package graphs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static graphs.LogicalExpression.evaluate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogicalExpressionTest {
    @Nested
    @DisplayName("comparison")
    class ComparisonTest {
        @Test
        void equality() {
            assertTrue(evaluate("1", 1));
            assertTrue(evaluate("= 1", 1));

            assertTrue(evaluate("1.0", 1.0));
            assertTrue(evaluate("= 1.0", 1.0));
        }

        @Test
        void greaterThan() {
            var query = "> 1";
            assertFalse(evaluate(query, 0));
            assertFalse(evaluate(query, 1));
            assertTrue(evaluate(query, 2));
        }

        @Test
        void greaterThanOrEqual() {
            var query = ">= 1";
            assertTrue(evaluate(query, 2));
            assertTrue(evaluate(query, 1));
            assertFalse(evaluate(query, 0));
        }

        @Test
        void lessThan() {
            var query = "< 1";
            assertTrue(evaluate(query, 0));
            assertFalse(evaluate(query, 1));
            assertFalse(evaluate(query, 2));
        }

        @Test
        void lessThanOrEqualTo() {
            var query = "<= 1";
            assertTrue(evaluate(query, 0));
            assertTrue(evaluate(query, 1));
            assertFalse(evaluate(query, 2));
        }
    }

    @Nested
    @DisplayName("logical")
    class LogicalTest {
        @Test
        void and() {
            assertTrue(evaluate("AND(> -1, < 1)", 0));
            assertFalse(evaluate("AND(< -1, > 1)", 2));
            assertFalse(evaluate("AND(< -1, > 1)", -2));
            assertFalse(evaluate("AND(< -1, > 1)", 0));
        }

        @Test
        void Or() {
            assertTrue(evaluate("OR(> -1, < 1)", 0));
            assertTrue(evaluate("OR(< -1, > 1)", 2));
            assertTrue(evaluate("OR(< -1, > 1)", -2));
            assertFalse(evaluate("OR(< -1, > 1)", 0));
        }

        @Test
        void Not() {
            assertTrue(evaluate("NOT(< 1)", 2));
            assertFalse(evaluate("NOT(< 1)", 0));
        }

        @Test
        void NestedConditions() {
            var query = "AND(> 0, AND(< 5, NOT(= 3))";
            assertTrue(evaluate(query, 4));
            assertFalse(evaluate(query, 3));
        }
    }
}
