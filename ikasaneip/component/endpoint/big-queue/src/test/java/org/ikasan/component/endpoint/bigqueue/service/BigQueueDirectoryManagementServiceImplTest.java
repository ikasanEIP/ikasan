package org.ikasan.component.endpoint.bigqueue.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BigQueueDirectoryManagementServiceImplTest {

    @Mock
    private BigQueueManagementService bigQueueManagementService;

    private BigQueueDirectoryManagementService service;

    private String queueDir;

    @BeforeEach
    void setUp() {
        queueDir = "/someDir/" + RandomStringUtils.randomAlphabetic(10);
        service = new BigQueueDirectoryManagementServiceImpl(bigQueueManagementService, queueDir);
        ReflectionTestUtils.setField(service, "bigQueueManagementService", bigQueueManagementService);
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueDirectoryManagementServiceImpl(null, queueDir);
        });
    }

    @Test
    void getQueueDirectory() {
        assertEquals(queueDir, service.getQueueDirectory());
    }

    @Test
    void size() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.size(queueName)).thenReturn(3L);

        assertEquals(3, service.size(queueName));

        verify(bigQueueManagementService).size(queueName);
    }

    @Test
    void peek() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        BigQueueMessage bqm = new BigQueueMessageBuilder<>().build();
        when(bigQueueManagementService.peek(queueName)).thenReturn(bqm);

        assertEquals(bqm, service.peek(queueName));

        verify(bigQueueManagementService).peek(queueName);
    }

    @Test
    void getMessages() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        BigQueueMessage bqm = new BigQueueMessageBuilder<>().build();
        when(bigQueueManagementService.getMessages(queueName)).thenReturn(List.of(bqm));

        assertEquals(List.of(bqm), service.getMessages(queueName));

        verify(bigQueueManagementService).getMessages(queueName);
    }

    @Test
    void deleteAllMessage() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);

        service.deleteAllMessage(queueName);
        verify(bigQueueManagementService).deleteAllMessage(queueName);
    }

    @Test
    void deleteMessage() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String messageId = RandomStringUtils.randomAlphabetic(12);

        service.deleteMessage(queueName, messageId);

        verify(bigQueueManagementService).deleteMessage(queueName, messageId);
    }

    @Test
    void listQueues() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName));

        assertEquals(List.of(queueName), service.listQueues());

        verify(bigQueueManagementService).listQueues(queueDir);
    }

    @Test
    void deleteQueue() throws Exception {
        String queueName = RandomStringUtils.randomAlphabetic(12);

        service.deleteQueue(queueName);

        verify(bigQueueManagementService).deleteQueue(queueDir, queueName);
    }

    @Test
    void sizeOfAllQueuesWithReturnZeroTrue() throws IOException {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String queueName2 = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName, queueName2));

        when(bigQueueManagementService.size(queueName)).thenReturn(3L);
        when(bigQueueManagementService.size(queueName2)).thenReturn(0L);

        Map<String, Long> results = service.size(true);

        verify(bigQueueManagementService).listQueues(queueDir);
        verify(bigQueueManagementService).size(queueName);
        verify(bigQueueManagementService).size(queueName2);

        assertEquals(2, results.size());
        assertEquals(3L, results.get(queueName).longValue());
        assertEquals(0L, results.get(queueName2).longValue());
    }

    @Test
    void sizeOfAllQueuesWithReturnZeroFalse() throws IOException {
        String queueName = RandomStringUtils.randomAlphabetic(12);
        String queueName2 = RandomStringUtils.randomAlphabetic(12);
        when(bigQueueManagementService.listQueues(queueDir)).thenReturn(List.of(queueName, queueName2));

        when(bigQueueManagementService.size(queueName)).thenReturn(3L);
        when(bigQueueManagementService.size(queueName2)).thenReturn(0L);

        Map<String, Long> results = service.size(false);

        verify(bigQueueManagementService).listQueues(queueDir);
        verify(bigQueueManagementService).size(queueName);
        verify(bigQueueManagementService).size(queueName2);

        assertEquals(1, results.size());
        assertEquals(3L, results.get(queueName).longValue());
    }
}