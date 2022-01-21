package core.graphs;

import core.exceptions.QbeException;
import org.jetbrains.annotations.NotNull;
import syntax.graphml.GraphML;
import syntax.graphml.GraphMLResultWriter;
import core.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


public class ResultGraph extends Graph {
    public final transient Set<QbeEdge> unvisitedEdges;

    public ResultGraph() {
        unvisitedEdges = new HashSet<>();
    }

    public int order() {
        return super.size();
    }

    public void put(QbeNode node) {
        put(node.id, node);
    }

    // TODO: Continue here, override put method so that it won't link by name.
    // Result graphs are always by id!
    public void put(@NotNull QbeEdge edge) throws QbeException {
        if (edge.id == null) {
            throw new QbeException("Edge '%s' doesn't have id!", edge.name);
        }

        boolean added = false;
        if (edge.headNode != null) {
            edge.headNode.edges.put(edge.id, edge);
            put(edge.headNode.id, edge.headNode);
            added = true;
        }
        if (edge.tailNode != null) {
            edge.tailNode.edges.put(edge.id, edge);
            put(edge.tailNode.id, edge.headNode);
            added = true;
        }

        if (!added) {
            hangingEdges.add(edge);
        }
    }
}
