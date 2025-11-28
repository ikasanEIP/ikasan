package org.ikasan.ootb.scheduler.agent.rest;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ContextInstanceApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
public class ContextInstanceApplicationTest {

    @MockBean
    private ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Set<String> contextInstanceIds = getContextInstanceIds();
        for (String contextInstanceId : contextInstanceIds) {
            ContextInstanceCache.instance().remove(contextInstanceId);
        }
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void save_read_only_user_causes_access_denied_exception() throws Exception {
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
    public void save() throws Exception {

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
    public void remove_read_only_user_causes_access_denied_exception() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/remove?correlationId=COL_ID_1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void remove() throws Exception {
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
    public void remove_all_read_only_user_causes_access_denied_exception() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/removeAll")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void removeAll() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/contextInstance/removeAll")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        verify(this.contextInstanceIdentifierProvisionService).removeAll();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void context_instance_cache_id_not_present() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/rest/contextInstance/cache/004bc348-472f-4e62-bbf8-f84c1baa224a")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void context_instance_cache_id_present() throws Exception {
        ContextInstanceCache.instance().put("004bc348-472f-4e62-bbf8-f84c1baa224a"
            , new ContextInstanceImpl());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/rest/contextInstance/cache/004bc348-472f-4e62-bbf8-f84c1baa224a")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("{\"contexts\":[],\"scheduledJobs\":[],\"jobLocks\":[],\"customWeekDayOfMonth\":false," +
            "\"treeViewExpandLevel\":1,\"ableToRunConcurrently\":false,\"useDisplayName\":false,\"ordinal\":-1," +
            "\"renderLogicalBoundaries\":true,\"useAutoLayout\":true,\"endJobPlanUponCompletion\":false,\"createdDateTime\":0" +
            ",\"updatedDateTime\":0,\"startTime\":0,\"projectedEndTime\":0,\"endTime\":0,\"containsRepeatingJobs\":false," +
            "\"runContextUntilManuallyEnded\":false,\"contextTtlMilliseconds\":0,\"quartzScheduleDrivenJobsDisabledForContext\"" +
            ":false,\"errorAcknowledged\":false}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void context_instance_cache_empty_no_identifiers() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/rest/contextInstance/cache/identifiers")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void context_instance_cache_empty_with_identifiers() throws Exception {
        ContextInstanceCache.instance().put("004bc348-472f-4e62-bbf8-f84c1baa224a"
            , new ContextInstanceImpl());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/rest/contextInstance/cache/identifiers")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("[\"004bc348-472f-4e62-bbf8-f84c1baa224a\"]"
            , result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
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
