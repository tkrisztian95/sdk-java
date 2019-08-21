/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */
package com.acrolinx.client.sdk.check;

import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class MultiPartAcrolinxDocument implements AcrolinxDocument {

    private Document document;
    private DocumentBuilder documentBuilder;
    private Element root;

    public MultiPartAcrolinxDocument(String rootElement, String publicId, String systemId) throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        this.document = documentBuilder.newDocument();

        if (publicId != null || systemId != null) {
            DOMImplementation implementation = this.document.getImplementation();
            DocumentType documentType = implementation.createDocumentType(rootElement, publicId, systemId);
            this.document.appendChild(documentType);
        }

        this.root = this.document.createElement(rootElement);
        this.document.appendChild(this.root);

    }

    public MultiPartAcrolinxDocument(String rootElement) throws ParserConfigurationException {
        this(rootElement, null, null);
    }

    public void addDocumentPart(String partName, String content, @Nullable Map<String, String> attributes) {
        Element element = this.document.createElement(partName);
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                Attr attribute = this.document.createAttribute(entry.getKey());
                attribute.setValue(entry.getValue());
                element.setAttributeNode(attribute);
            }
        }
        Text textNode = this.document.createTextNode(content);
        element.appendChild(textNode);
        this.root.appendChild(element);
    }

    public void addDocumentNode(String xml) throws ParserConfigurationException, IOException, SAXException {
        Element node = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes()))
                .getDocumentElement();

        Node importedNode = this.document.importNode(node, true);
        this.root.appendChild(importedNode);
    }


    @Override
    public String getContent() throws AcrolinxException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DocumentType doctype = this.document.getDoctype();
            if (doctype != null) {
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
            }

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(this.document), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            throw new AcrolinxException(e);
        }
    }

}
