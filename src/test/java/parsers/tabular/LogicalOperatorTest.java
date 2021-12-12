package parsers.tabular;

import core.parsers.LogicalExpression;
import org.junit.Assert;
import org.junit.Test;

public class LogicalOperatorTest {
    @Test
    public void And() {
        var r11 = LogicalExpression.evaluate("AND(> -1, < 1)", 0);
        Assert.assertTrue(r11);

        var r01 = LogicalExpression.evaluate("AND(< -1, > 1)", 2);
        Assert.assertFalse(r01);

        var r10 = LogicalExpression.evaluate("AND(< -1, > 1)", -2);
        Assert.assertFalse(r10);

        var r00 = LogicalExpression.evaluate("AND(< -1, > 1)", 0);
        Assert.assertFalse(r00);
    }

    @Test
    public void Or() {
        var r11 = LogicalExpression.evaluate("OR(> -1, < 1)", 0);
        Assert.assertTrue(r11);

        var r01 = LogicalExpression.evaluate("OR(< -1, > 1)", 2);
        Assert.assertTrue(r01);

        var r10 = LogicalExpression.evaluate("OR(< -1, > 1)", -2);
        Assert.assertTrue(r10);

        var r00 = LogicalExpression.evaluate("OR(< -1, > 1)", 0);
        Assert.assertFalse(r00);
    }

    @Test
    public void Not() {
        var rT = LogicalExpression.evaluate("NOT(< 1)", 2);
        Assert.assertTrue(rT);

        var rF = LogicalExpression.evaluate("NOT(< 1)", 0);
        Assert.assertFalse(rF);
    }

    @Test
    public void NestedConditions() {
        var query = "AND(> 0, AND(< 5, NOT(= 3))";
        var r1 = LogicalExpression.evaluate(query, 4);
        Assert.assertTrue(r1);

        var r2 = LogicalExpression.evaluate(query, 3);
        Assert.assertFalse(r2);
    }
}
