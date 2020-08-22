package org.ikasan.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ReplayRestServiceImplTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private ReplayRestServiceImpl uut = new ReplayRestServiceImpl();

    private String contexBaseUrl;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
    }

    @Test
    public void replay()
    {
        stubFor(put(urlEqualTo(ReplayRestServiceImpl.REPLAY_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"event\":\"cmVzdWJtaXQ=\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(200)));
        boolean result = uut.replay(contexBaseUrl, null, null, "test Module Name", "flow Test", "resubmit".getBytes());
        assertEquals(true, result);
    }

    @Test
    public void replay_returns400()
    {
        stubFor(put(urlEqualTo(ReplayRestServiceImpl.REPLAY_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"event\":\"cmVzdWJtaXQ=\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(400)));
        boolean result = uut.replay(contexBaseUrl, null, null, "test Module Name", "flow Test", "resubmit".getBytes());
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns404()
    {
        stubFor(put(urlEqualTo(ReplayRestServiceImpl.REPLAY_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"event\":\"cmVzdWJtaXQ=\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(404)));
        boolean result = uut.replay(contexBaseUrl,  null, null,"test Module Name", "flow Test", "resubmit".getBytes());
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns500()
    {

        stubFor(put(urlEqualTo(ReplayRestServiceImpl.REPLAY_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"event\":\"cmVzdWJtaXQ=\"}"))
                    .willReturn(aResponse()
                                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                                    .withStatus(500)
                               ));
        boolean result = uut.replay(contexBaseUrl, null, null,"test Module Name","flow Test","resubmit".getBytes());
        assertEquals(false, result);


    }
}