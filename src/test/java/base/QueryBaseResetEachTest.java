package base;

import db.neo4j.Neo4j;
import org.junit.jupiter.api.BeforeEach;

public abstract class QueryBaseResetEachTest extends BaseTest {
    @BeforeEach
    public void beforeEach() {
        Neo4j.resetDatabase(db);
    }
}
