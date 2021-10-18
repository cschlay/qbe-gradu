package core.parsers;

import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Parsing operations for GraphML -like queries.
 */
public class GraphMLParser {
    private final DocumentBuilder xmlDocumentBuilder;
    private final Logger logger = Logger.getLogger(GraphMLParser.class.getName());

    /**
     * Parse the query into QueryGraph.
     *
     * @param query a valid GraphML string
     * @return a query traversable graph
     */
    public QueryGraph parse(String query) throws IOException, SAXException {
        byte[] queryBytes = query.getBytes(StandardCharsets.UTF_8);
        var inputStream = new ByteArrayInputStream(queryBytes);

        Document xmlDocument = xmlDocumentBuilder.parse(inputStream);
        NodeList nodes = xmlDocument.getElementsByTagName(GraphMLKeywords.Node);
        NodeList edges = xmlDocument.getElementsByTagName(GraphMLKeywords.Edge);

        var graph = new QueryGraph();
        iterateNodeList(nodes, (Node node) -> addQbeNode(graph, node));
        iterateNodeList(edges, (Node edge) -> addQbeEdge(graph, edge));

        return graph;
    }

    private void addQbeNode(QueryGraph graph, Node node)  {
        var qbeNode = new QbeNode();
        // TODO: Fill attributes
        graph.addNode(qbeNode);
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
        this.xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
}
