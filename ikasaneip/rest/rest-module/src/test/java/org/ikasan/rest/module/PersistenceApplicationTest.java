package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { PersistenceApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class PersistenceApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected PersistenceApplication persistenceApplication;

    @MockBean
    GeneralDatabaseService generalDatabaseService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void getRowCountWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/persistence/rowCount/test")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testGetRowCount() throws Exception
    {
        Mockito.when(this.generalDatabaseService.getRecordCountForDatabaseTable("TestTableName"))
            .thenReturn(5);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/persistence/rowCount/TestTableName")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",

            "{\"tableName\":\"TestTableName\",\"rowCount\":5}",
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.generalDatabaseService).getRecordCountForDatabaseTable("TestTableName");
        Mockito.verifyNoMoreInteractions(this.generalDatabaseService);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testGetRowCountException() throws Exception
    {
        Mockito.when(this.generalDatabaseService.getRecordCountForDatabaseTable("TestTableName"))
            .thenThrow(new RuntimeException("TestTableName is not valid!"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/persistence/rowCount/TestTableName")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"errorMessage\":\"An error has occurred requesting row count for table [TestTableName]." +
                " Error[TestTableName is not valid!]\"}",
        result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.generalDatabaseService).getRecordCountForDatabaseTable("TestTableName");
        Mockito.verifyNoMoreInteractions(this.generalDatabaseService);

    }
}
