package utilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static utilities.Comparison.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[Utils] Generic Comparison ")
class ComparisonTest {
    @Test
    void greaterThanTest() {
        assertTrue(greaterThan(2, 1));
        assertFalse(greaterThan(1, 2));

        assertTrue(greaterThan(2.01, 2.0));
        assertFalse(greaterThan(1.99, 2.0));
    }

    @Test
    void greaterThanOrEqualToTest() {
        assertTrue(greaterThanOrEqualTo(2, 1));
        assertTrue(greaterThanOrEqualTo(1, 1));
        assertFalse(greaterThanOrEqualTo(0, 1));

        assertTrue(greaterThanOrEqualTo(1.01, 1.0));
        assertTrue(greaterThanOrEqualTo(1.0, 1.0));
        assertFalse(greaterThanOrEqualTo(0.9, 1));
    }

    @Test
    void lessThanTest() {
        assertTrue(lessThan(0, 1));
        assertFalse(lessThan(2, 1));

        assertTrue(lessThan(1.99, 2.0));
        assertFalse(lessThan(2.0, 1.99));
    }

    @Test
    void lessThanOrEqualToTest() {
        assertTrue(lessThanOrEqualTo(0, 1));
        assertTrue(lessThanOrEqualTo(1, 1));
        assertFalse(lessThanOrEqualTo(2, 1));

        assertTrue(lessThanOrEqualTo(1.99, 2.0));
        assertTrue(lessThanOrEqualTo(2.0, 2.0));
        assertFalse(lessThanOrEqualTo(2.0, 1.99));
    }
}
