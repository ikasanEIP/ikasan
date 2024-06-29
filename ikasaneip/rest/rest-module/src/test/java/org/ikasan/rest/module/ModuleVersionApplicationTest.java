package org.ikasan.rest.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.IkasanVersion;
import org.ikasan.module.ApplicationContextProvider;
import org.ikasan.rest.module.dto.BuildPropertiesDto;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ModuleVersionApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class ModuleVersionApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;


    @Autowired
    protected ModuleVersionApplication moduleVersionApplication;

    @MockBean
    protected ApplicationContext applicationContext;

    @MockBean
    protected BuildProperties buildProperties;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp()
    {
        ApplicationContextProvider.init(this.applicationContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/module/version/info")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleVersion() throws Exception
    {
        Mockito
            .when(applicationContext.containsBean("buildProperties"))
            .thenReturn(true);

        Mockito
            .when(applicationContext.getBean("buildProperties"))
            .thenReturn(buildProperties);

        Mockito
            .when(buildProperties.getVersion())
            .thenReturn("version");
        Mockito
            .when(buildProperties.getName())
            .thenReturn("name");
        Mockito
            .when(buildProperties.getArtifact())
            .thenReturn("artifact");
        Mockito
            .when(buildProperties.getGroup())
            .thenReturn("group");
        Mockito
            .when(buildProperties.getTime())
            .thenReturn(Instant.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/module/version/info");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        Assert.assertNotNull(result.getResponse().getContentAsString());

        BuildPropertiesDto buildPropertiesDto =
            this.mapper.readValue(result.getResponse().getContentAsString(), BuildPropertiesDto.class);

        Assert.assertNotNull(buildPropertiesDto);
        Assert.assertEquals("version", buildPropertiesDto.getVersion());
        Assert.assertEquals("name", buildPropertiesDto.getName());
        Assert.assertEquals("group", buildPropertiesDto.getGroup());
        Assert.assertEquals("artifact", buildPropertiesDto.getArtifact());
        Assert.assertEquals(IkasanVersion.getVersion(), buildPropertiesDto.getIkasanVersion());
        Assert.assertTrue(buildPropertiesDto.getBuildTimestamp() > 0);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleVersionBuildPropertiesNotFound() throws Exception
    {
        Mockito
            .when(applicationContext.containsBean("buildProperties"))
            .thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/module/version/info");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        Assert.assertNotNull(result.getResponse().getContentAsString());
        Assert.assertEquals("{\"errorMessage\":\"Spring build properties are not available within the application " +
            "context of your Ikasan integration module. Please include goal <goal>build-info</goal> in the spring-boot-maven-plugin" +
            " goals within your module jar pom.xml.\"}", result.getResponse().getContentAsString());

    }
}
