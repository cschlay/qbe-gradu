package core.graphs;

import core.graphs.LogicalExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// TODO: merge into one
class ComparisonOperatorTest {
    @Test
    void Equals() {
        var r1 = LogicalExpression.evaluate("1", 1);
        var r2 = LogicalExpression.evaluate("= 1", 1);
        Assertions.assertTrue(r1);
        Assertions.assertTrue(r2);
    }

    @Test
    void GreaterThan() {
        var query = "> 1";
        var r1 = LogicalExpression.evaluate(query, 0);
        var r2 = LogicalExpression.evaluate(query, 1);
        var r3 = LogicalExpression.evaluate(query, 2);

        Assertions.assertFalse(r1);
        Assertions.assertFalse(r2);
        Assertions.assertTrue(r3);
    }

    @Test
    void GreaterThanOrEqual() {
        var query = ">= 1";
        var r1 = LogicalExpression.evaluate(query, 2);
        var r2 = LogicalExpression.evaluate(query, 1);
        var r3 = LogicalExpression.evaluate(query, 0);

        Assertions.assertTrue(r1);
        Assertions.assertTrue(r2);
        Assertions.assertFalse(r3);
    }

    @Test
    void LessThan() {
        var query = "< 1";
        var r1 = LogicalExpression.evaluate(query, 0);
        var r2 = LogicalExpression.evaluate(query, 1);
        var r3 = LogicalExpression.evaluate(query, 2);

        Assertions.assertTrue(r1);
        Assertions.assertFalse(r2);
        Assertions.assertFalse(r3);
    }

    @Test
    void LessThanOrEqual() {
        var query = "<= 1";
        var r1 = LogicalExpression.evaluate(query, 0);
        var r2 = LogicalExpression.evaluate(query, 1);
        var r3 = LogicalExpression.evaluate(query, 2);

        Assertions.assertTrue(r1);
        Assertions.assertTrue(r2);
        Assertions.assertFalse(r3);
    }
}
