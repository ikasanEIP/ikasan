package org.ikasan.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.rest.client.dto.TriggerDto;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ResubmissionRestServiceImplTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private ResubmissionRestServiceImpl uut;

    private String contexBaseUrl;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
        Environment environment = new StandardEnvironment();
        uut = new ResubmissionRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void resubmit()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(200)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(true, result);
    }

    @Test
    public void resubmit_returns400()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(400)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns404()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(404)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns500()
    {

        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse()
                                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                                    .withStatus(500)
                               ));
        boolean result = uut.resubmit(contexBaseUrl,"test Module Name","flow Test","resubmit","testErrorURI");
        assertEquals(false, result);


    }

    @Test
    public void testTimeout() {
        Environment environment = new StandardEnvironment();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory();

        httpComponentsClientHttpRequestFactory.setConnectTimeout(1000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(1000);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(1000);

        uut = new ResubmissionRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);

        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
            .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(200).withFixedDelay(2000)));

        Assert.assertFalse(uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI"));
    }
}