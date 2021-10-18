package core.db.neo4j.labels;

import org.neo4j.graphdb.Label;

public class CourseLabel implements Label {

    @Override
    public String name() {
        return "Course";
    }
}
