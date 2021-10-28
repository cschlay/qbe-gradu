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
            courseAlgorithms.setProperty("title", "Algorithms");
            courseAlgorithms.setProperty("difficulty", 4);

            courseAlgorithms.createRelationshipTo(topicDijkstraAlgorithm, Relations.contains);
            var courseGraphTheory = tx.createNode(Labels.Course);
            courseGraphTheory.setProperty("title", "Graph Theory");
            courseGraphTheory.setProperty("difficulty", 3);
            courseGraphTheory.createRelationshipTo(topicDijkstraAlgorithm, Relations.contains);

            //var lecturerA = tx.createNode(Labels.lecturer);
            //lecturerA.createRelationshipTo(courseA, RelationshipType.withName("teaches"));

            tx.commit();
        }
    }
}
