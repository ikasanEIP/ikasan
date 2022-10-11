package org.ikasan.component.endpoint.bigqueue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.TestEvent;
import org.ikasan.component.endpoint.bigqueue.serialiser.TestParam;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.Assert.*;

public class BigQueueManagementServiceImplTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String QUEUE_DIR = "./target/queues/";
    private static final String QUEUE_NAME = "test-queue";

    private IBigQueue bigQueue;

    private BigQueueManagementService service;

    class TestBigQueueManagement extends AbstractBigQueueManagementService {

        private IBigQueue testQueue;

        TestBigQueueManagement(IBigQueue bigQueue) {
            testQueue = bigQueue;
        }

        @Override
        public IBigQueue getBigQueue(String queueName) {
            return bigQueue;
        }
    }

    @Before
    public void setUp() throws Exception {
        bigQueue = new BigQueueImpl(QUEUE_DIR, QUEUE_NAME);
        bigQueue.removeAll();
        service = new TestBigQueueManagement(bigQueue);
    }

    @Test
    public void list_queues_returns_queue_names() throws Exception {
        // the setup creates an empty
        List<String> queues = service.listQueues(QUEUE_DIR);
        assertEquals(1, queues.size());
        assertEquals(QUEUE_NAME, queues.get(0));

        // add some more queues
        String rand = randomAlphabetic(10);
        String queueDir = QUEUE_DIR.substring(0, QUEUE_DIR.length() - 1) + "-" + rand + File.separator;
        String queueName = QUEUE_NAME + "-" + rand;

        new BigQueueImpl(queueDir, queueName + "-" + 0);
        new BigQueueImpl(queueDir, queueName + "-" + 1);
        new BigQueueImpl(queueDir, queueName + "-" + 2);

        // list
        queues = service.listQueues(queueDir);

        assertEquals(3, queues.size());
        assertTrue(queues.contains(queueName + "-" + 0));
        assertTrue(queues.contains(queueName + "-" + 1));
        assertTrue(queues.contains(queueName + "-" + 2));
    }

    @Test
    public void delete_queue() throws Exception {
        Path path = Paths.get(QUEUE_DIR + File.separator + QUEUE_NAME);
        assertTrue(Files.exists(path));
        service.deleteQueue(QUEUE_DIR, QUEUE_NAME);
        assertFalse(Files.exists(path));
    }

    @Test
    public void delete_null_queue_should_not_npe() throws Exception {
        service.deleteQueue(null, QUEUE_NAME);
        service.deleteQueue(QUEUE_DIR, null);
    }

    @Test
    public void list_queues_returns_empty_if_unknown_directory() throws Exception {
        List<String> queues = service.listQueues(randomAlphabetic(10));
        assertTrue(queues.isEmpty());

        queues = service.listQueues(null);
        assertTrue(queues.isEmpty());
    }

    @Test
    public void delete_existing_message_id_by_two_threads() throws Exception {
        assertEquals(0, service.size(QUEUE_NAME));
        String messageId = null;
        int numberOfMessages = 1_000_000;
        for (int i = 0; i < numberOfMessages; i++) {
            BigQueueMessage bigQueueMessage = createBigQueueMessage();
            bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage));
            if (i == 500_000) {
                messageId = bigQueueMessage.getMessageId();
            }
        }

        assertEquals(numberOfMessages, service.size(QUEUE_NAME));

        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final CompletionService<String> completionService = new ExecutorCompletionService<String>(pool);
        final List<? extends Callable<String>> callables = Arrays.asList(
            new DeleterCallable(service, QUEUE_DIR, QUEUE_NAME, messageId),
            new DeleterCallable(service, QUEUE_DIR, QUEUE_NAME, messageId)
        );

        for (final Callable<String> callable : callables) {
            completionService.submit(callable);
        }

        pool.shutdown();

        long start = System.currentTimeMillis();
        long thirtySecondsInMillis = 30000;
        try {
            while (!pool.isTerminated()) {
                Thread.sleep(500);
                if (System.currentTimeMillis() > start + thirtySecondsInMillis) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(numberOfMessages - 1, service.size(QUEUE_NAME));
    }

    @Test
    public void delete_existing_message_id_100k_of_messages() throws Exception {
        BigQueueMessage bigQueueMessage;
        String messageId = null;
        int numberOfMessages = 100_000;
        for (int i = 0; i < numberOfMessages; i++) {
            bigQueueMessage = createBigQueueMessage();
            bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage));
            if (i == 50_000) {
                messageId = bigQueueMessage.getMessageId();
            }
        }

        assertEquals(numberOfMessages, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, messageId);

        assertEquals(numberOfMessages - 1, service.size(QUEUE_NAME));
    }

    @Test
    public void delete_existing_queue_existing_message_id_same_id() throws Exception {
        BigQueueMessage bigQueueMessage = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage));
        assertEquals(3, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, bigQueueMessage.getMessageId());
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Ignore //TODO FIX later when we better understand the issue with AbstractBigQueueManagementService.getMessages
    @Test
    public void delete_existing_queue_existing_message_id() throws Exception {
        BigQueueMessage bigQueueMessage1 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage2 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage3 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage3));
        assertEquals(3, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, bigQueueMessage2.getMessageId());
        assertEquals(2, service.size(QUEUE_NAME));

        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertEquals(2, messages.size());
        assertEquals(bigQueueMessage1, messages.get(0));
        assertEquals(bigQueueMessage3, messages.get(1));

        service.deleteMessage(QUEUE_NAME, bigQueueMessage3.getMessageId());
        assertEquals(1, service.size(QUEUE_NAME));
        messages = service.getMessages(QUEUE_NAME);
        assertEquals(1, messages.size());
        assertEquals(bigQueueMessage1, messages.get(0));

        service.deleteMessage(QUEUE_NAME, bigQueueMessage1.getMessageId());
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void delete_existing_queue_unknown_message_id() throws Exception {
        String randomMessageId = randomAlphabetic(10);
        BigQueueMessage bigQueueMessage1 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage2 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage3 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage3));

        // make sure we do not blow up
        service.deleteMessage(QUEUE_NAME, null);
        assertEquals(3, service.size(QUEUE_NAME));

        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertEquals(3, messages.size());
        assertEquals(bigQueueMessage1, messages.get(0));
        assertEquals(bigQueueMessage2, messages.get(1));
        assertEquals(bigQueueMessage3, messages.get(2));

        service.deleteMessage(QUEUE_NAME, randomMessageId);
        assertEquals(3, service.size(QUEUE_NAME));

        messages = service.getMessages(QUEUE_NAME);
        assertEquals(3, messages.size());
        assertEquals(bigQueueMessage1, messages.get(0));
        assertEquals(bigQueueMessage2, messages.get(1));
        assertEquals(bigQueueMessage3, messages.get(2));
    }

    @Test
    public void delete_all_message_queue() throws Exception {
        BigQueueMessage bigQueueMessage1 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage2 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage3 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage3));

        // make sure we do not blow up
        assertEquals(3, service.size(QUEUE_NAME));
        service.deleteAllMessage(QUEUE_NAME);
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void delete_all_message_queue_different_queue_name() throws Exception {
        BigQueueMessage bigQueueMessage1 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage2 = createBigQueueMessage();
        BigQueueMessage bigQueueMessage3 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage3));

        // make sure we do not blow up
        assertEquals(3, service.size(QUEUE_NAME));
        service.deleteAllMessage(QUEUE_NAME + "DOSE_NOT_EXIST");
        assertEquals(3, service.size(QUEUE_NAME));
    }

    @Test
    public void delete_unknown_queue_does_not_npe() throws Exception {
        String randomString = randomAlphabetic(10);
        service.deleteMessage(QUEUE_NAME, randomString);
        validateNoQueueCreated(randomString);

        service.deleteMessage(null, null);
        service.deleteMessage(null, null);
        service.deleteMessage(QUEUE_NAME, null);

    }

    @Test
    public void messages_non_empty_queue_returns_list() throws Exception {
        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertTrue(messages.isEmpty());

        BigQueueMessage queueMessage1 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(queueMessage1));

        messages = service.getMessages(QUEUE_NAME);
        assertEquals(1, messages.size());

        BigQueueMessage queueMessage2 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(queueMessage2));

        messages = service.getMessages(QUEUE_NAME);
        assertEquals(2, messages.size());

        BigQueueMessage queueMessage3 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(queueMessage3));

        messages = service.getMessages(QUEUE_NAME);
        assertEquals(3, messages.size());

        assertEquals(queueMessage1, messages.get(0));
        assertEquals(queueMessage2, messages.get(1));
        assertEquals(queueMessage3, messages.get(2));
    }

    @Test
    public void messages_empty_queue_returns_emptyList() throws Exception {
        assertTrue(service.getMessages(QUEUE_NAME).isEmpty());

        assertTrue(service.getMessages(QUEUE_NAME).isEmpty());
        assertTrue(service.getMessages(null).isEmpty());
        assertTrue(service.getMessages(null).isEmpty());
    }

    @Test
    public void messages_unknown_queue_returns_emptyList() throws Exception {
        String randomString = randomAlphabetic(10);
        assertTrue(service.getMessages(randomString).isEmpty());
        validateNoQueueCreated(randomString);
    }

    @Test
    public void peek_unknown_queue_returns_null() throws Exception {
        String randomString = randomAlphabetic(10);
        assertNull(service.peek(randomString));
        validateNoQueueCreated(randomString);

        assertNull(service.peek(randomString));
        assertNull(service.peek(null));
        assertNull(service.peek(null));
    }

    @Test
    public void peek_empty_queue_returns_null() throws Exception {
        assertNull(service.peek(QUEUE_NAME));
    }

    @Test
    public void peek_non_empty_queue_returns_top_of_queue() throws Exception {
        assertNull(service.peek(QUEUE_NAME));

        BigQueueMessage bigQueueMessage1 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage1));

        BigQueueMessage message = service.peek(QUEUE_NAME);
        assertNotNull(message);
        assertEquals(bigQueueMessage1, message);

        BigQueueMessage bigQueueMessage2 = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage2));

        message = service.peek(QUEUE_NAME);
        assertNotNull(message);
        assertEquals(bigQueueMessage1, message);

        bigQueue.dequeue();

        message = service.peek(QUEUE_NAME);
        assertNotNull(message);
        assertEquals(bigQueueMessage2, message);
    }

    @Test
    public void size_unknown_queue_returns_zero() throws Exception {
        String randomString = randomAlphabetic(10);
        assertEquals(0, service.size(randomString));
        validateNoQueueCreated(randomString);

        assertEquals(0, service.size(randomString));
        assertEquals(0, service.size(null));
        assertEquals(0, service.size(null));
    }

    @Test
    public void size_empty_queue() throws Exception {
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void size_non_empty_queue() throws Exception {
        assertEquals(0, service.size(QUEUE_NAME));

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertEquals(1, service.size(QUEUE_NAME));

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertEquals(2, service.size(QUEUE_NAME));

        bigQueue.dequeue();
        assertEquals(1, service.size(QUEUE_NAME));

        bigQueue.dequeue();
        assertEquals(0, service.size(QUEUE_NAME));

        assertEquals(0, service.size(QUEUE_NAME));
    }

    private void validateNoQueueCreated(String randomString) {
        assertFalse(Files.exists(Paths.get(QUEUE_DIR + File.separator + randomString)));
    }

    private BigQueueMessage createBigQueueMessage() throws JsonProcessingException {
        return new BigQueueMessageBuilder<>()
            .withMessage(OBJECT_MAPPER.writeValueAsString(createTestEvent()))
            .withMessageProperties(Map.of("property1", "value1", "property2", "value2"))
            .build();
    }

    public TestEvent createTestEvent() {
        TestEvent testEvent = new TestEvent();
        testEvent.setSomeValue1("value1");
        testEvent.setSomeValue2("value2");
        List<TestParam> testParams = List.of(new TestParam("paramName1", 11), new TestParam("paramName1", 12));
        testEvent.setParams(testParams);
        return testEvent;
    }

    private class DeleterCallable implements Callable<String> {

        private BigQueueManagementService service;
        private String queueDir;
        private String queueName;
        private String messageId;

        public DeleterCallable(BigQueueManagementService service, String queueDir, String queueName, String messageId) {
            this.service = service;
            this.queueDir = queueDir;
            this.queueName = queueName;
            this.messageId = messageId;
        }

        @Override
        public String call() {
            try {
                service.deleteMessage(queueName, messageId);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return "done";
        }
    }

}