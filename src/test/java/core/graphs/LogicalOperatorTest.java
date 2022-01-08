package core.graphs;

import core.graphs.LogicalExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// TODO: merge into one
public class LogicalOperatorTest {
    @Test
    public void And() {
        var r11 = LogicalExpression.evaluate("AND(> -1, < 1)", 0);
        Assertions.assertTrue(r11);

        var r01 = LogicalExpression.evaluate("AND(< -1, > 1)", 2);
        Assertions.assertFalse(r01);

        var r10 = LogicalExpression.evaluate("AND(< -1, > 1)", -2);
        Assertions.assertFalse(r10);

        var r00 = LogicalExpression.evaluate("AND(< -1, > 1)", 0);
        Assertions.assertFalse(r00);
    }

    @Test
    public void Or() {
        var r11 = LogicalExpression.evaluate("OR(> -1, < 1)", 0);
        Assertions.assertTrue(r11);

        var r01 = LogicalExpression.evaluate("OR(< -1, > 1)", 2);
        Assertions.assertTrue(r01);

        var r10 = LogicalExpression.evaluate("OR(< -1, > 1)", -2);
        Assertions.assertTrue(r10);

        var r00 = LogicalExpression.evaluate("OR(< -1, > 1)", 0);
        Assertions.assertFalse(r00);
    }

    @Test
    public void Not() {
        var rT = LogicalExpression.evaluate("NOT(< 1)", 2);
        Assertions.assertTrue(rT);

        var rF = LogicalExpression.evaluate("NOT(< 1)", 0);
        Assertions.assertFalse(rF);
    }

    @Test
    public void NestedConditions() {
        var query = "AND(> 0, AND(< 5, NOT(= 3))";
        var r1 = LogicalExpression.evaluate(query, 4);
        Assertions.assertTrue(r1);

        var r2 = LogicalExpression.evaluate(query, 3);
        Assertions.assertFalse(r2);
    }
}
