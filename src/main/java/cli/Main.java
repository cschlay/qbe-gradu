package cli;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;

import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;


public class Main {
    public static void main(String[] args) {
        // The startup commands can be found at:
        // https://neo4j.com/docs/java-reference/current/java-embedded/include-neo4j/#tutorials-java-embedded-setup-startstop
        var dataLocation = Path.of("data");

        DatabaseManagementService management = new DatabaseManagementServiceBuilder(dataLocation).build();

        // Community edition doesn't support multiple database names.
        // https://github.com/neo4j/neo4j/issues/12506
        GraphDatabaseService db = management.database(DEFAULT_DATABASE_NAME);

        try {
            var session = new QuerySession(db);
            session.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        management.shutdown();
        System.exit(0);
    }
}
