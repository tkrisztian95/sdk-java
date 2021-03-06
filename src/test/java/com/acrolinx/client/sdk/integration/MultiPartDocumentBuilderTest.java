/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */

package com.acrolinx.client.sdk.integration;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.acrolinx.client.sdk.check.MultiPartDocumentBuilder;
import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import com.acrolinx.client.sdk.integration.common.IntegrationTestBase;

public class MultiPartDocumentBuilderTest extends IntegrationTestBase
{

    @Test
    public void testMultiPartDocumentCreation() throws AcrolinxException
    {
        MultiPartDocumentBuilder multiPartDocument = new MultiPartDocumentBuilder("test");
        multiPartDocument.addDocumentPart("element", "This text contains errorss", null);
        String content = multiPartDocument.build().getContent();

        assertNotNull(content);
    }

    @Test
    public void testMultiPartDocumentCreationWithAttributes() throws AcrolinxException
    {
        MultiPartDocumentBuilder multiPartDocument = new MultiPartDocumentBuilder("test");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attr", "val");
        attributes.put("attr2", "val2");

        multiPartDocument.addDocumentPart("element", "This text contains errorss", attributes);
        String content = multiPartDocument.build().getContent();

        assertNotNull(content);
        assertTrue(content.contains("val2"));
        assertTrue(content.contains("val"));
    }

    @Test
    public void testDoctypeIsSetCorrectly() throws AcrolinxException
    {
        String publicId = "-//Acrolinx/DTD Acrolinx Integration v2//EN";
        String systemId = "https://acrolinx,com/dtd/acrolinx.dtd";
        MultiPartDocumentBuilder multiPartDocument = new MultiPartDocumentBuilder("test", publicId, systemId);

        multiPartDocument.addDocumentPart("element", "This text contains errorss", null);
        String content = multiPartDocument.build().getContent();

        assertNotNull(content);
        assertTrue(content.contains(publicId));
        assertTrue(content.contains(systemId));
    }

    @Test
    public void testAddHTMLPart() throws AcrolinxException
    {
        String publicId = "-//Acrolinx/DTD Acrolinx Integration v2//EN";
        String systemId = "https://acrolinx,com/dtd/acrolinx.dtd";
        MultiPartDocumentBuilder multiPartDocument = new MultiPartDocumentBuilder("test", publicId, systemId);

        multiPartDocument.addDocumentNode(
                "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n" + "\n" + "<h1>A saample acro para</h1>\n" + "\n"
                        + "<p>Another acrooo paraa</p>\n" + "\n" + "</body>\n" + "</html>",
                "UTF-8");
        String content = multiPartDocument.build().getContent();

        assertNotNull(content);
        assertTrue(content.contains("A saample acro para"));
    }

}
