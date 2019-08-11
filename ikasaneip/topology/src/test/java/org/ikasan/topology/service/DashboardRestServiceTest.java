package org.ikasan.topology.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class DashboardRestServiceTest
{

    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    DashboardRestServiceImpl uut;

    @Before
    public void setup(){

        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port() ;

        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty(DashboardRestService.HARVESTING_ENABLED_PROPERTY,"false");
            will(returnValue("true"));

            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));

            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue(null));

            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue(null));

           oneOf(environment).getProperty(DashboardRestService.MODULE_NAME_PROPERTY);
            will(returnValue("testModule"));

        }});


        uut = new DashboardRestServiceImpl(environment,"/rest/harvest/wiretaps");

        wireMockRule.addMockServiceRequestListener((request,response) ->{
            System.out.println(request);
            System.out.println(response);
        });

    }

    @Test
    public void pushWiretapReturns201()
    {
        WiretapFlowEvent wiretap = new WiretapFlowEvent("testModule","testFlow","testComponent","lifeId",null,1111l,"{event:content,as:json}",222222l);
        List<HarvestEvent> wiretaps = new ArrayList<>();
        wiretaps.add(wiretap);


        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))

            .willReturn(aResponse()
                .withStatus(201)
            ));

        assertEquals(true,uut.publish(wiretaps));


    }

    @Test
    public void pushWiretapReturns400()
    {
        WiretapFlowEvent wiretap = new WiretapFlowEvent("testModule","testFlow","testComponent","lifeId",null,1111l,"{event:content,as:json}",222222l);
        List<HarvestEvent> wiretaps = new ArrayList<>();
        wiretaps.add(wiretap);


        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))

            .willReturn(aResponse()
                .withStatus(400)
            ));

        assertEquals(false,uut.publish(wiretaps));

    }

    @Test
    public void pushWiretapReturns500()
    {
        WiretapFlowEvent wiretap = new WiretapFlowEvent("testModule","testFlow","testComponent","lifeId",null,1111l,"{event:content,as:json}",222222l);
        List<HarvestEvent> wiretaps = new ArrayList<>();
        wiretaps.add(wiretap);


        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))

            .willReturn(aResponse()
                .withStatus(500)
            ));

        assertEquals(false,uut.publish(wiretaps));

    }


}
