package demo;

import core.db.neo4j.Labels;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

/**
 * Seeds the database with demo data. Remember to flush before seeding.
 */
public class DemoSeeder {
    /**
     * Fictional educational data based on my experiences in Tampere University.
     */
    public static void seedEducationData(GraphDatabaseService db) {
        try (var tx = db.beginTx()) {
            var courseA = tx.createNode(Labels.course);
            var lecturerA = tx.createNode(Labels.lecturer);
            lecturerA.createRelationshipTo(courseA, RelationshipType.withName("teaches"));

            tx.commit();
        }
    }
}
