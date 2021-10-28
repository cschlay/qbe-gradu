package core.parsers;

import core.exceptions.SyntaxError;
import core.graphs.*;
import core.xml.XmlUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.validation.constraints.Null;
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

    private void addQbeNode(QueryGraph graph, Node node) throws SyntaxError {
        var qbeNode = new QbeNode();
        qbeNode.name = readAttribute(GraphMLAttributes.NodeName, node);

        NodeList dataNodes = node.getChildNodes();
        iterateNodeList(dataNodes, (Node dataNode) -> {
            // Notice that #text nodes are included as child nodes
            String tagName = dataNode.getNodeName();
            if (GraphMLKeywords.Data.equals(tagName)) {
                @Nullable String key = readAttribute(GraphMLAttributes.Key, dataNode);
                @Nullable String type = readAttribute(GraphMLAttributes.Type, dataNode);

                @Nullable QbeData queryData = parseConstraintNodes(type, dataNode);

                if (type != null) {
                    // TODO: Handle regex and numbers
                }

                // Having textContent to be null simplified checks in traversal.
                @Nullable String textContent = dataNode.getTextContent();
                if (textContent != null && textContent.isEmpty()) {
                    textContent = null;
                }
                // TODO: Use QbeData instead
                qbeNode.properties.put(key, textContent);
            }
        });

        graph.addNode(qbeNode);
    }

    @Nullable private QbeData parseConstraintNodes(@Nullable String fieldType, Node dataNode) throws SyntaxError {
        NodeList constraintNodes = dataNode.getChildNodes();

        var queryData = new QbeData();
        for (int i = 0; i < constraintNodes.getLength(); i++) {
            Node node = constraintNodes.item(i);
            if ("constraint".equals(node.getNodeName())) {
                @Nullable String constraintType = readAttribute("type", dataNode);
                @Nullable String textContent = node.getTextContent();

                if (constraintType == null || textContent == null) {
                    throw new SyntaxError("Constraint type and textContent cannot be null!");
                }

                // The text content need to cast into same type as the field
                if ("integer".equals(fieldType)) {
                    var constraint = new QbeConstraint(constraintType, Integer.parseInt(textContent));
                    queryData.constraints.add(constraint);
                }
            }
        }

        return queryData.constraints.isEmpty() ? null : queryData;
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
    private String readAttribute(@NotNull String attributeName, @NotNull Node node) {
        @Nullable NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            @Nullable Node attribute = attributes.getNamedItem(attributeName);
            if (attribute != null) {
                return attribute.getNodeValue();
            }
        }
        return null;
    }

    public GraphMLParser() throws ParserConfigurationException {
        this.xmlUtilities = new XmlUtilities();
    }
}
