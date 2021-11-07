package core.interfaces;

import core.graphs.QbeData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface PropertyQueryable {
    @Nullable String name = null;
    HashMap<String, QbeData> properties = null;
}
