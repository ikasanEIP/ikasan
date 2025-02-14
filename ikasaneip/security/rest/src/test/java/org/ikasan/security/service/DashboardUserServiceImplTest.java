package org.ikasan.security.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.cache.Cache;
import org.apache.commons.io.FileUtils;
import org.ikasan.security.model.User;
import org.ikasan.security.service.dto.JwtRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class DashboardUserServiceImplTest
{

    private Environment environment = Mockito.mock(Environment.class);

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

        Mockito.when(environment.getProperty(DashboardUserServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"))
        .thenReturn("true");

        Mockito.when(environment.getProperty(DashboardUserServiceImpl.DASHBOARD_BASE_URL_PROPERTY))
               .thenReturn(dashboardBaseUrl);

       Mockito.when(environment.getProperty(DashboardUserServiceImpl.MODULE_NAME_PROPERTY))
               .thenReturn("testModule");

        Mockito.when(environment.containsProperty(DashboardUserServiceImpl.DASHBOARD_AUTHENTICATION_USER_CREDENTIAL_CACHE_TIMEOUT_SECONDS))
            .thenReturn(true);

        Mockito.when(environment.getProperty(DashboardUserServiceImpl.DASHBOARD_AUTHENTICATION_USER_CREDENTIAL_CACHE_TIMEOUT_SECONDS, Integer.class))
            .thenReturn(5);

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
    public void authenticate_successful_cached_user()
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
        assertEquals(true, uut.authenticate("admin", "admin"));

        // Because the user has been cached, the service will only be hit once!
        verify(1, postRequestedFor(urlEqualTo("/authenticate")));
    }

    @Test
    public void authenticate_successful_cached_user_expires() throws InterruptedException {
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        assertEquals(true, uut.authenticate("admin", "admin"));
        assertEquals(true, uut.authenticate("admin", "admin"));

        // We are testing to make sure that the token expires after it is first added to the cache,
        // rather than when it is last accessed.
        Thread.sleep(2000);

        assertEquals(true, uut.authenticate("admin", "admin"));
        assertEquals(true, uut.authenticate("admin", "admin"));

        // Sleep to allow the user cache expire.
        Thread.sleep(5000);

        assertEquals(true, uut.authenticate("admin", "admin"));
        assertEquals(true, uut.authenticate("admin", "admin"));

        // Because the user has been cached, the service will only be hit once either side of the cache expiry!
        verify(2, postRequestedFor(urlEqualTo("/authenticate")));
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
    public void loadUserByUsernameWhenUserIsDisabled() throws IOException
    {
        stubFor(get(urlEqualTo("/rest/user?username=disabledUser"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody(readFile("disabled-user.json"))
                                   .withStatus(200)
                                   .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                       ));

        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Given user: disabledUser is disabled. Contact administrator.");

        uut.loadUserByUsername("disabledUser");

    }

    @Test
    public void loadUserByUsernameReturns400() throws IOException
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : admin");

        ReflectionTestUtils.setField(uut, "token", "token");

        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody("{}")
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        uut.loadUserByUsername("admin");

        Assert.assertNotNull(ReflectionTestUtils.getField(uut, "token"));
    }

    @Test
    public void loadUserByUsernameReturns401() throws IOException
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : admin");

        // Put a token in the cache so we can make sure it is purged.
        Cache<String, JwtRequest> userCredentialCache
            = (Cache<String, JwtRequest>) ReflectionTestUtils.getField(uut, "userCredentialCache");
        userCredentialCache.put("token", new JwtRequest("user", "pass"));
        Assert.assertNotNull(userCredentialCache.getIfPresent("token"));

        ReflectionTestUtils.setField(uut, "token", "token");

        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody("{}")
                .withStatus(401)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        uut.loadUserByUsername("admin");

        Assert.assertNull(ReflectionTestUtils.getField(uut, "token"));
        // Confirm that the cache has been purged of the token.
        Assert.assertNull(userCredentialCache.getIfPresent("token"));
    }

    @Test
    public void loadUserByUsernameReturns500() throws IOException
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : admin");

        ReflectionTestUtils.setField(uut, "token", "token");

        stubFor(get(urlEqualTo("/rest/user?username=admin"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse().withBody("{}")
                .withStatus(500)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        uut.loadUserByUsername("admin");

        Assert.assertNotNull(ReflectionTestUtils.getField(uut, "token"));
    }

    private String readFile(String filePath) throws IOException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return FileUtils.readFileToString(file, "UTF-8");

    }
}
