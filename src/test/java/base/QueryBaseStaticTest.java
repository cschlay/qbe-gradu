package base;

import db.neo4j.Neo4j;
import org.junit.jupiter.api.BeforeAll;

public abstract class QueryBaseStaticTest extends BaseTest {
    @BeforeAll
    static void resetDatabase() {
        Neo4j.resetDatabase(db);
    }
}
