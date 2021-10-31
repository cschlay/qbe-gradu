package demo;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

/**
 * Seeds the database with demo data. Remember to flush before seeding.
 */
public class CourseGraphDemo {
    public static class Labels {
        public static Label Course = Label.label("Course");
        public static Label Lecturer = Label.label("Lecturer");
        public static Label Topic = Label.label("Topic");
    }

    public static class Relations {
        public static RelationshipType contains = RelationshipType.withName("contains");
        public static RelationshipType teaches = RelationshipType.withName("teaches");
    }

    /**
     * Fictional educational data based on my experiences in Tampere University.
     */
    public static void seedEducationData(GraphDatabaseService db) {
        try (var tx = db.beginTx()) {
            var topicDijkstraAlgorithm = tx.createNode(Labels.Topic);
            topicDijkstraAlgorithm.setProperty("title", "Dijkstra's Algorithm");

            var courseAlgorithms = tx.createNode(Labels.Course);
            courseAlgorithms.setProperty("title", "Introduction to Algorithms");
            courseAlgorithms.setProperty("difficulty", 4);
            courseAlgorithms.createRelationshipTo(topicDijkstraAlgorithm, Relations.contains);

            var courseLogic = tx.createNode(Labels.Course);
            courseLogic.setProperty("title", "Introduction to Logic");
            courseLogic.setProperty("difficulty", 2);

            var courseGraphTheory = tx.createNode(Labels.Course);
            courseGraphTheory.setProperty("title", "Graph Theory");
            courseGraphTheory.setProperty("difficulty", 3);
            courseGraphTheory.createRelationshipTo(topicDijkstraAlgorithm, Relations.contains);

            tx.commit();
        }
    }
}
