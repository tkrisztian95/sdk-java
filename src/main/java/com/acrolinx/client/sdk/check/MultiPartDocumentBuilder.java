/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */

package com.acrolinx.client.sdk.check;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.acrolinx.client.sdk.exceptions.AcrolinxException;

public class MultiPartDocumentBuilder
{

    private org.w3c.dom.Document document;
    private Element root;
    private static final Logger logger = LoggerFactory.getLogger(MultiPartDocumentBuilder.class);

    public MultiPartDocumentBuilder(String rootElement, @Nullable String publicId, @Nullable String systemId)
            throws AcrolinxException
    {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.debug("Unable to create Document Builder Factory.");
            throw new AcrolinxException(e);
        }
        this.document = documentBuilder.newDocument();

        if (publicId != null || systemId != null) {
            logger.debug("Doctype Public Id: " + publicId);
            logger.debug("Doctype system Id: " + systemId);

            DOMImplementation implementation = this.document.getImplementation();
            DocumentType documentType = implementation.createDocumentType(rootElement, publicId, systemId);
            this.document.appendChild(documentType);
        }

        this.root = this.document.createElement(rootElement);
        this.document.appendChild(this.root);
    }

    public MultiPartDocumentBuilder(String rootElement) throws AcrolinxException
    {
        this(rootElement, null, null);
    }

    public MultiPartDocumentBuilder addDocumentPart(String partName, String content,
            @Nullable Map<String, String> attributes)
    {
        Element element = this.document.createElement(partName);
        if (attributes != null) {
            logger.debug("Adding attributes to node");
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                Attr attribute = this.document.createAttribute(entry.getKey());
                attribute.setValue(entry.getValue());
                element.setAttributeNode(attribute);
            }
        }
        Text textNode = this.document.createTextNode(content);
        element.appendChild(textNode);
        this.root.appendChild(element);

        return this;
    }

    public MultiPartDocumentBuilder addDocumentNode(String xml, @Nullable String encoding) throws AcrolinxException
    {

        Element node;
        try {
            logger.debug("Encoding specified? " + (encoding == null ? "Not specified" : encoding));
            node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xml.getBytes(encoding == null ? "UTF-8" : encoding))).getDocumentElement();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            logger.debug("Parsing node content failed.");
            throw new AcrolinxException(e);
        }

        Node importedNode = this.document.importNode(node, true);
        this.root.appendChild(importedNode);

        return this;
    }

    public Document build() throws AcrolinxException
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            logger.debug("Applying transformation to XML.");
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
            return new SimpleDocument(writer.getBuffer().toString());
        } catch (TransformerException e) {
            logger.debug("Creating XML string from document failed.");
            throw new AcrolinxException(e);
        }
    }

}
