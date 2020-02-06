/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */

package com.acrolinx.client.sdk.wiremock.common;

import static com.acrolinx.client.sdk.testutils.TestConstants.DEVELOPMENT_SIGNATURE;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.net.URI;
import java.net.URISyntaxException;

import com.acrolinx.client.sdk.AcrolinxEndpoint;
import com.acrolinx.client.sdk.internal.JsonUtils;
import com.acrolinx.client.sdk.internal.SuccessResponse;

public class WireMockUtils
{
    public static final int PLATFORM_PORT_MOCKED = 8089;
    public static final String acrolinxUrl = "http://localhost:" + PLATFORM_PORT_MOCKED;
    public static final String API_PATH_PREFIX = "/api/v1/";

    public static AcrolinxEndpoint createTestAcrolinxEndpointMocked() throws URISyntaxException
    {
        return new AcrolinxEndpoint(new URI(acrolinxUrl), DEVELOPMENT_SIGNATURE, "1.2.3.4", "en");
    }

    public static <T> void mockSuccessResponse(String path, T data)
    {
        mockSuccessResponseWithDelay(path, data, 0);
    }

    public static void httpClientMockTimeOut(String path)
    {
        httpClientMockTimeOutwithDelay(path, 5000);
    }

    public static void httpClientMockTimeOutwithDelay(String path, int delayMs)
    {
        stubFor(get(urlEqualTo(path)).willReturn(
                aResponse().withStatus(408).withHeader("Content-Type", "application/json").withBody("{\n"
                        + "\"statusCode\": 408,\n" + "\"description\": \"Request Timed Out\"\n" + "}").withFixedDelay(
                                delayMs)));
    }

    public static void httpClientMockSuccess(String path)
    {
        httpClientMockSuccesswithDelay(path, 0);
    }

    public static void httpClientMockSuccesswithDelay(String path, int delayMs)
    {
        stubFor(get(urlEqualTo(path)).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\n"
                        + "\"statusCode\": 200,\n" + "\"description\": \"Request Timed Out\"\n" + "}").withFixedDelay(
                                delayMs)));
    }

    public static void httpClientMockNotFoundResponse(String path)
    {
        httpClientMockNotFoundResponsewithDelay(path, 0);
    }

    public static void httpClientMockNotFoundResponsewithDelay(String path, int delayMs)
    {
        stubFor(get(urlEqualTo(path)).willReturn(
                aResponse().withStatus(404).withHeader("Content-Type", "application/json").withBody(
                        "{\n" + "\"statusCode\": 404,\n" + "\"description\": \"Not Found\"\n" + "}").withFixedDelay(
                                delayMs)));
    }

    public static <T> void mockSuccessResponseWithDelay(String path, T data, int delayMs)
    {
        stubFor(get(urlEqualTo(API_PATH_PREFIX + path)).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(
                        JsonUtils.toJson(new SuccessResponse<T>(data))).withFixedDelay(delayMs)));
    }

    public static <T> void mockPostSuccessResponseWithDelay(String path, T data, int delayMs)
    {
        stubFor(post(urlEqualTo(API_PATH_PREFIX + path)).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(
                        JsonUtils.toJson(new SuccessResponse<T>(data))).withFixedDelay(delayMs)));
    }

    public static <T> void mockPostResponse(String path, T response)
    {
        stubFor(post(urlEqualTo(API_PATH_PREFIX + path)).willReturn(okJson(JsonUtils.toJson(response))));
    }

    public static <T> void mockSuccessResponseInScenario(String path, T successData, String scenario,
            String requiredState)
    {
        stubFor(get(urlEqualTo(API_PATH_PREFIX + path)).inScenario(scenario).whenScenarioStateIs(
                requiredState).willReturn(okJson(JsonUtils.toJson(new SuccessResponse<T>(successData)))));
    }

    public static <T> void mockGetResponseInScenario(String path, T response, String scenario, String requiredState,
            String nextState)
    {
        stubFor(get(urlEqualTo(API_PATH_PREFIX + path)).inScenario(scenario).whenScenarioStateIs(
                requiredState).willSetStateTo(nextState).willReturn(okJson(JsonUtils.toJson(response))));
    }

    public static String mockUrlOfApiPath(String apiPath)
    {
        return acrolinxUrl + API_PATH_PREFIX + apiPath;
    }
}
