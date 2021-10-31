package core.graphs;

import core.parsers.GraphMLAttributes;
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
            xmlNode.setAttribute(GraphMLAttributes.Id, node.id);
            if (node.name != null) {
                xmlNode.setAttribute(GraphMLAttributes.NodeName, node.name);
            }

            if (!node.properties.isEmpty()) {
                node.properties.forEach((String key, QbeData data) -> {
                    Element xmlDataNode = xmlDocument.createElement("data");
                    xmlDataNode.setAttribute("key", key);
                    if (data.value != null) {
                        xmlDataNode.setTextContent(data.value.toString());
                    }

                    xmlNode.appendChild(xmlDataNode);
                });
            }

            graph.appendChild(xmlNode);
        });

        return xmlUtilities.dumpXmlDocument(xmlDocument);
    }

    public ResultGraph() throws ParserConfigurationException {
        xmlUtilities = new XmlUtilities();
    }
}
