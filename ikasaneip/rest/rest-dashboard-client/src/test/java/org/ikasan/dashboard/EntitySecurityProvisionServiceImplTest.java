package org.ikasan.dashboard;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.security.service.EntitySecurityProvisionServiceException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Unit tests for EntitySecurityProvisionServiceImpl.
 *
 * @author Ikasan Development Team
 */
public class EntitySecurityProvisionServiceImplTest {

    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().dynamicPort());

    EntitySecurityProvisionServiceImpl uut;

    @Before
    public void setup() {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations() {{
            atLeast(2).of(environment).getProperty(EntitySecurityProvisionServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(EntitySecurityProvisionServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue("admin"));
            oneOf(environment).getProperty(EntitySecurityProvisionServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue("admin"));
            oneOf(environment).getProperty(EntitySecurityProvisionServiceImpl.DASHBOARD_REST_USERAGENT);
            will(returnValue("test-user-agent"));
        }});
    }

    @Test
    public void test_assign_module_to_roles_success() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2", "Role3");

        stubFor(post(urlEqualTo("/rest/security/module/roles?moduleName=TestModule"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\",\"Role3\"]"))
            .willReturn(aResponse()
                .withStatus(200)
            ));

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles("TestModule", roleNames);

        verify(postRequestedFor(urlEqualTo("/rest/security/module/roles?moduleName=TestModule")));
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_module_to_roles_null_module_name() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles(null, roleNames);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_module_to_roles_empty_module_name() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles("", roleNames);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_module_to_roles_null_role_names() throws EntitySecurityProvisionServiceException {
        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles("TestModule", null);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_module_to_roles_empty_role_names() throws EntitySecurityProvisionServiceException {
        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles("TestModule", Collections.emptyList());
    }


    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_module_to_roles_server_error() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        stubFor(post(urlEqualTo("/rest/security/module/roles?moduleName=TestModule"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")
            ));

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignModuleToRoles("TestModule", roleNames);
    }

    @Test
    public void test_assign_jobplan_to_roles_success() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        stubFor(post(urlEqualTo("/rest/security/jobplan/roles?jobPlanName=TestJobPlan"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\"]"))
            .willReturn(aResponse()
                .withStatus(200)
            ));

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles("TestJobPlan", roleNames);

        verify(postRequestedFor(urlEqualTo("/rest/security/jobplan/roles?jobPlanName=TestJobPlan")));
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_jobplan_to_roles_null_jobplan_name() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles(null, roleNames);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_jobplan_to_roles_empty_jobplan_name() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles("", roleNames);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_jobplan_to_roles_null_role_names() throws EntitySecurityProvisionServiceException {
        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles("TestJobPlan", null);
    }

    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_jobplan_to_roles_empty_role_names() throws EntitySecurityProvisionServiceException {
        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles("TestJobPlan", Collections.emptyList());
    }


    @Test(expected = EntitySecurityProvisionServiceException.class)
    public void test_assign_jobplan_to_roles_server_error() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1");

        stubFor(post(urlEqualTo("/rest/security/jobplan/roles?jobPlanName=TestJobPlan"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")
            ));

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.assignJobPlanToRoles("TestJobPlan", roleNames);
    }

    @Test
    public void provision_job_plan_roles_followed_by_authentication_and_successful_post() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        stubFor(post(urlEqualTo("/rest/security/jobplan/roles?jobPlanName=AuthJobPlan"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\"]"))
            .willReturn(aResponse()
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("\"username\":\"admin\""))
            .withRequestBody(containing("\"password\":\"admin\""))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));
        stubFor(post(urlEqualTo("/rest/security/jobplan/roles?jobPlanName=AuthJobPlan"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer msamsmsamsmas"))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\"]"))
            .willReturn(aResponse()
                .withStatus(200)
            ));

        uut.assignJobPlanToRoles("AuthJobPlan", roleNames);
    }

    @Test
    public void provision_module_roles_followed_by_authentication_and_successful_post() throws EntitySecurityProvisionServiceException {
        List<String> roleNames = Arrays.asList("Role1", "Role2", "Role3");

        uut = new EntitySecurityProvisionServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        stubFor(post(urlEqualTo("/rest/security/module/roles?moduleName=AuthModule"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\",\"Role3\"]"))
            .willReturn(aResponse()
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("\"username\":\"admin\""))
            .withRequestBody(containing("\"password\":\"admin\""))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(post(urlEqualTo("/rest/security/module/roles?moduleName=AuthModule"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("test-user-agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer msamsmsamsmas"))
            .withRequestBody(equalToJson("[\"Role1\",\"Role2\",\"Role3\"]"))
            .willReturn(aResponse()
                .withStatus(200)
            ));

        uut.assignModuleToRoles("AuthModule", roleNames);
    }
}
