package syntax.graphml;

import core.exceptions.SyntaxError;
import core.graphs.*;
import core.utilities.XmlUtilities;
import interfaces.QueryParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Parsing operations for GraphML -like queries.
 */
public class GraphMLParser implements QueryParser {
    private final XmlUtilities xmlUtilities;

    /**
     * Parse the query into QueryGraph.
     *
     * @param query a valid GraphML string
     * @return a query traversable graph
     */
    public QueryGraph parse(String query) throws SyntaxError {
        try {
            Document xmlDocument = xmlUtilities.readXmlString(query);
            NodeList nodes = xmlDocument.getElementsByTagName(GraphML.Node);
            NodeList edges = xmlDocument.getElementsByTagName(GraphML.Edge);

            QueryGraph graph = GraphMLNodeParser.parseNodeList(nodes);
            GraphMLEdgeParser.parseNodeList(edges, graph);
            return graph;

        } catch (IOException | SAXException exception) {
            throw new SyntaxError("s");
        }
    }

    public GraphMLParser() throws ParserConfigurationException {
        this.xmlUtilities = new XmlUtilities();
    }
}
