package syntax.graphml;

import core.exceptions.SyntaxError;
import core.graphs.*;
import core.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Parsing operations for GraphML -like queries.
 */
public class GraphMLParser {
    private final XmlUtilities xmlUtilities;

    /**
     * Parse the query into QueryGraph.
     *
     * @param query a valid GraphML string
     * @return a query traversable graph
     */
    public QueryGraph parse(String query) throws IOException, SAXException, SyntaxError {
        Document xmlDocument = xmlUtilities.readXmlString(query);
        NodeList nodes = xmlDocument.getElementsByTagName(GraphML.Node);
        NodeList edges = xmlDocument.getElementsByTagName(GraphML.Edge);

        QueryGraph graph = GraphMLNodeParser.parseNodeList(nodes);
        GraphMLEdgeParser.parseNodeList(edges, graph);

        return graph;
    }

    public GraphMLParser() throws ParserConfigurationException {
        this.xmlUtilities = new XmlUtilities();
    }
}
