package graphs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[Traversal] QbeData Class")
class QbeDataTest {
    @Test
    @DisplayName("should accept only null if null")
    void acceptNull() {
        var data = new QbeData(null,false);
        assertTrue(data.check(null));
        assertFalse(data.check(1));
        assertFalse(data.check("x"));
    }

    @Test
    @DisplayName("should reject nulls by default")
    void rejectNull() {
        var data = new QbeData("password", false);
        assertFalse(data.check(null));
    }

    @Test
    @DisplayName("should bypass checks if example value is null")
    void bypassChecks() {
        var data = new QbeData("", false);
        assertTrue(data.check("any"));
        assertTrue(data.check(null));
        assertTrue(data.check(1));
    }

    @Test
    @DisplayName("should check equality if no constraints is defined")
    void checkEquality() {
        var data = new QbeData(10, false);
        assertTrue(data.check(10));
        assertFalse(data.check("10"));
    }

    @Test
    @DisplayName("should check logical expressions")
    void checkLogicalExpressions() {
        var expression = new LogicalExpression("AND(> 1, < 5)");
        var data = new QbeData(expression, false);

        assertFalse(data.check(0));
        assertTrue(data.check(3));
        assertFalse(data.check(6));
    }

    @Test
    @DisplayName("should check strings using regex")
    void checkByRegex() {
        var data = new QbeData("Introduction to .*", false);
        assertFalse(data.check("Logic"));
        assertTrue(data.check("Introduction to Algorithms"));
        assertTrue(data.check("Introduction to Logic"));
    }
}
