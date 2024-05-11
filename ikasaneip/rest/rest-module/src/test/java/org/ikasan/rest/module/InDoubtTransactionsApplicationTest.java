package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.persistence.model.InDoubtTransactionImpl;
import org.ikasan.spec.persistence.model.InDoubtTransaction;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { InDoubtTransactionsApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class InDoubtTransactionsApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected InDoubtTransactionsApplication inDoubtTransactionsApplication;

    @MockBean
    InDoubtTransactionService inDoubtTransactionService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void getGetAllInDoubtTransactionWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/transaction/inDoubt/all")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testGetAllInDoubtTransactions() throws Exception
    {
        Mockito.when(this.inDoubtTransactionService.getInDoubtTransactions())
            .thenReturn(List.of(createInDoubtTransaction("TRANS_1"), createInDoubtTransaction("TRANS_2")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/transaction/inDoubt/all")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
            [{"transactionName":"TRANS_1","transactionState":"IN_DOUBT"},{"transactionName":"TRANS_2","transactionState":"IN_DOUBT"}]
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).getInDoubtTransactions();
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void getGetInDoubtTransactionWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/transaction/inDoubt/get/transactionName")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testGetInDoubtTransaction() throws Exception
    {
        Mockito.when(this.inDoubtTransactionService.getInDoubtTransaction("TRANS_1"))
            .thenReturn(createInDoubtTransaction("TRANS_1"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/transaction/inDoubt/get/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
            {"transactionName":"TRANS_1","transactionState":"IN_DOUBT"}
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).getInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testGetInDoubtTransactionInvalidTransactionName() throws Exception
    {
        Mockito.when(this.inDoubtTransactionService.getInDoubtTransaction("TRANS_1"))
            .thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/transaction/inDoubt/get/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        Mockito.verify(this.inDoubtTransactionService).getInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void getCommitInDoubtTransactionWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/commit/transactionName")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesCommitInDoubtTransactionSuccess() throws Exception
    {
        Mockito.doNothing().when(this.inDoubtTransactionService).commitInDoubtTransaction("TRANS_1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/commit/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals("Result must equal!",
            "\"Transaction[TRANS_1] successfully committed!\"",
            result.getResponse().getContentAsString());

        Mockito.verify(this.inDoubtTransactionService).commitInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesCommitAllInDoubtTransactionsSuccess() throws Exception
    {
        Mockito.doNothing().when(this.inDoubtTransactionService).commitAllInDoubtTransactions();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/commitAll")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals("Result must equal!",
            "\"All in doubt transactions have been successfully committed!\"",
            result.getResponse().getContentAsString());

        Mockito.verify(this.inDoubtTransactionService).commitAllInDoubtTransactions();
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesCommitAllInDoubtTransactionsException() throws Exception
    {
        Mockito.doThrow(new RuntimeException("Exception committing!")).when(this.inDoubtTransactionService).commitAllInDoubtTransactions();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/commitAll")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("Result must equal!",
            """
            {"errorMessage":"An error has occurred committing all in doubt transactions. Some of the transactions may have committed successfully. Please query the /rest/transaction/inDoubt/all service to determine which transactions are still waiting to be committed. Error[Exception committing!]"}
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).commitAllInDoubtTransactions();
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesCommitInboundTransactionInvalidTransactionNames() throws Exception
    {
        Mockito.doThrow(new RuntimeException("In Doubt Transaction not found!"))
            .when(this.inDoubtTransactionService).commitInDoubtTransaction("TRANS_1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/commit/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
                {"errorMessage":"An error has occurred committing in doubt transaction[TRANS_1]. Error[In Doubt Transaction not found!]"}
                """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).commitInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void getRollBackInDoubtTransactionWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/rollback/transactionName")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesRollbackInDoubtTransactionSuccess() throws Exception
    {
        Mockito.doNothing().when(this.inDoubtTransactionService).rollbackInDoubtTransaction("TRANS_1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/rollback/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assert.assertEquals("JSON Result must equal!",
            "\"Transaction[TRANS_1] successfully rolled back!\"",
            result.getResponse().getContentAsString());

        Mockito.verify(this.inDoubtTransactionService).rollbackInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testRollbackAllInDoubtTransactionsSuccess() throws Exception
    {
        Mockito.doNothing().when(this.inDoubtTransactionService).rollbackAllInDoubtTransactions();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/rollbackAll")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals("Result must equal!",
            "\"All in doubt transactions have been successfully rolled back!\"",
            result.getResponse().getContentAsString());

        Mockito.verify(this.inDoubtTransactionService).rollbackAllInDoubtTransactions();
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testRollbackAllInDoubtTransactionsException() throws Exception
    {
        Mockito.doThrow(new RuntimeException("Error rolling back!"))
            .when(this.inDoubtTransactionService).rollbackAllInDoubtTransactions();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/rollbackAll")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("Result must equal!",
            """
                {"errorMessage":"An error has occurred rolling back all in doubt transactions. Some of the transactions may have rolled back successfully. Please query the /rest/transaction/inDoubt/all service to determine which transactions are still waiting to be rolled back. Error[Error rolling back!]"}
                """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).rollbackAllInDoubtTransactions();
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void tesRollbackInDoubtTransactionInvalidTransactionNames() throws Exception
    {
        Mockito.doThrow(new RuntimeException("In Doubt Transaction not found!"))
            .when(this.inDoubtTransactionService).rollbackInDoubtTransaction("TRANS_1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/transaction/inDoubt/rollback/TRANS_1")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
                {"errorMessage":"An error has occurred rolling back in doubt transaction[TRANS_1]. Error[In Doubt Transaction not found!]"}
                """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

        Mockito.verify(this.inDoubtTransactionService).rollbackInDoubtTransaction("TRANS_1");
        Mockito.verifyNoMoreInteractions(this.inDoubtTransactionService);
    }

    private InDoubtTransaction createInDoubtTransaction(String transactionName) {
        InDoubtTransactionImpl inDoubtTransaction = new InDoubtTransactionImpl();
        inDoubtTransaction.setTransactionName(transactionName);
        inDoubtTransaction.setTransactionState("IN_DOUBT");

        return inDoubtTransaction;
    }


}
