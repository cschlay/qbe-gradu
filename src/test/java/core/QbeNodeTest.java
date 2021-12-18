package core;

import core.graphs.QbeNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class QbeNodeTest {
    @Test
    @DisplayName("should convert long id to String")
    void convertLongToString() {
        var node = new QbeNode(1, "Course");
        assertEquals("1", node.id);
    }

    @Test
    @DisplayName("should check name equality in comparison")
    void equalNames() {
        var course1 = new QbeNode("Course");
        var course2 = new QbeNode("Course");
        var lecture = new QbeNode("Lecture");

        assertTrue(course1.equalByName(course2));
        assertFalse(course1.equalByName(lecture));
    }
}
