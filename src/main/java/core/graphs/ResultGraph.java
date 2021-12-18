package core.graphs;

import interfaces.ResultWriter;
import syntax.graphml.GraphML;
import syntax.tabular.TabularResultWriter;
import core.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.function.Consumer;


public class ResultGraph extends HashMap<String, QbeNode> {
    private final XmlUtilities xmlUtilities;

    // TODO: MOve aaway
    public ResultGraph() throws ParserConfigurationException {
        xmlUtilities = new XmlUtilities();
    }

    // TODO: Move away
    public String toGraphML() {
        Document xmlDocument = xmlUtilities.newDocument();
        Element graph = xmlDocument.createElement(GraphML.Graph);
        xmlDocument.appendChild(graph);

        var edges = new HashMap<String, QbeEdge>();
        this.forEach((String name, QbeNode node) -> {
            Element xmlNodeElement = toGraphMLNode(xmlDocument, node);
            graph.appendChild(xmlNodeElement);

            node.edges.forEach((QbeEdge edge) -> {
                if (!edges.containsKey(edge.id)) {
                    edges.put(edge.id, edge);

                }
            });
        });
        edges.forEach((id, edge) -> {
            Element xmlEdgeElement = toGraphMLEdge(xmlDocument, edge);
            graph.appendChild(xmlEdgeElement);
        });

        return xmlUtilities.dumpXmlDocument(xmlDocument);
    }

    public Element toGraphMLEdge(Document xmlDocument, QbeEdge edge) {
        Element xmlEdge = xmlDocument.createElement(GraphML.Edge);
        xmlEdge.setAttribute(GraphML.IdAttribute, edge.id);
        xmlEdge.setAttribute(GraphML.NameAttribute, edge.name);
        xmlEdge.setAttribute(GraphML.SourceAttribute, edge.tailNode != null ? edge.tailNode.id : null);
        xmlEdge.setAttribute(GraphML.TargetAttribute, edge.headNode != null ? edge.headNode.id : null);
        toGraphMLData(xmlDocument, edge.properties, xmlEdge::appendChild);

        return xmlEdge;
    }

    public Element toGraphMLNode(Document xmlDocument, QbeNode node) {
        Element xmlNode = xmlDocument.createElement(GraphML.Node);
        xmlNode.setAttribute(GraphML.IdAttribute, node.id);
        xmlNode.setAttribute(GraphML.NameAttribute, node.name);
        toGraphMLData(xmlDocument, node.properties, xmlNode::appendChild);

        return xmlNode;
    }

    public void toGraphMLData(Document xmlDocument, HashMap<String, QbeData> properties, Consumer<Element> onCreated) {
        properties.forEach((String propertyName, QbeData data) -> {
            Element xmlDataNode = xmlDocument.createElement(GraphML.Data);
            xmlDataNode.setAttribute(GraphML.KeyAttribute, propertyName);
            if (data.value != null) {
                xmlDataNode.setTextContent(data.value.toString());
            }
            String dataType = data.getType();
            if (!GraphML.TypeText.equals(dataType)) {
                xmlDataNode.setAttribute(GraphML.TypeAttribute, dataType);
            }

            onCreated.accept(xmlDataNode);
        });
    }
}
