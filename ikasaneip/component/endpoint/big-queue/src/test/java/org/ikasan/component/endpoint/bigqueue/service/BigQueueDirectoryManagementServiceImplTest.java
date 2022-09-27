package org.ikasan.component.endpoint.bigqueue.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BigQueueDirectoryManagementServiceImplTest {

    @Mock
    private BigQueueManagementService bigQueueManagementService;

    private BigQueueDirectoryManagementService service;

    private String queueDir;

    @Before
    public void setUp() {
        queueDir = "/someDir/" + RandomStringUtils.randomAlphabetic(10);
        service = new BigQueueDirectoryManagementServiceImpl(queueDir);
        ReflectionTestUtils.setField(service, "bigQueueManagementService", bigQueueManagementService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor() {
        new BigQueueDirectoryManagementServiceImpl(null);
    }

    @Test
    public void getQueueDirectory() {
        assertEquals(queueDir, service.getQueueDirectory());
    }

    @Test
    public void size() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.size(queueDir, queueName)).thenReturn(3L);

        assertEquals(3, service.size(queueName));

        verify(bigQueueManagementService).size(queueDir, queueName);
    }

    @Test
    public void peek() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        BigQueueMessage bqm = new BigQueueMessageBuilder<>().build();
        when(bigQueueManagementService.peek(queueDir, queueName)).thenReturn(bqm);

        assertEquals(bqm, service.peek(queueName));

        verify(bigQueueManagementService).peek(queueDir, queueName);
    }

    @Test
    public void getMessages() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        BigQueueMessage bqm = new BigQueueMessageBuilder<>().build();
        when(bigQueueManagementService.getMessages(queueDir, queueName)).thenReturn(List.of(bqm));

        assertEquals(List.of(bqm), service.getMessages(queueName));

        verify(bigQueueManagementService).getMessages(queueDir, queueName);
    }

    @Test
    public void deleteMessage() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String messageId = RandomStringUtils.randomAlphabetic(12);

        service.deleteMessage(queueName, messageId);

        verify(bigQueueManagementService).deleteMessage(queueDir, queueName, messageId);
    }

    @Test
    public void listQueues() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName));

        assertEquals(List.of(queueName), service.listQueues());

        verify(bigQueueManagementService).listQueues(queueDir);
    }

    @Test
    public void deleteQueue() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);

        service.deleteQueue(queueName);

        verify(bigQueueManagementService).deleteQueue(queueDir, queueName);
    }

    @Test
    public void sizeOfAllQueuesWithReturnZeroTrue() throws IOException {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String queueName2 = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName, queueName2));

        when(bigQueueManagementService.size(queueDir, queueName)).thenReturn(3L);
        when(bigQueueManagementService.size(queueDir, queueName2)).thenReturn(0L);

        Map<String, Long> results = service.size(true);

        verify(bigQueueManagementService).listQueues(queueDir);
        verify(bigQueueManagementService).size(queueDir, queueName);
        verify(bigQueueManagementService).size(queueDir, queueName2);

        Assert.assertEquals(2, results.size());
        Assert.assertEquals(3L, results.get(queueName).longValue());
        Assert.assertEquals(0L, results.get(queueName2).longValue());
    }

    @Test
    public void sizeOfAllQueuesWithReturnZeroFalse() throws IOException {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String queueName2 = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName, queueName2));

        when(bigQueueManagementService.size(queueDir, queueName)).thenReturn(3L);
        when(bigQueueManagementService.size(queueDir, queueName2)).thenReturn(0L);

        Map<String, Long> results = service.size(false);

        verify(bigQueueManagementService).listQueues(queueDir);
        verify(bigQueueManagementService).size(queueDir, queueName);
        verify(bigQueueManagementService).size(queueDir, queueName2);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(3L, results.get(queueName).longValue());
    }
}