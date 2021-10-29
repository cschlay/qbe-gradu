package core.graphs;

import core.interfaces.Graphable;
import core.parsers.GraphMLAttributes;
import core.parsers.GraphML;
import core.xml.XmlUtilities;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;


public class ResultGraph implements Graphable {
    @NotNull
    public HashMap<String, QbeNode> nodes;
    private final XmlUtilities xmlUtilities;

    public String toGraphML() {
        Document xmlDocument = xmlUtilities.newDocument();

        Element graph = xmlDocument.createElement(GraphML.Graph);
        xmlDocument.appendChild(graph);

        nodes.forEach((String name, QbeNode node) -> {
            Element xmlNode = xmlDocument.createElement(GraphML.Node);
            xmlNode.setAttribute(GraphMLAttributes.Id, node.getId());
            if (node.name != null) {
                xmlNode.setAttribute(GraphMLAttributes.NodeName, node.name);
            }

            // TODO: Extract to own function
            if (!node.properties.isEmpty()) {
                node.properties.forEach((String key, Object value) -> {
                    Element xmlDataNode = xmlDocument.createElement("data");
                    xmlDataNode.setAttribute("key", key);
                    xmlDataNode.setTextContent(value.toString());

                    xmlNode.appendChild(xmlDataNode);
                });
            }

            graph.appendChild(xmlNode);
        });

        return xmlUtilities.dumpXmlDocument(xmlDocument);
    }

    public ResultGraph() throws ParserConfigurationException {
        nodes = new HashMap<>();
        xmlUtilities = new XmlUtilities();
    }
}
