package org.ikasan.ootb.scheduler.agent.rest;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ContextInstanceApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
class ContextInstanceApplicationTest {

    @MockBean
    private ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Set<String> contextInstanceIds = getContextInstanceIds();
        for (String contextInstanceId : contextInstanceIds) {
            ContextInstanceCache.instance().remove(contextInstanceId);
        }
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void save_read_only_user_causes_access_denied_exception() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        String content = IOUtils.toString(getClass().getResourceAsStream("/data/job-context-instance-1.json"), StandardCharsets.UTF_8);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/contextInstance/save")
            .content(content)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void save() throws Exception {

        assertEquals(0, getContextInstanceIds().size());

        String content = IOUtils.toString(getClass().getResourceAsStream("/data/job-context-instance-1.json"), StandardCharsets.UTF_8);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/contextInstance/save")
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(this.contextInstanceIdentifierProvisionService).provision(any());
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void remove_read_only_user_causes_access_denied_exception() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/remove?correlationId=COL_ID_1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void remove() throws Exception {
        String CORRELATION_ID = "COL_ID_1";
        assertEquals(0, getContextInstanceIds().size());

        getContextInstanceMap().put(CORRELATION_ID, new ContextInstanceImpl());
        assertEquals(1, getContextInstanceIds().size());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/remove?correlationId=COL_ID_1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        verify(this.contextInstanceIdentifierProvisionService).remove(CORRELATION_ID);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void remove_all_read_only_user_causes_access_denied_exception() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/removeAll")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void removeAll() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/removeAll")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        verify(this.contextInstanceIdentifierProvisionService).removeAll();
    }

    private ConcurrentHashMap<String, ContextInstance> getContextInstanceMap() {
        ConcurrentHashMap<String, ContextInstance> contextInstanceMap
            = (ConcurrentHashMap<String, ContextInstance>) ReflectionTestUtils.getField(ContextInstanceCache.instance(), "contextInstanceMap");
        return contextInstanceMap;
    }

    private Set<String> getContextInstanceIds() {
        ConcurrentHashMap<String, ContextInstance> contextInstanceMap = getContextInstanceMap();
        return contextInstanceMap.keySet();
    }
}
