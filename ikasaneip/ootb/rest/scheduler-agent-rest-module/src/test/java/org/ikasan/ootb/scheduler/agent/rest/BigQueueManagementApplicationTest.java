package org.ikasan.ootb.scheduler.agent.rest;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {"big.queue.consumer.queueDir=/opt/some/dir/workingQueueDirectory"})
@SpringBootTest(classes = {BigQueueManagementApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
public class BigQueueManagementApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @MockBean
    protected BigQueueManagementService bigQueueManagementService;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void size_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_web_admin() throws Exception {
        when(bigQueueManagementService.size("queueDir", "queueName")).thenReturn(1L);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("1", result.getResponse().getContentAsString());

        verify(bigQueueManagementService).size("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_web_admin_errors() throws Exception {
        when(bigQueueManagementService.size("queueDir", "queueName")).thenThrow(new RuntimeException("Expected"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).size("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }


    @Test
    @WithMockUser(authorities = "readonly")
    public void peek_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin() throws Exception {
        BigQueueMessage bigQueueMessage = new BigQueueMessageBuilder()
            .withMessageId("uuidAsMessageId")
            .withCreatedTime(1657509967)
            .withMessage("some message").build();
        when(bigQueueManagementService.peek("queueDir", "queueName")).thenReturn(bigQueueMessage);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"messageId\":\"uuidAsMessageId\",\"createdTime\":1657509967,\"message\":\"some message\"}",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).peek("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin_null_response() throws Exception {
        when(bigQueueManagementService.peek("queueDir", "queueName")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).peek("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin_error_response() throws Exception {
        when(bigQueueManagementService.peek("queueDir", "queueName")).thenThrow(new RuntimeException("Expected"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).peek("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void messages_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void messages_web_admin() throws Exception {
        BigQueueMessage bigQueueMessage1 = new BigQueueMessageBuilder()
            .withMessageId("uuidAsMessageId1")
            .withCreatedTime(1657509967)
            .withMessage("some message 1").build();

        BigQueueMessage bigQueueMessage2 = new BigQueueMessageBuilder()
            .withMessageId("uuidAsMessageId1")
            .withCreatedTime(1657509960)
            .withMessage("some message 2").build();

        when(bigQueueManagementService.getMessages("queueDir", "queueName")).thenReturn(List.of(bigQueueMessage1, bigQueueMessage2));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("[{\"messageId\":\"uuidAsMessageId1\",\"createdTime\":1657509967,\"message\":\"some message 1\"},{\"messageId\":\"uuidAsMessageId1\",\"createdTime\":1657509960,\"message\":\"some message 2\"}]",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).getMessages("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void messages_web_admin_null_response() throws Exception {
        when(bigQueueManagementService.getMessages("queueDir", "queueName")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).getMessages("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void messages_web_admin_error_response() throws Exception {
        when(bigQueueManagementService.getMessages("queueDir", "queueName")).thenThrow(new RuntimeException("Expected"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).getMessages("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void delete_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        verify(bigQueueManagementService).deleteMessage("queueDir", "queueName", "messageId");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_admin_error() throws Exception {
        doThrow(new RuntimeException("Expected")).when(bigQueueManagementService).deleteMessage("queueDir", "queueName", "messageId");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).deleteMessage("queueDir", "queueName", "messageId");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void delete_queue_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_queue_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        verify(bigQueueManagementService).deleteQueue("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_queue_admin_errors() throws Exception {
        doThrow(new RuntimeException("Expected")).when(bigQueueManagementService).deleteQueue("queueDir", "queueName");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueDir/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).deleteQueue("queueDir", "queueName");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void get_queue_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queue_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("\"/opt/some/dir/workingQueueDirectory\"",
            result.getResponse().getContentAsString());

        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void get_queues_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/queueDir")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin() throws Exception {
        when(bigQueueManagementService.listQueues("queueDir")).thenReturn(List.of("queueName1", "queueName2"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/queueDir")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("[\"queueName1\",\"queueName2\"]",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).listQueues("queueDir");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin_null() throws Exception {
        when(bigQueueManagementService.listQueues("queueDir")).thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/queueDir")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueManagementService).listQueues("queueDir");
        verifyNoMoreInteractions(bigQueueManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin_errors() throws Exception {
        when(bigQueueManagementService.listQueues("queueDir")).thenThrow(new RuntimeException("Expected"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/queueDir")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueManagementService).listQueues("queueDir");
        verifyNoMoreInteractions(bigQueueManagementService);
    }
}