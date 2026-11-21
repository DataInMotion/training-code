package biz.datainmotion.training.docservice;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/** Liest Angaben aus den XML-Begleitdateien der Dokumente. */
public class XmlMetadataReader {

    /** Liefert den Titel aus der Begleitdatei oder einen leeren Text. */
    public String readTitle(InputStream xml) throws Exception {
        Document doc = hardenedFactory().newDocumentBuilder().parse(xml);
        NodeList titles = doc.getElementsByTagName("title");
        return titles.getLength() == 0 ? "" : titles.item(0).getTextContent().trim();
    }

    /** Liefert den gesamten Textinhalt einer Begleitdatei. */
    public String parseXml(InputStream xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(xml);
        return doc.getDocumentElement().getTextContent();
    }

    private static DocumentBuilderFactory hardenedFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return dbf;
    }
}
