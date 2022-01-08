package core;

import core.graphs.LogicalExpression;
import core.graphs.QbeData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QbeDataTest {
    @Test
    @DisplayName("should accept nulls if enabled")
    void acceptNull() {
        var data = new QbeData("nickname",false, true);
        assertTrue(data.check(null));
    }

    @Test
    @DisplayName("should reject nulls by default")
    void rejectNull() {
        var data = new QbeData("password", false, false);
        assertFalse(data.check(null));
    }

    @Test
    @DisplayName("should bypass checks if example value is null")
    void bypassChecks() {
        var data = new QbeData(null, false, false);
        assertTrue(data.check("any"));
    }

    @Test
    @DisplayName("should check equality if no constraints is defined")
    void checkEquality() {
        var data = new QbeData(10, false, false);
        assertTrue(data.check(10));
        assertFalse(data.check("10"));
    }

    @Test
    @DisplayName("should check logical expressions")
    void checkLogicalExpressions() {
        var expression = new LogicalExpression("AND(> 1, < 5)");
        var data = new QbeData(expression, false, false);

        assertFalse(data.check(0));
        assertTrue(data.check(3));
        assertFalse(data.check(6));
    }

    @Test
    @DisplayName("should check strings using regex")
    void checkByRegex() {
        var data = new QbeData("Introduction to .*", false, false);
        assertFalse(data.check("Logic"));
        assertTrue(data.check("Introduction to Algorithms"));
        assertTrue(data.check("Introduction to Logic"));
    }
}
