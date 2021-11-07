package core.graphs;

import core.parsers.GraphML;
import core.xml.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;


public class ResultGraph extends HashMap<String, QbeNode> {
    private final XmlUtilities xmlUtilities;

    public String toGraphML() {
        Document xmlDocument = xmlUtilities.newDocument();

        Element graph = xmlDocument.createElement(GraphML.Graph);
        xmlDocument.appendChild(graph);

        this.forEach((String name, QbeNode node) -> {
            Element xmlNode = xmlDocument.createElement(GraphML.Node);
            xmlNode.setAttribute(GraphML.IdAttribute, node.id);
            if (node.name != null) {
                xmlNode.setAttribute(GraphML.NameAttribute, node.name);
            }

            if (!node.properties.isEmpty()) {
                node.properties.forEach((String key, QbeData data) -> {
                    Element xmlDataNode = xmlDocument.createElement(GraphML.Data);
                    xmlDataNode.setAttribute(GraphML.KeyAttribute, key);
                    if (data.value != null) {
                        xmlDataNode.setTextContent(data.value.toString());
                    }

                    xmlNode.appendChild(xmlDataNode);
                });
            }

            graph.appendChild(xmlNode);

            // The edges
            node.edges.forEach(edge -> {
                Element xmlEdgeNode = xmlDocument.createElement(GraphML.Edge);
                xmlEdgeNode.setAttribute(GraphML.IdAttribute, edge.id);
                xmlEdgeNode.setAttribute(GraphML.NameAttribute, edge.name);
                xmlEdgeNode.setAttribute(GraphML.SourceAttribute, edge.tailNode != null ? edge.tailNode.id : null);
                xmlEdgeNode.setAttribute(GraphML.TargetAttribute, edge.headNode != null ? edge.headNode.id : null);

                graph.appendChild(xmlEdgeNode);
            });
        });

        return xmlUtilities.dumpXmlDocument(xmlDocument);
    }

    public ResultGraph() throws ParserConfigurationException {
        xmlUtilities = new XmlUtilities();
    }
}
