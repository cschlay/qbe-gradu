package interfaces;

import core.graphs.QueryGraph;
import core.graphs.ResultGraph;

public interface ResultWriter {
    String write(QueryGraph queryGraph, ResultGraph resultGraph);
    Object writeNative(QueryGraph queryGraph, ResultGraph resultGraph);
}
