package org.ikasan.ootb.scheduler.agent.rest;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {BigQueueManagementApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
public class BigQueueManagementApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @MockBean
    protected BigQueueDirectoryManagementService bigQueueDirectoryManagementService;

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

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_web_admin() throws Exception {
        when(bigQueueDirectoryManagementService.size("queueName")).thenReturn(1L);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("1", result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).size("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_web_admin_errors() throws Exception {
        when(bigQueueDirectoryManagementService.size("queueName")).thenThrow(new RuntimeException("Expected"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).size("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void peek_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin() throws Exception {
        BigQueueMessage bigQueueMessage = new BigQueueMessageBuilder()
            .withMessageId("uuidAsMessageId")
            .withCreatedTime(1657509967)
            .withMessage("some message").build();
        when(bigQueueDirectoryManagementService.peek("queueName")).thenReturn(bigQueueMessage);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"messageId\":\"uuidAsMessageId\",\"createdTime\":1657509967,\"message\":\"some message\"}",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).peek("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin_null_response() throws Exception {
        when(bigQueueDirectoryManagementService.peek("queueName")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).peek("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void peek_web_admin_error_response() throws Exception {
        when(bigQueueDirectoryManagementService.peek("queueName")).thenThrow(new RuntimeException("Expected"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/peek/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).peek("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void messages_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
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

        when(bigQueueDirectoryManagementService.getMessages("queueName")).thenReturn(List.of(bigQueueMessage1, bigQueueMessage2));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("[{\"messageId\":\"uuidAsMessageId1\",\"createdTime\":1657509967,\"message\":\"some message 1\"},{\"messageId\":\"uuidAsMessageId1\",\"createdTime\":1657509960,\"message\":\"some message 2\"}]",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).getMessages("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void messages_web_admin_null_response() throws Exception {
        when(bigQueueDirectoryManagementService.getMessages("queueName")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).getMessages("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void messages_web_admin_error_response() throws Exception {
        when(bigQueueDirectoryManagementService.getMessages("queueName")).thenThrow(new RuntimeException("Expected"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/messages/queueName")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).getMessages("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void delete_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).deleteMessage("queueName", "messageId");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_admin_error() throws Exception {
        doThrow(new RuntimeException("Expected")).when(bigQueueDirectoryManagementService).deleteMessage("queueName", "messageId");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName/messageId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).deleteMessage("queueName", "messageId");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void delete_queue_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_queue_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).deleteQueue("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delete_queue_admin_errors() throws Exception {
        doThrow(new RuntimeException("Expected")).when(bigQueueDirectoryManagementService).deleteQueue("queueName");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/big/queue/delete/queueName")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).deleteQueue("queueName");
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void get_queues_read_only_user() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin() throws Exception {
        when(bigQueueDirectoryManagementService.listQueues()).thenReturn(List.of("queueName1", "queueName2"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("[\"queueName1\",\"queueName2\"]",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).listQueues();
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin_null() throws Exception {
        when(bigQueueDirectoryManagementService.listQueues()).thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("",
            result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).listQueues();
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void get_queues_admin_errors() throws Exception {
        when(bigQueueDirectoryManagementService.listQueues()).thenThrow(new RuntimeException("Expected"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).listQueues();
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_all_web_admin() throws Exception {

        Map<String, Long> withZeroSize = new HashMap<>();
        withZeroSize.put("queue1", 0L);
        withZeroSize.put("queue2", 3L);
        withZeroSize.put("queue3", 0L);
        withZeroSize.put("queue4", 4L);

        when(bigQueueDirectoryManagementService.size(true)).thenReturn(withZeroSize);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"queue1\":0,\"queue2\":3,\"queue3\":0,\"queue4\":4}", result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).size(true);
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_all_web_admin_dont_include_zeros() throws Exception {

        Map<String, Long> withZeroSize = new HashMap<>();
        withZeroSize.put("queue2", 3L);
        withZeroSize.put("queue4", 4L);

        when(bigQueueDirectoryManagementService.size(false)).thenReturn(withZeroSize);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size?includeZeros=false")
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"queue2\":3,\"queue4\":4}", result.getResponse().getContentAsString());

        verify(bigQueueDirectoryManagementService).size(false);
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void size_all_web_read_only() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(bigQueueDirectoryManagementService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void size_all_web_admin_errors() throws Exception {
        when(bigQueueDirectoryManagementService.size(true)).thenThrow(new RuntimeException("Expected"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/big/queue/size")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        verify(bigQueueDirectoryManagementService).size(true);
        verifyNoMoreInteractions(bigQueueDirectoryManagementService);
    }

}