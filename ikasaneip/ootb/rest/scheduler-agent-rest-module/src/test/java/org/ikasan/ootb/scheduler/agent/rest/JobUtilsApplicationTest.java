package org.ikasan.ootb.scheduler.agent.rest;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.ootb.scheduler.agent.rest.util.JavaUtilsTestHelper;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {JobUtilsApplication.class, MockedUserServiceTestConfigWithConverter.class})
class JobUtilsApplicationTest
{

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;


    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void killPidWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/9")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void killPidNotFound() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void killPidNotFoundForcibly() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void killPid() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid())
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void killPidForcibly() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid()+"?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

}
