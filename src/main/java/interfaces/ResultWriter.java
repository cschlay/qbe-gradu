package interfaces;

import graphs.QueryGraph;
import graphs.ResultGraph;

public interface ResultWriter {
    String write(QueryGraph queryGraph, ResultGraph resultGraph);
    Object writeNative(QueryGraph queryGraph, ResultGraph resultGraph);
}
