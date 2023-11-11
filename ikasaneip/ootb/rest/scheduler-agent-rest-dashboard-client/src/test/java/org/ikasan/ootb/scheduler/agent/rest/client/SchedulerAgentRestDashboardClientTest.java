package org.ikasan.ootb.scheduler.agent.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.ootb.scheduler.agent.rest.client.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulerAgentRestDashboardClientTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @RegisterExtension
    public WireMockExtension wireMockRule = WireMockExtension.newInstance().options(WireMockConfiguration.options().dynamicPort()).build(); // No-args constructor defaults to port 8080

    DashboardRestServiceImpl uut;

    @BeforeEach
    void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations()
        {{
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false");
            will(returnValue("true"));
            atLeast(2).of(environment).getProperty(DashboardRestService.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DashboardRestService.MODULE_NAME_PROPERTY);
            will(returnValue("testModule"));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_EXTRACT_EXCEPTIONS_PROPERTY, "false");
            will(returnValue("false"));
        }});
    }

    @Test
    void puush_events_returns_201() {
        uut = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/harvest/scheduled");
        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setAgentName("blah");

        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();
        contextualisedScheduledProcessEvents.add(event);
        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(201)
            ));
        assertTrue(uut.publish(contextualisedScheduledProcessEvents));
    }

    @Test
    void push_events_401_Followed_by_authentication_and_successful_evens_push()
    {
        uut = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/harvest/scheduled");

        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setAgentName("blah");

        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();
        contextualisedScheduledProcessEvents.add(event);

        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations()
        {{
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false");
            will(returnValue("true"));
            atLeast(2).of(environment).getProperty(DashboardRestService.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue("admin"));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue("admin"));
            oneOf(environment).getProperty(DashboardRestService.MODULE_NAME_PROPERTY);
            will(returnValue("testModule"));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_EXTRACT_EXCEPTIONS_PROPERTY, "false");
            will(returnValue("false"));
        }});
        uut = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/harvest/scheduled");
        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(401)
            ));
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer msamsmsamsmas"))
            .willReturn(aResponse()
                .withStatus(201)
            ));
        assertTrue(uut.publish(contextualisedScheduledProcessEvents));
    }

    @Test
    void pushWiretapReturns400()
    {
        uut = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/harvest/scheduled");

        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setAgentName("blah");

        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();
        contextualisedScheduledProcessEvents.add(event);

        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(400)
            ));
        assertFalse(uut.publish(contextualisedScheduledProcessEvents));
    }

    @Test
    void pushWiretapReturns500()
    {
        uut = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/harvest/scheduled");

        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setAgentName("blah");

        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();
        contextualisedScheduledProcessEvents.add(event);
        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(500)
            ));
        assertFalse(uut.publish(contextualisedScheduledProcessEvents));
    }

    @Test
    void testTimeout()
    {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory();

        httpComponentsClientHttpRequestFactory.setReadTimeout(1000);

        uut = new DashboardRestServiceImpl(new StandardEnvironment(), httpComponentsClientHttpRequestFactory, "/rest/harvest/scheduled");

        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setAgentName("blah");

        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();
        contextualisedScheduledProcessEvents.add(event);
        stubFor(put(urlEqualTo("/rest/harvest/scheduled"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(201)
                .withFixedDelay(2000)
            ));
        assertFalse(uut.publish(contextualisedScheduledProcessEvents));
    }
}
