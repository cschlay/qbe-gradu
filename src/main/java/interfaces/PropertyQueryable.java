package interfaces;

import core.graphs.QbeData;

import java.util.Map;

public interface PropertyQueryable {
    Map<String, QbeData> getProperties();
}
