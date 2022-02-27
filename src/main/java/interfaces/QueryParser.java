package interfaces;

import exceptions.SyntaxError;
import graphs.QueryGraph;

public interface QueryParser {
    QueryGraph parse(String query) throws SyntaxError;
}
