package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.service.ManagementFilterService;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {FilterApplication.class, MockedUserServiceTestConfig.class})
class FilterApplicationTest
{

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected ManagementFilterService managementFilterServiceAuto;

    @Mock
    protected ManagementFilterService managementFilterService;

    @Autowired
    protected FilterApplication filterApplication;


    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        filterApplication.setManagementFilterService(managementFilterService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/search?clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void searchByClientId() throws Exception
    {
        DefaultFilterEntry filterEntry = new DefaultFilterEntry(11221, "test", 1);
        filterEntry.setCreatedDateTime(1l);
        filterEntry.setExpiry(2l);
        Mockito
            .when(managementFilterService.findMessagesByPage(0, 20, "Test", null, null, null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(filterEntry), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/search?clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(managementFilterService).findMessagesByPage(0, 20, "Test", null, null, null);
        Mockito.verifyNoMoreInteractions(managementFilterService);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"criteria\":11221,\"clientId\":\"test\",\"createdDateTime\":1,\"expiry\":2}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void searchByTime() throws Exception
    {
        DefaultFilterEntry filterEntry = new DefaultFilterEntry(11221, "test", 1);
        filterEntry.setCreatedDateTime(1l);
        filterEntry.setExpiry(2l);

        Mockito
            .when(managementFilterService.findMessagesByPage(0, 20, null, null,
                dateTimeConverter.getDate("2000-10-30T00:00:00"), null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(filterEntry), 0, 1))
        ;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/search?fromDateTime=2000-10-30T00:00:00")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(managementFilterService).findMessagesByPage(0, 20, null, null, dateTimeConverter.getDate("2000-10-30T00:00:00"), null);

        Mockito.verifyNoMoreInteractions(managementFilterService);


        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{\"criteria\":11221,\"clientId\":\"test\",\"createdDateTime\":1,\"expiry\":2}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void searchByTimeInvalidDate() throws Exception
    {
        DefaultFilterEntry filterEntry = new DefaultFilterEntry(11221, "test", 1);
        filterEntry.setCreatedDateTime(1l);
        filterEntry.setExpiry(2l);
        Mockito
            .when(managementFilterService.findMessagesByPage(0, 20, null, null, new Date(), null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(filterEntry), 0, 1));


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/search?fromDateTime=2000-10-3AT")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"errorMessage\":\"fromDateTime or untilDateTime has invalid dateTime format not following yyyy-MM-dd'T'HH:mm:ss.\" }",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);


    }

    @Test
    @WithMockUser(authorities = "readonly")
    void testFindByClientIdAndCriteriaWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/?criteria=111&clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verifyNoInteractions(managementFilterService);

        assertEquals(200, result.getResponse().getStatus());


    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void testFindByClientIdAndCriteria() throws Exception
    {
        DefaultFilterEntry filterEntry = new DefaultFilterEntry(11221, "test", 1);
        filterEntry.setCreatedDateTime(1l);
        filterEntry.setExpiry(2l);
        Mockito
            .when(managementFilterService.find(111,"Test"))
            .thenReturn(filterEntry);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filter/?criteria=111&clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(managementFilterService).find(111,"Test");

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"criteria\":11221,\"clientId\":\"test\",\"createdDateTime\":1,\"expiry\":2}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }

    @Test
    @WithMockUser(authorities = "readonly")
    void deleteByClientIdAndCriteriaWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        Mockito.verifyNoInteractions(managementFilterService);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/filter/?criteria=111&clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void deleteByClientIdAndCriteria() throws Exception
    {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/filter/?criteria=111&clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(managementFilterService).delete(111,"Test");

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void testCreate() throws Exception
    {
        DefaultFilterEntry filterEntry = new DefaultFilterEntry(11221, "test", 1);
        filterEntry.setCreatedDateTime(1l);
        filterEntry.setExpiry(2l);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/filter/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("{\"criteria\":11221,\"clientId\":\"test\",\"createdDateTime\":1,\"expiry\":2}");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(managementFilterService).save(filterEntry);

        assertEquals(201, result.getResponse().getStatus());

    }

}
