package parsers;

import core.parsers.LogicalQueryParser;
import org.junit.Assert;
import org.junit.Test;

public class LogicalQueryParserTest {
    @Test
    public void BinaryAnd() {
        var parser = new LogicalQueryParser();
        var node = parser.parse("AND(1, >=5)");

        Assert.assertEquals("AND", node.value);
        Assert.assertEquals("1", node.children.get(0).value);
        Assert.assertEquals(">=5", node.children.get(1).value);
    }

    @Test
    public void BinaryOr() {
        var parser = new LogicalQueryParser();
        var node = parser.parse("OR(<1, 5)");

        Assert.assertEquals("OR", node.value);
        Assert.assertEquals("<1", node.children.get(0).value);
        Assert.assertEquals("5", node.children.get(1).value);
    }

    @Test
    public void NotSimple()
    {
        var parser = new LogicalQueryParser();
        var node = parser.parse("NOT(1)");

        Assert.assertEquals("NOT", node.value);
        Assert.assertEquals("1", node.children.get(0).value);
    }

    @Test
    public void BinaryNest() {
        var parser = new LogicalQueryParser();
        var node = parser.parse("AND(OR(1, 3), OR(2, 4))");

        Assert.assertEquals("AND", node.value);
        Assert.assertEquals(2, node.children.size());

        Assert.assertEquals("OR", node.children.get(0).value);
        Assert.assertEquals("1", node.children.get(0).children.get(0).value);
        Assert.assertEquals("3", node.children.get(0).children.get(1).value);

        Assert.assertEquals("OR", node.children.get(1).value);
        Assert.assertEquals("2", node.children.get(1).children.get(0).value);
        Assert.assertEquals("4", node.children.get(1).children.get(1).value);
    }

    @Test
    public void Complex() {
        var parser = new LogicalQueryParser();
        var node = parser.parse("AND(12, AND(1, OR(4, 5)), NOT(5), NOT(6))");

        Assert.assertEquals("AND", node.value);

        for (var x : node.children) {
            System.out.println(x.value);
        }

        Assert.assertEquals(4, node.children.size());

        /*var child1 = node.children.get(0);
        Assert.assertEquals("NOT", child1.value);*/

    }

    @Test
    public void In() {
        var parser = new LogicalQueryParser();
        var node = parser.parse("OR(1, 2, 3, 4)");
        Assert.assertEquals("OR", node.value);
        Assert.assertEquals(4, node.children.size());

    }
}
