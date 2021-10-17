package cli;

import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;


public class Main {
    public static void main(String[] args) {
        // The startup commands can be found at:
        // https://neo4j.com/docs/java-reference/current/java-embedded/include-neo4j/#tutorials-java-embedded-setup-startstop
        var dataLocation = Path.of("data");

        var management = new DatabaseManagementServiceBuilder(dataLocation).build();
        // Community edition doesn't support multiple database names.
        // https://github.com/neo4j/neo4j/issues/12506
        var db = management.database(DEFAULT_DATABASE_NAME);

        var session = new QuerySession(db);
        session.start();

        management.shutdown();
    }
}
