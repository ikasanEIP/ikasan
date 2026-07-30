package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SystemEventApplication.class, MockedUserServiceTestConfig.class })
public class SystemEventApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockitoBean
    protected SystemEventService systemEventService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithDefaultParameters() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, null, null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"subject\":\"testSubject\",\"action\":\"testAction\",\"actor\":\"testActor\"}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithAllParameters() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Long fromDateTime = 1577836800000L; // 2020-01-01T00:00:00 in epoch milliseconds
        Long toDateTime = 1609459199000L; // 2020-12-31T23:59:59 in epoch milliseconds
        Date fromDate = new Date(fromDateTime);
        Date toDate = new Date(toDateTime);

        Mockito
            .when(systemEventService.listSystemEvents(1, 50, "subject", true, "testSubject", "testAction", fromDate, toDate, "testActor"))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 1, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("pageNumber", "1")
            .param("pageSize", "50")
            .param("orderBy", "subject")
            .param("orderAscending", "true")
            .param("subject", "testSubject")
            .param("action", "testAction")
            .param("actor", "testActor")
            .param("fromDateTime", fromDateTime.toString())
            .param("untilDateTime", toDateTime.toString())
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(1, 50, "subject", true, "testSubject", "testAction", fromDate, toDate, "testActor");
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"subject\":\"testSubject\",\"action\":\"testAction\",\"actor\":\"testActor\"}],\"firstResultIndex\":1,\"resultSize\":1}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchBySubject() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("moduleA-flowA", "Start Flow", timestamp, "admin", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, "moduleA-flowA", null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("subject", "moduleA-flowA")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, "moduleA-flowA", null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"subject\":\"moduleA-flowA\",\"action\":\"Start Flow\",\"actor\":\"admin\"}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchByAction() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "Delete Wiretap", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, "Delete Wiretap", null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("action", "Delete Wiretap")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, null, "Delete Wiretap", null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchByActor() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "john.doe", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, null, null, null, "john.doe"))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("actor", "john.doe")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, null, null, null, null, "john.doe");
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchByDateRange() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Long fromDateTime = 1604016000000L; // 2020-10-30T00:00:00 in epoch milliseconds
        Long toDateTime = 1604188799000L; // 2020-10-31T23:59:59 in epoch milliseconds
        Date fromDate = new Date(fromDateTime);
        Date toDate = new Date(toDateTime);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, null, fromDate, toDate, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("fromDateTime", fromDateTime.toString())
            .param("untilDateTime", toDateTime.toString())
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, null, null, fromDate, toDate, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"subject\":\"testSubject\",\"action\":\"testAction\",\"actor\":\"testActor\"}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithCustomPageSize() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 100, "timestamp", false, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("pageSize", "100")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 100, "timestamp", false, null, null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithCustomOrderBy() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "action", false, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("orderBy", "action")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "action", false, null, null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithAscendingOrder() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", true, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("orderAscending", "true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", true, null, null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithMultipleResults() throws Exception
    {
        Date timestamp1 = new Date(1000000000L);
        Date timestamp2 = new Date(1100000000L);
        Date expiry = new Date(2000000000L);

        SystemEventImpl systemEvent1 = new SystemEventImpl("subject1", "action1", timestamp1, "actor1", expiry);
        SystemEventImpl systemEvent2 = new SystemEventImpl("subject2", "action2", timestamp2, "actor2", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent1, systemEvent2), 0, 2));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, null, null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"subject\":\"subject1\",\"action\":\"action1\",\"actor\":\"actor1\"},{\"subject\":\"subject2\",\"action\":\"action2\",\"actor\":\"actor2\"}],\"firstResultIndex\":0,\"resultSize\":2,\"lastResultIndex\":2,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchWithEmptyResults() throws Exception
    {
        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, "nonexistent", null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(), 0, 0));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .param("subject", "nonexistent")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(systemEventService).listSystemEvents(0, 25, "timestamp", false, "nonexistent", null, null, null, null);
        Mockito.verifyNoMoreInteractions(systemEventService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[],\"firstResultIndex\":0,\"resultSize\":0,\"lastResultIndex\":0,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "ALL")
    public void searchWithAllAuthority() throws Exception
    {
        Date timestamp = new Date(1000000000L);
        Date expiry = new Date(2000000000L);
        SystemEventImpl systemEvent = new SystemEventImpl("testSubject", "testAction", timestamp, "testActor", expiry);

        Mockito
            .when(systemEventService.listSystemEvents(0, 25, "timestamp", false, null, null, null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(systemEvent), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/systemEvent/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }
}
