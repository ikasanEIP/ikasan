package org.ikasan.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.rest.client.dto.FlowDto;
import org.ikasan.rest.client.dto.ModuleDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ModuleControlRestServiceImplTest
{

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private ModuleControlRestServiceImpl uut;

    private String contexBaseUrl;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
        Environment environment = new StandardEnvironment();
        uut = new ModuleControlRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

    }

    @Test
    public void getAllFlowsStatus()
    {

        stubFor(get(urlEqualTo("/rest/moduleControl/test%20Module%20Name"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{\"name\":\"test Module Name\",\"flows\":[{\"name\":\"flow test\",\"state\":\"running\"}]}")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
            ));
        Optional<ModuleDto> result = uut.getFlowStates(contexBaseUrl,"test Module Name");
        assertEquals("test Module Name", result.get().getName());
        assertEquals("flow test", result.get().getFlows().get(0).getName());
        assertEquals("running", result.get().getFlows().get(0).getState());
    }

    @Test
    public void getAllFlowsStatus_Returns404()
    {

        stubFor(get(urlEqualTo("/rest/moduleControl/test%20Module%20Name"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()

                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(404)
            ));
        Optional<ModuleDto> result = uut.getFlowStates(contexBaseUrl,"test Module Name");
        assertEquals(false, result.isPresent());

    }

    @Test
    public void getFlowStatus()
    {

        stubFor(get(urlEqualTo("/rest/moduleControl/test%20Module%20Name/flow%20Test"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{\"name\":\"flow test\",\"state\":\"running\"}")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
            ));
        Optional<FlowDto> result = uut.getFlowState(contexBaseUrl,"test Module Name","flow Test");
        assertEquals("flow test", result.get().getName());
        assertEquals("running", result.get().getState());
    }

    @Test
    public void getFlowStatus_Returns404()
    {

        stubFor(get(urlEqualTo("/rest/moduleControl/test%20Module%20Name/flow%20Test"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()

                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(404)
            ));
        Optional<FlowDto> result = uut.getFlowState(contexBaseUrl,"test Module Name","flow Test");
        assertEquals(false, result.isPresent());

    }

    @Test
    public void updateFlowStatus()
    {

        stubFor(put(urlEqualTo(ModuleControlRestServiceImpl.CHANGE_FLOW_STATE_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"action\":\"start\"}"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
            ));
        boolean result = uut.changeFlowState(contexBaseUrl,"test Module Name","flow Test","start");
        assertEquals(true, result);
    }

    @Test
    public void updateFlowStatus_returns400()
    {

        stubFor(put(urlEqualTo(ModuleControlRestServiceImpl.CHANGE_FLOW_STATE_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"action\":\"T\"}"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(400)
            ));
        boolean result = uut.changeFlowState(contexBaseUrl,"test Module Name","flow Test","T");
        assertEquals(false, result);
    }

    @Test
    public void changeFlowStartup()
    {

        stubFor(put(urlEqualTo(ModuleControlRestServiceImpl.CHANGE_FLOW_STARTUP_MODE_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"startupType\":\"automatic\",\"comment\":null}"))
                    .willReturn(aResponse()
                                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                                    .withStatus(200)
                               ));
        boolean result = uut.changeFlowStartupType(contexBaseUrl,"test Module Name","flow Test","automatic",null);
        assertEquals(true, result);
    }

    @Test
    public void testTimeout() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory();

        httpComponentsClientHttpRequestFactory.setConnectTimeout(1000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(1000);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(1000);

        Environment environment = new StandardEnvironment();
        uut = new ModuleControlRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);

        stubFor(get(urlEqualTo("/rest/moduleControl/test%20Module%20Name"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{\"name\":\"test Module Name\",\"flows\":[{\"name\":\"flow test\",\"state\":\"running\"}]}")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
                .withFixedDelay(2000)
            ));

        Optional<ModuleDto> result = uut.getFlowStates(contexBaseUrl,"test Module Name");

        Assert.assertFalse(result.isPresent());
    }
}
