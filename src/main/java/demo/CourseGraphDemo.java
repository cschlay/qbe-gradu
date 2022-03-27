package demo;

import org.neo4j.graphdb.*;

/**
 * Seeds the database with demo data. Remember to flush before seeding.
 */
public class CourseGraphDemo {
    private CourseGraphDemo() {}

    public static void seed(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node courseGT = nodeCourse(tx, "Introduction to Graph Theory");
            Node courseMCS = nodeCourse(tx, "Mathematics for Computer Science");
            Node bookMCS = nodeBook(tx, "Mathematics for Computer Science", 2010, 5.00);
            Node bookRA = nodeBook(tx, "Randomized Algorithms", 1989, 60.00);
            Node topicGT = nodeTopic(tx, "Graph Theory", 2);
            Node topicP = nodeTopic(tx, "Probability", 4);

            edgeCourseContainsBook(courseGT, topicGT);
            edgeCourseRecommendsCourse(courseMCS, courseMCS, "Winter II");
            edgeCourseRecommendsCourse(courseMCS, courseGT, "Fall I");
            edgeCourseUsesBook(courseMCS, bookMCS, false);
            edgeCourseUsesBook(courseMCS, bookRA, true);
            bookMCS.createRelationshipTo(courseMCS, RelationshipType.withName("written for"));
            edgeCourseContainsTopic(bookMCS, topicGT, false);
            edgeCourseContainsTopic(bookMCS, topicP, true);
            edgeCourseContainsTopic(bookRA, topicP, true);
            tx.commit();
        }
    }

    private static Node nodeBook(Transaction tx, String title, int year, double price) {
        Node book = tx.createNode(Label.label("Book"));
        book.setProperty("title", title);
        book.setProperty("year", year);
        book.setProperty("price", price);
        return book;
    }

    private static Node nodeCourse(Transaction tx, String name) {
        Node course = tx.createNode(Label.label("Course"));
        course.setProperty("name", name);
        return course;
    }

    private static Node nodeTopic(Transaction tx, String name, int level) {
        Node topic = tx.createNode(Label.label("Topic"));
        topic.setProperty("name", name);
        topic.setProperty("level", level);
        return topic;
    }

    private static void edgeCourseContainsBook(Node tail, Node head) {
        Relationship edge = tail.createRelationshipTo(head, RelationshipType.withName("contains"));
        edge.setProperty("difficulty", "easy");
        edge.setProperty("primary", true);
    }

    private static void edgeCourseContainsTopic(Node tail, Node head, boolean theory) {
        Relationship edge = tail.createRelationshipTo(head, RelationshipType.withName("contains"));
        edge.setProperty("theory", theory);
    }

    private static void edgeCourseRecommendsCourse(Node tail, Node head, String period) {
        Relationship edge = tail.createRelationshipTo(head, RelationshipType.withName("recommends"));
        edge.setProperty("period", period);
    }

    private static void edgeCourseUsesBook(Node tail, Node head, boolean supplemental) {
        Relationship edge = tail.createRelationshipTo(head, RelationshipType.withName("uses"));
        edge.setProperty("supplemental", supplemental);
    }
}
