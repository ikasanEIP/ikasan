package org.ikasan.security.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.commons.io.FileUtils;
import org.ikasan.security.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardUserServiceImplTest
{

    private Environment environment = Mockito.mock(Environment.class);

    @RegisterExtension
    public WireMockExtension wireMockRule = WireMockExtension.newInstance().options(WireMockConfiguration.options().dynamicPort()).build(); // No-args constructor defaults to port 8080

    private DashboardUserServiceImpl uut;

    @BeforeEach
    void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();

        Mockito.when(environment.getProperty(DashboardUserServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"))
        .thenReturn("true");

        Mockito.when(environment.getProperty(DashboardUserServiceImpl.DASHBOARD_BASE_URL_PROPERTY))
               .thenReturn(dashboardBaseUrl);

       Mockito.when(environment.getProperty(DashboardUserServiceImpl.MODULE_NAME_PROPERTY))
               .thenReturn("testModule");

        uut = new DashboardUserServiceImpl(environment);

    }

    @Test
    void authenticate_successful()
    {
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        assertTrue(uut.authenticate("admin", "admin"));
    }

    @Test
    void authenticate_bad_request()
    {
        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        assertFalse(uut.authenticate("admin", "admin"));
    }

    @Test
    void loadUserByUsername() throws IOException
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
    void loadUserByUsernameWhenUserIsDisabled() throws IOException
    {
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {
            stubFor(get(urlEqualTo("/rest/user?username=disabledUser"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse().withBody(readFile("disabled-user.json"))
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                ));

            uut.loadUserByUsername("disabledUser");

        });
        assertTrue(exception.getMessage().contains("Given user: disabledUser is disabled. Contact administrator."));

    }

    @Test
    void loadUserByUsernameReturns400() throws IOException
    {
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {

            stubFor(get(urlEqualTo("/rest/user?username=admin"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse().withBody("{}")
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                ));
            uut.loadUserByUsername("admin");
        });
        assertTrue(exception.getMessage().contains("Unknown username : admin"));
    }

    @Test
    void loadUserByUsernameReturns500() throws IOException
    {
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {

            stubFor(get(urlEqualTo("/rest/user?username=admin"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse().withBody("{}")
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                ));
            uut.loadUserByUsername("admin");
        });
        assertTrue(exception.getMessage().contains("Unknown username : admin"));
    }

    private String readFile(String filePath) throws IOException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return FileUtils.readFileToString(file, "UTF-8");

    }
}
