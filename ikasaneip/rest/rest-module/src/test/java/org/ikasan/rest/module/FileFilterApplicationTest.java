package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
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
@SpringBootTest(classes = { FileFilterApplication.class, MockedUserServiceTestConfig.class })
public class FileFilterApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected BaseFileTransferDao baseFileTransferDaoAuto;

    @Mock
    protected BaseFileTransferDao baseFileTransferDao;

    @Autowired
    protected FileFilterApplication uat;


    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        uat.setBaseFileTransferDao(baseFileTransferDao);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/search?clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void searchByClientId() throws Exception
    {
        Date creationDate = new Date(1l);
        FileFilter filterEntry = new FileFilter("testClientId", "test.log", creationDate,creationDate,1000 );

        Mockito
            .when(baseFileTransferDao.find(0, 20, "Test", null))
            .thenReturn(new ArrayListPagedSearchResult<>(Arrays.asList(filterEntry), 0, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/search?clientId=Test")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito
            .verify(baseFileTransferDao).find(0, 20, "Test", null);
        Mockito.verifyNoMoreInteractions(baseFileTransferDao);

        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"pagedResults\":[{"
                + "\"criteria\":\"test.log\",\"clientId\":\"testClientId\",\"size\":1000"
                + "}],\"firstResultIndex\":0,\"resultSize\":1,\"lastResultIndex\":1,\"lastPage\":true}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }


    @Test
    @WithMockUser(authorities = "readonly")
    public void testFindByIdWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/filefilter/?id=111")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verifyZeroInteractions(baseFileTransferDao);

        assertEquals(200, result.getResponse().getStatus());


    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testFindById() throws Exception
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
    public void deleteByIdWithWrongUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        Mockito.verifyZeroInteractions(baseFileTransferDao);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/filefilter/?id=111")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void deleteById() throws Exception
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

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testCreate() throws Exception
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

        assertEquals(201, result.getResponse().getStatus());

    }

}
