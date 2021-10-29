package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.*;
import core.xml.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.function.Consumer;

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

        var graph = new QueryGraph();
        graph.nodes = GraphMLNodeParser.parseNodeList(nodes);

        // TODO: Parse edges
        iterateNodeList(edges, (Node edge) -> addQbeEdge(graph, edge));

        return graph;
    }

    private void addQbeEdge(QueryGraph graph, Node node) {
        var qbeEdge = new QbeEdge();
        // TODO: Fill attributes
        graph.addEdge(qbeEdge);
    }

    // Custom iterator as NodeList doesn't support for each.
    private void iterateNodeList(NodeList nodeList, Consumer<Node> apply) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            apply.accept(node);
        }
    }

    public GraphMLParser() throws ParserConfigurationException {
        this.xmlUtilities = new XmlUtilities();
    }
}
