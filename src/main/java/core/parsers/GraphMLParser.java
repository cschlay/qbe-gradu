package core.parsers;

import core.graphs.QbeEdge;
import core.graphs.QbeNode;
import core.graphs.QueryGraph;
import core.xml.XmlUtilities;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
    public QueryGraph parse(String query) throws IOException, SAXException {
        Document xmlDocument = xmlUtilities.readXmlString(query);
        NodeList nodes = xmlDocument.getElementsByTagName(GraphMLKeywords.Node);
        NodeList edges = xmlDocument.getElementsByTagName(GraphMLKeywords.Edge);

        var graph = new QueryGraph();
        iterateNodeList(nodes, (Node node) -> addQbeNode(graph, node));
        iterateNodeList(edges, (Node edge) -> addQbeEdge(graph, edge));

        return graph;
    }

    private void addQbeNode(QueryGraph graph, Node node)  {
        var qbeNode = new QbeNode();
        qbeNode.name = readAttribute(GraphMLAttributes.NodeName, node);

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

    @Nullable
    private String readAttribute(String name, Node node) {
        Node attribute = node.getAttributes().getNamedItem(name);
        if (attribute != null) {
            return attribute.getNodeValue();
        }
        return null;
    }

    public GraphMLParser() throws ParserConfigurationException {
        this.xmlUtilities = new XmlUtilities();
    }
}
