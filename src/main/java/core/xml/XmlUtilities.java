package core.xml;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * The default XML utilities are rather confusing, there exists at least 4 different ways to read and write XML.
 *
 * This utility class contains one interface to read and write.
 *
 * Writing is based on:
 * - https://www.baeldung.com/java-write-xml-document-file
 * - https://stackoverflow.com/a/23220388
 */
public class XmlUtilities {
    private final DocumentBuilder xmlDocumentBuilder;
    private final TransformerFactory transformerFactory;

    public String dumpXmlDocument(@NotNull Document xmlDocument) {
        try {
            Transformer transformer = newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception exception) {
            return "Dumping xml -document failed.";
        }
    }

    public Document newDocument() {
        return xmlDocumentBuilder.newDocument();
    }

    /**
     * Configures transformation to xml string.
     *
     * @return the Transformer object
     * @throws TransformerConfigurationException if Java libraries have bugs
     */
    public Transformer newTransformer() throws TransformerConfigurationException {
        Transformer transformer = this.transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        return transformer;
    }

    /**
     * Reads the xmlString into a Document object.
     *
     * @param xmlString a valid xml -string
     * @return the Document object
     * @throws IOException if xml is not valid
     * @throws SAXException if xml is not valid
     */
    public Document readXmlString(@NotNull String xmlString) throws IOException, SAXException {
        byte[] queryBytes = xmlString.getBytes(StandardCharsets.UTF_8);
        var inputStream = new ByteArrayInputStream(queryBytes);
        return xmlDocumentBuilder.parse(inputStream);
    }

    /**
     * Initializes the factory classes which are used to construct individual documents.
     *
     * @throws ParserConfigurationException only if Java libraries have bugs
     */
    public XmlUtilities() throws ParserConfigurationException {
        xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        transformerFactory = TransformerFactory.newInstance();
    }
}
