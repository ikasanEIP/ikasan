package org.ikasan.security.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.FileUtils;
import org.ikasan.security.model.User;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class DashboardUserServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    private DashboardUserServiceImpl uut;

    @Before
    public void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations()
        {{
            oneOf(environment).getProperty(DashboardUserServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false");
            will(returnValue("true"));
            atLeast(2).of(environment).getProperty(DashboardUserServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(DashboardUserServiceImpl.MODULE_NAME_PROPERTY);
            will(returnValue("testModule"));
        }});
        uut = new DashboardUserServiceImpl(environment);

    }

    @Test
    public void authenticate_successful()
    {
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        assertEquals(true, uut.authenticate("admin", "admin"));
    }

    @Test
    public void authenticate_bad_request()
    {
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        assertEquals(false, uut.authenticate("admin", "admin"));
    }

    @Test
    public void loadUserByUsername() throws IOException
    {
        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody(readFile("user.json"))
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        User expected = new User("testUser",null,"test@test.com",true);
        expected.setDepartment("department");
        expected.setFirstName("TestName");
        expected.setSurname("TestSurname");

        User result = uut.loadUserByUsername("admin");
        assertEquals(expected, result);
    }

    @Test
    public void loadUserByUsernameReturns400() throws IOException
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : admin");

        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody("{}")
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        uut.loadUserByUsername("admin");
    }

    @Test
    public void loadUserByUsernameReturns500() throws IOException
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : admin");

        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody("{}")
                .withStatus(500)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        uut.loadUserByUsername("admin");
    }

    private String readFile(String filePath) throws IOException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return FileUtils.readFileToString(file, "UTF-8");

    }
}
