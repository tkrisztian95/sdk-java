/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.acrolinx.client.sdk;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class AcrolinxEndPointTest {
    @Test public void testFetchingPlatformInformation() {
        AcrolinxEndpoint endpoint = null;
        try {
            endpoint = new AcrolinxEndpoint("clientSignature", "https://unstable.acrolinx.com",
                    "1.0", "en");

            PlatformInformation platformInformation = endpoint.getPlatformInformation();
            assertTrue("testMethod", true );
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }


    }
}
