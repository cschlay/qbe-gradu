package parsers;

import core.parsers.NumericQueryEvaluator;
import org.junit.Assert;
import org.junit.Test;

public class LogicalOperatorTest {
    @Test
    public void And() {
        var r11 = NumericQueryEvaluator.evaluate("AND(> -1, < 1)", 0);
        Assert.assertTrue(r11);

        var r01 = NumericQueryEvaluator.evaluate("AND(< -1, > 1)", 2);
        Assert.assertFalse(r01);

        var r10 = NumericQueryEvaluator.evaluate("AND(< -1, > 1)", -2);
        Assert.assertFalse(r10);

        var r00 = NumericQueryEvaluator.evaluate("AND(< -1, > 1)", 0);
        Assert.assertFalse(r00);
    }

    @Test
    public void Or() {
        var r11 = NumericQueryEvaluator.evaluate("OR(> -1, < 1)", 0);
        Assert.assertTrue(r11);

        var r01 = NumericQueryEvaluator.evaluate("OR(< -1, > 1)", 2);
        Assert.assertTrue(r01);

        var r10 = NumericQueryEvaluator.evaluate("OR(< -1, > 1)", -2);
        Assert.assertTrue(r10);

        var r00 = NumericQueryEvaluator.evaluate("OR(< -1, > 1)", 0);
        Assert.assertFalse(r00);
    }

    @Test
    public void Not() {
        var rT = NumericQueryEvaluator.evaluate("NOT(< 1)", 2);
        Assert.assertTrue(rT);

        var rF = NumericQueryEvaluator.evaluate("NOT(< 1)", 0);
        Assert.assertFalse(rF);
    }

    @Test
    public void NestedConditions() {
        var query = "AND(> 0, AND(< 5, NOT(= 3))";
        var r1 = NumericQueryEvaluator.evaluate(query, 4);
        Assert.assertTrue(r1);

        var r2 = NumericQueryEvaluator.evaluate(query, 3);
        Assert.assertFalse(r2);
    }
}
