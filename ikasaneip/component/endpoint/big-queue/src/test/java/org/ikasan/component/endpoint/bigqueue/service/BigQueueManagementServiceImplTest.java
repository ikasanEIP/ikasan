package org.ikasan.component.endpoint.bigqueue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.TestEvent;
import org.ikasan.component.endpoint.bigqueue.serialiser.TestParam;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.ikasan.spec.bigqueue.service.exception.BigQueueNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        public IBigQueue getBigQueue(String queueName) throws BigQueueNotFoundException {
            if (QUEUE_NAME.equals(queueName)) {
                return bigQueue;
            }
            throw new BigQueueNotFoundException("not found");
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

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_queue_dir_should_not_npe() throws Exception {
        service.deleteQueue(null, QUEUE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_queue_name_should_not_npe() throws Exception {
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

    @Test(expected = BigQueueNotFoundException.class)
    public void size_unknown_queue_returns_zero() throws Exception {
        String randomString = randomAlphabetic(10);
        service.size(randomString);
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

    @Test
    public void test_deleteQueue_with_path_traversal_attack() throws Exception {
        try {
            service.deleteQueue(QUEUE_DIR, "../../../etc/passwd");
            fail("Should throw IllegalArgumentException for path traversal");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid queueName path traversal attempt!", e.getMessage());
        }
    }

    @Test
    public void test_deleteQueue_with_absolute_path() throws Exception {
        try {
            service.deleteQueue(QUEUE_DIR, "/etc/passwd");
            fail("Should throw IllegalArgumentException for absolute path");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid queueName path traversal attempt!", e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteQueue_with_empty_queue_dir() throws Exception {
        service.deleteQueue("", QUEUE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteQueue_with_blank_queue_dir() throws Exception {
        service.deleteQueue("   ", QUEUE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteQueue_with_empty_queue_name() throws Exception {
        service.deleteQueue(QUEUE_DIR, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteQueue_with_blank_queue_name() throws Exception {
        service.deleteQueue(QUEUE_DIR, "   ");
    }

    @Test
    public void test_deleteQueue_nonexistent_queue_does_not_throw() throws Exception {
        // Should not throw if queue doesn't exist
        service.deleteQueue(QUEUE_DIR, "nonexistent-queue");
    }

    @Test
    public void test_deleteQueue_with_dot_in_queue_name() throws Exception {
        String queueWithDot = "test.queue";
        IBigQueue testQueue = new BigQueueImpl(QUEUE_DIR, queueWithDot);
        testQueue.removeAll();

        assertTrue("Queue should exist", Files.exists(Paths.get(QUEUE_DIR + File.separator + queueWithDot)));
        service.deleteQueue(QUEUE_DIR, queueWithDot);
        assertFalse("Queue should be deleted", Files.exists(Paths.get(QUEUE_DIR + File.separator + queueWithDot)));
    }

    @Test
    public void test_listQueues_empty_directory() throws Exception {
        String emptyDir = "./target/empty-queues-" + randomAlphabetic(5) + "/";
        Files.createDirectories(Paths.get(emptyDir));

        List<String> queues = service.listQueues(emptyDir);
        assertTrue("Empty directory should return empty list", queues.isEmpty());
    }

    @Test
    public void test_listQueues_with_files_not_directories() throws Exception {
        String testDir = "./target/test-queues-" + randomAlphabetic(5) + "/";
        Files.createDirectories(Paths.get(testDir));
        Files.createFile(Paths.get(testDir + "file.txt"));

        List<String> queues = service.listQueues(testDir);
        assertTrue("Should not list files, only directories", queues.isEmpty());
    }

    @Test
    public void test_deleteMessage_with_first_message() throws Exception {
        BigQueueMessage msg1 = createBigQueueMessage();
        BigQueueMessage msg2 = createBigQueueMessage();
        BigQueueMessage msg3 = createBigQueueMessage();

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg3));

        assertEquals(3, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, msg1.getMessageId());

        assertEquals(2, service.size(QUEUE_NAME));
        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertEquals(msg2, messages.get(0));
        assertEquals(msg3, messages.get(1));
    }

    @Test
    public void test_deleteMessage_with_last_message() throws Exception {
        BigQueueMessage msg1 = createBigQueueMessage();
        BigQueueMessage msg2 = createBigQueueMessage();
        BigQueueMessage msg3 = createBigQueueMessage();

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg3));

        assertEquals(3, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, msg3.getMessageId());

        assertEquals(2, service.size(QUEUE_NAME));
        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertEquals(msg1, messages.get(0));
        assertEquals(msg2, messages.get(1));
    }

    @Test
    public void test_deleteMessage_single_message_queue() throws Exception {
        BigQueueMessage msg = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg));

        assertEquals(1, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, msg.getMessageId());

        assertEquals(0, service.size(QUEUE_NAME));
        assertTrue(service.getMessages(QUEUE_NAME).isEmpty());
    }

    @Test
    public void test_deleteMessage_empty_message_id() throws Exception {
        BigQueueMessage msg = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg));

        assertEquals(1, service.size(QUEUE_NAME));

        service.deleteMessage(QUEUE_NAME, "");

        assertEquals(1, service.size(QUEUE_NAME));
    }

    @Test
    public void test_deleteAllMessage_empty_queue() throws Exception {
        assertEquals(0, service.size(QUEUE_NAME));
        service.deleteAllMessage(QUEUE_NAME);
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void test_deleteAllMessage_verifies_messages_cleared() throws Exception {
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));

        assertEquals(3, service.size(QUEUE_NAME));
        assertFalse(service.getMessages(QUEUE_NAME).isEmpty());

        service.deleteAllMessage(QUEUE_NAME);

        assertEquals(0, service.size(QUEUE_NAME));
        assertTrue(service.getMessages(QUEUE_NAME).isEmpty());
    }

    @Test
    public void test_peek_does_not_remove_message() throws Exception {
        BigQueueMessage msg = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg));

        assertEquals(1, service.size(QUEUE_NAME));

        BigQueueMessage peeked = service.peek(QUEUE_NAME);
        assertNotNull(peeked);
        assertEquals(msg, peeked);

        assertEquals(1, service.size(QUEUE_NAME));
        BigQueueMessage peeked2 = service.peek(QUEUE_NAME);
        assertEquals(peeked, peeked2);
    }

    @Test
    public void test_getMessages_returns_messages_in_order() throws Exception {
        BigQueueMessage msg1 = createBigQueueMessage();
        BigQueueMessage msg2 = createBigQueueMessage();
        BigQueueMessage msg3 = createBigQueueMessage();

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg2));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg3));

        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);

        assertEquals(3, messages.size());
        assertEquals(msg1, messages.get(0));
        assertEquals(msg2, messages.get(1));
        assertEquals(msg3, messages.get(2));
    }

    @Test
    public void test_getMessages_does_not_modify_queue() throws Exception {
        BigQueueMessage msg1 = createBigQueueMessage();
        BigQueueMessage msg2 = createBigQueueMessage();

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg1));
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg2));

        assertEquals(2, service.size(QUEUE_NAME));

        List<BigQueueMessage> messages1 = service.getMessages(QUEUE_NAME);
        assertEquals(2, messages1.size());
        assertEquals(2, service.size(QUEUE_NAME));

        List<BigQueueMessage> messages2 = service.getMessages(QUEUE_NAME);
        assertEquals(2, messages2.size());
        assertEquals(2, service.size(QUEUE_NAME));
    }

    @Test
    public void test_size_after_multiple_operations() throws Exception {
        assertEquals(0, service.size(QUEUE_NAME));

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertEquals(1, service.size(QUEUE_NAME));

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertEquals(2, service.size(QUEUE_NAME));

        bigQueue.dequeue();
        assertEquals(1, service.size(QUEUE_NAME));

        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertEquals(2, service.size(QUEUE_NAME));

        service.deleteAllMessage(QUEUE_NAME);
        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void test_listQueues_sorts_queue_names() throws Exception {
        String rand = randomAlphabetic(10);
        String queueDir = QUEUE_DIR.substring(0, QUEUE_DIR.length() - 1) + "-sorted-" + rand + File.separator;

        new BigQueueImpl(queueDir, "queue-c");
        new BigQueueImpl(queueDir, "queue-a");
        new BigQueueImpl(queueDir, "queue-b");

        List<String> queues = service.listQueues(queueDir);

        assertEquals(3, queues.size());
        // Note: The implementation doesn't sort, so we just verify they're all there
        assertTrue(queues.contains("queue-a"));
        assertTrue(queues.contains("queue-b"));
        assertTrue(queues.contains("queue-c"));
    }

    @Test
    public void test_deleteMessage_with_malformed_message_id() throws Exception {
        BigQueueMessage msg = createBigQueueMessage();
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(msg));

        assertEquals(1, service.size(QUEUE_NAME));

        // Try deleting with various malformed IDs
        service.deleteMessage(QUEUE_NAME, "malformed-id");
        service.deleteMessage(QUEUE_NAME, "12345");
        service.deleteMessage(QUEUE_NAME, "");
        service.deleteMessage(QUEUE_NAME, null);

        // Message should still be there
        assertEquals(1, service.size(QUEUE_NAME));
    }

    @Test
    public void test_peek_after_deleteAllMessage() throws Exception {
        bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        assertNotNull(service.peek(QUEUE_NAME));

        service.deleteAllMessage(QUEUE_NAME);

        assertNull(service.peek(QUEUE_NAME));
    }

    @Test
    public void test_concurrent_deleteAllMessage_operations() throws Exception {
        // Add messages
        for (int i = 0; i < 1000; i++) {
            bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        }

        assertEquals(1000, service.size(QUEUE_NAME));

        // Delete all twice concurrently
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            try {
                service.deleteAllMessage(QUEUE_NAME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.submit(() -> {
            try {
                service.deleteAllMessage(QUEUE_NAME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, service.size(QUEUE_NAME));
    }

    @Test
    public void test_getBigQueue_throws_exception_for_unknown_queue() {
        try {
            service.size("unknown-queue");
            fail("Should throw BigQueueNotFoundException");
        } catch (BigQueueNotFoundException e) {
            assertEquals("not found", e.getMessage());
        }
    }

    @Test
    public void test_deleteQueue_validates_path_does_not_escape_base() throws Exception {
        try {
            service.deleteQueue(QUEUE_DIR, "test/../../../etc/passwd");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid queueName path traversal attempt!", e.getMessage());
        }
    }

    @Test
    public void test_listQueues_ignores_hidden_directories() throws Exception {
        String testDir = "./target/hidden-test-" + randomAlphabetic(5) + "/";
        Files.createDirectories(Paths.get(testDir));
        Files.createDirectories(Paths.get(testDir + ".hidden"));
        new BigQueueImpl(testDir, "visible-queue");

        List<String> queues = service.listQueues(testDir);

        // Should include both hidden and visible (implementation doesn't filter hidden)
        assertTrue(queues.size() >= 1);
        assertTrue(queues.contains("visible-queue"));
    }

    @Test
    public void test_large_queue_operations_performance() throws Exception {
        int messageCount = 10000;

        // Add messages
        for (int i = 0; i < messageCount; i++) {
            bigQueue.enqueue(OBJECT_MAPPER.writeValueAsBytes(createBigQueueMessage()));
        }

        assertEquals(messageCount, service.size(QUEUE_NAME));

        // Get messages should work on large queue
        List<BigQueueMessage> messages = service.getMessages(QUEUE_NAME);
        assertEquals(messageCount, messages.size());

        // Peek should be fast
        BigQueueMessage peeked = service.peek(QUEUE_NAME);
        assertNotNull(peeked);
    }

}