package interfaces;

import core.exceptions.SyntaxError;
import core.graphs.QueryGraph;

public interface QueryParser {
    QueryGraph parse(String query) throws SyntaxError;
}
