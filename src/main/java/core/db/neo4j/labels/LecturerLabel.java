package core.db.neo4j.labels;

import org.neo4j.graphdb.Label;

public class LecturerLabel implements Label {

    @Override
    public String name() {
        return "Lecturer";
    }
}
