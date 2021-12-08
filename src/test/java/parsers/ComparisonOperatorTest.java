package parsers;

import core.parsers.NumericQueryEvaluator;
import org.junit.Assert;
import org.junit.Test;

public class ComparisonOperatorTest {
    @Test
    public void Equals() {
        var r1 = NumericQueryEvaluator.evaluate("1", 1);
        var r2 = NumericQueryEvaluator.evaluate("= 1", 1);
        Assert.assertTrue(r1);
        Assert.assertTrue(r2);
    }

    @Test
    public void GreaterThan() {
        var query = "> 1";
        var r1 = NumericQueryEvaluator.evaluate(query, 0);
        var r2 = NumericQueryEvaluator.evaluate(query, 1);
        var r3 = NumericQueryEvaluator.evaluate(query, 2);

        Assert.assertFalse(r1);
        Assert.assertFalse(r2);
        Assert.assertTrue(r3);
    }

    @Test
    public void GreaterThanOrEqual() {
        var query = ">= 1";
        var r1 = NumericQueryEvaluator.evaluate(query, 2);
        var r2 = NumericQueryEvaluator.evaluate(query, 1);
        var r3 = NumericQueryEvaluator.evaluate(query, 0);

        Assert.assertTrue(r1);
        Assert.assertTrue(r2);
        Assert.assertFalse(r3);
    }

    @Test
    public void LessThan() {
        var query = "< 1";
        var r1 = NumericQueryEvaluator.evaluate(query, 0);
        var r2 = NumericQueryEvaluator.evaluate(query, 1);
        var r3 = NumericQueryEvaluator.evaluate(query, 2);

        Assert.assertTrue(r1);
        Assert.assertFalse(r2);
        Assert.assertFalse(r3);
    }

    @Test
    public void LessThanOrEqual() {
        var query = "<= 1";
        var r1 = NumericQueryEvaluator.evaluate(query, 0);
        var r2 = NumericQueryEvaluator.evaluate(query, 1);
        var r3 = NumericQueryEvaluator.evaluate(query, 2);

        Assert.assertTrue(r1);
        Assert.assertTrue(r2);
        Assert.assertFalse(r3);
    }
}
