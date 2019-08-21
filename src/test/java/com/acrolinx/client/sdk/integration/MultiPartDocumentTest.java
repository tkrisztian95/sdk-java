/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */
package com.acrolinx.client.sdk.integration;

import com.acrolinx.client.sdk.check.MultiPartAcrolinxDocument;
import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import com.acrolinx.client.sdk.integration.common.IntegrationTestBase;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class MultiPartDocumentTest extends IntegrationTestBase {

    @Test
    public void testMultiPartDocumentCreation() throws ParserConfigurationException, AcrolinxException {
        MultiPartAcrolinxDocument multiPartAcrolinxDocument = new MultiPartAcrolinxDocument("test");
        multiPartAcrolinxDocument.addDocumentPart("element", "This text contains errorss", null);
        String content = multiPartAcrolinxDocument.getContent();

        assertNotNull(content);
    }

    @Test
    public void testMultiPartDocumentCreationWithAttributes() throws ParserConfigurationException, AcrolinxException {
        MultiPartAcrolinxDocument multiPartAcrolinxDocument = new MultiPartAcrolinxDocument("test");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attr", "val");
        attributes.put("attr2", "val2");

        multiPartAcrolinxDocument.addDocumentPart("element", "This text contains errorss", attributes);
        String content = multiPartAcrolinxDocument.getContent();

        assertNotNull(content);
        assertTrue(content.contains("val2"));
        assertTrue(content.contains("val"));
    }

    @Test
    public void testDoctypeIsSetCorrectly() throws ParserConfigurationException, AcrolinxException {
        String publicId = "-//Acrolinx/DTD Acrolinx Integration v2//EN";
        String systemId = "https://acrolinx,com/dtd/acrolinx.dtd";
        MultiPartAcrolinxDocument multiPartAcrolinxDocument =
                new MultiPartAcrolinxDocument("test", publicId, systemId);

        multiPartAcrolinxDocument.addDocumentPart("element", "This text contains errorss", null);
        String content = multiPartAcrolinxDocument.getContent();

        assertNotNull(content);
        assertTrue(content.contains(publicId));
        assertTrue(content.contains(systemId));
    }

    @Test
    public void testAddHTMLPart() throws ParserConfigurationException, AcrolinxException, IOException, SAXException {
        String publicId = "-//Acrolinx/DTD Acrolinx Integration v2//EN";
        String systemId = "https://acrolinx,com/dtd/acrolinx.dtd";
        MultiPartAcrolinxDocument multiPartAcrolinxDocument =
                new MultiPartAcrolinxDocument("test", publicId, systemId);

        multiPartAcrolinxDocument.addDocumentNode("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>A saample acro para</h1>\n" +
                "\n" +
                "<p>Another acrooo paraa</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        String content = multiPartAcrolinxDocument.getContent();

        assertNotNull(content);
        assertTrue(content.contains("A saample acro para"));
    }


}
