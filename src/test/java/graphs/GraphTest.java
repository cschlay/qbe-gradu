package graphs;

import exceptions.QbeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Unit] Graph")
class GraphTest {
    @Nested
    @DisplayName("Union")
    class UnionTest {
        private ResultGraph g1;
        private ResultGraph g2;

        @BeforeEach
        void setup() {
            g1 = new ResultGraph();
            g2 = new ResultGraph();
        }

        @Test
        void differentNodes() {
            var n1 = new QbeNode(1, "Book");
            n1.addProperty("year", 2019);
            var n2 = new QbeNode(2, "Book");
            n2.addProperty("year", 2020);
            g1.put(n1);
            g2.put(n2);

            ResultGraph graph = g1.union(g2);
            assertEquals(2019, graph.get("1").properties.get("year").value);
            assertEquals(2020, graph.get("2").properties.get("year").value);
        }

        @Test
        void sameNodes() {
            var n1 = new QbeNode(1, "Book");
            n1.addProperty("year", 2019);
            n1.addProperty("name", "Graphs");
            var n2 = new QbeNode(1, "Book");
            n2.addProperty("year", 2020);
            g1.put(n1);
            g2.put(n2);

            ResultGraph graph = g1.union(g2);
            assertEquals(2020, graph.get("1").properties.get("year").value);
            assertEquals("Graphs", graph.get("1").properties.get("name").value);
        }

        @Test
        void differentEdges() throws QbeException {
            var course1 = new QbeNode(1, "Course");
            var book1 = new QbeNode(2, "Book");
            var edge1 = new QbeEdge(3, "uses");
            edge1.tailNode = course1;
            edge1.headNode = book1;

            var course2 = new QbeNode(1, "Book");
            var book2 = new QbeNode(2, "Book");
            var edge2 = new QbeEdge(4, "uses");
            edge2.tailNode = course2;
            edge2.headNode = book2;

            g1.put(edge1);
            g2.put(edge2);

            ResultGraph graph = g1.union(g2);
            assertNotNull(graph.getEdge("1", "3"));
            assertNotNull(graph.getEdge("1", "4"));
        }

        @Test
        void sameEdges() throws QbeException {
            var course1 = new QbeNode(1, "Course");
            var book1 = new QbeNode(2, "Book");
            var edge1 = new QbeEdge(3, "uses");
            edge1.addProperty("supplemental", false);
            edge1.addProperty("expensive", false);
            edge1.tailNode = course1;
            edge1.headNode = book1;

            var course2 = new QbeNode(1, "Book");
            var book2 = new QbeNode(2, "Book");
            var edge2 = new QbeEdge(3, "uses");
            edge2.addProperty("supplemental", true);
            edge2.addProperty("important", true);
            edge2.tailNode = course2;
            edge2.headNode = book2;

            g1.put(edge1);
            g2.put(edge2);

            ResultGraph graph = g1.union(g2);
            QbeEdge edge = graph.getEdge("1", "3");

            assertEquals(true, edge.properties.get("supplemental").value);
            assertEquals(false, edge.properties.get("expensive").value);
            assertEquals(true, edge.properties.get("important").value);
        }
    }
}
