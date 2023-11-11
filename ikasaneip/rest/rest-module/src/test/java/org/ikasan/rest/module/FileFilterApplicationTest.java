package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.systemevent.SystemEventService;
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

@SpringBootTest(classes = {FileFilterApplication.class, MockedUserServiceTestConfig.class})
class FileFilterApplicationTest
{

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected BaseFileTransferDao baseFileTransferDaoAuto;

    @Mock
    protected BaseFileTransferDao baseFileTransferDao;

    @MockBean
    protected SystemEventService systemEventService;

    @Autowired
    protected FileFilterApplication uat;


    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        uat.setBaseFileTransferDao(baseFileTransferDao);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/search?clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void searchByClientId() throws Exception
    {
        Date creationDate = new Date(1l);
        FileFilter filterEntry = new FileFilter("testClientId", "test.log", creationDate,creationDate,1000 );

        Mockito
            .when(baseFileTransferDao.find(0, 20, null, "testClientId"))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(filterEntry), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/search?clientId=testClientId")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(baseFileTransferDao).find(0, 20, null, "testClientId");
        Mockito.verifyNoMoreInteractions(baseFileTransferDao);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            """
            {"pagedResults":[{\
            "criteria":"test.log","clientId":"testClientId","size":1000\
            }],"firstResultIndex":0,"resultSize":1,"lastResultIndex":1,"lastPage":true}\
            """,
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }


    @Test
    @WithMockUser(authorities = "readonly")
    void testFindByIdWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/?id=111")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verifyNoInteractions(baseFileTransferDao);

        assertEquals(200, result.getResponse().getStatus());


    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void testFindById() throws Exception
    {

        Date creationDate = new Date(1l);
        FileFilter filterEntry = new FileFilter("testClientId", "test.log", creationDate,creationDate,1000 );

        Mockito
            .when(baseFileTransferDao.findById(0))
            .thenReturn(filterEntry);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/?id=0")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(baseFileTransferDao).findById(0);

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"criteria\":\"test.log\",\"clientId\":\"testClientId\",\"lastModified\":1,\"size\":1000}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }

    @Test
    @WithMockUser(authorities = "readonly")
    void deleteByIdWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        Mockito.verifyNoInteractions(baseFileTransferDao);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/filefilter/?id=111")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void deleteById() throws Exception
    {

        Date creationDate = new Date(1l);
        FileFilter filterEntry = new FileFilter("testClientId", "test.log", creationDate,creationDate,1000 );

        Mockito
            .when(baseFileTransferDao.findById(0))
            .thenReturn(filterEntry);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/filefilter/?id=0")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(baseFileTransferDao).findById(0);
        Mockito
            .verify(baseFileTransferDao).delete(filterEntry);
        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testClientId_test.log"),
            Mockito.anyString(),
            Mockito.anyString()
                                                         );

        Mockito
            .verifyNoMoreInteractions(baseFileTransferDao,systemEventService);


        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void testCreate() throws Exception
    {
        Date creationDate = new Date(1l);
        FileFilter filterEntry = new FileFilter("testClientId", "test.log", creationDate, creationDate,1000 );
        filterEntry.setCreatedDateTime(1l);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/filefilter/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("{\"criteria\":\"test.log\",\"clientId\":\"testClientId\",\"createdDateTime\":1,\"lastAccessed\":1,\"lastModified\":1,\"size\":1000}");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(baseFileTransferDao).save(filterEntry);

        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testClientId_test.log"),
            Mockito.anyString(),
            Mockito.anyString()
                                                         );
        Mockito
            .verifyNoMoreInteractions(baseFileTransferDao,systemEventService);

        assertEquals(201, result.getResponse().getStatus());

    }

}
