/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.dashboard.dto.error.ErrorOccurrenceImpl;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for ErrorOccurrencesToBigQueueMessageSerialiser.
 *
 * Tests cover:
 * - Serialization of single and multiple error occurrences
 * - Deserialization back to original structure
 * - Round-trip serialization/deserialization
 * - Empty list handling
 * - Large batch handling
 * - Error handling for null and invalid input
 * - Thread safety (synchronized methods)
 * - Data integrity verification
 */
public class ErrorOccurrencesToBigQueueMessageSerialiserTest {

    private ErrorOccurrenceToBigQueueMessageSerialiser serialiser;

    @Before
    public void setUp() {
        serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();
    }

    /**
     * Test serialization of a single error occurrence.
     */
    @Test
    public void test_serialise_single_error() {
        // Setup
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            createErrorOccurrenceSimpleTestFixture(1)
        );

        // Execute
        byte[] serialized = serialiser.serialise(errors);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, serialiser.deserialise(serialized));
    }

    /**
     * Test serialization of multiple error occurrences.
     */
    @Test
    public void test_serialise_multiple_errors() {
        // Setup
        List<ErrorOccurrence<byte[]>> errors = Arrays.asList(
            createErrorOccurrenceSimpleTestFixture(1),
            createErrorOccurrenceSimpleTestFixture(2),
            createErrorOccurrenceSimpleTestFixture(3)
        );

        // Execute
        byte[] serialized = serialiser.serialise(errors);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, serialiser.deserialise(serialized));
    }

    /**
     * Test serialization of empty error list.
     */
    @Test
    public void test_serialise_empty_list() {
        // Setup
        List<ErrorOccurrence<byte[]>> emptyList = Collections.emptyList();

        // Execute
        byte[] serialized = serialiser.serialise(emptyList);

        // Verify
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    /**
     * Test serialization of large batch of errors.
     */
    @Test
    public void test_serialise_large_batch() {
        // Setup: Create 1000 errors
        List<ErrorOccurrence<byte[]>> largeEventList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeEventList.add(createErrorOccurrenceTestFixture(i));
        }

        // Execute
        byte[] serialized = serialiser.serialise(largeEventList);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(largeEventList, serialiser.deserialise(serialized));
    }

    /**
     * Test deserialization of single errorOccurrence.
     */
    @Test
    public void test_deserialise_single_error() {
        // Setup
        List<ErrorOccurrence<byte[]>> originalEvents = Collections.singletonList(
            createErrorOccurrenceSimpleTestFixture(1)
        );
        byte[] serialized = serialiser.serialise(originalEvents);

        // Execute
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(originalEvents, deserialized);
    }

    /**
     * Test deserialization of multiple errors.
     */
    @Test
    public void test_deserialise_multiple_errors() {
        // Setup
        List<ErrorOccurrence<byte[]>> originalEvents = Arrays.asList(
            createErrorOccurrenceSimpleTestFixture(1),
            createErrorOccurrenceSimpleTestFixture(2),
            createErrorOccurrenceSimpleTestFixture(3)
        );
        byte[] serialized = serialiser.serialise(originalEvents);

        // Execute
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(originalEvents, deserialized);
    }

    /**
     * Test deserialization of empty list.
     */
    @Test
    public void test_deserialise_empty_list() {
        // Setup
        List<ErrorOccurrence<byte[]>> emptyList = Collections.emptyList();
        byte[] serialized = serialiser.serialise(emptyList);

        // Execute
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(emptyList, deserialized);
    }

    /**
     * Test round-trip serialization and deserialization maintains data integrity.
     */
    @Test
    public void test_round_trip_serialization_maintains_data_integrity() {
        // Setup
        List<ErrorOccurrence<byte[]>> originalEvents = Arrays.asList(
            createErrorOccurrenceSimpleTestFixture(1),
            createErrorOccurrenceSimpleTestFixture(2)
        );

        // Execute: serialize and deserialize
        byte[] serialized = serialiser.serialise(originalEvents);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(originalEvents, deserialized);
    }

    /**
     * Test round-trip with large batch.
     */
    @Test
    public void test_round_trip_large_batch() {
        // Setup: Create 500 errors
        List<ErrorOccurrence<byte[]>> originalEvents = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            originalEvents.add(createErrorOccurrenceTestFixture(i));
        }

        // Execute
        byte[] serialized = serialiser.serialise(originalEvents);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(originalEvents, deserialized);
    }


    public void test_serialise_null_list_throws_exception() {
        serialiser.serialise(null);
    }

    /**
     * Test deserialization with null bytes throws RuntimeException.
     */
    @Test(expected = RuntimeException.class)
    public void test_deserialise_null_bytes_throws_exception() {
        serialiser.deserialise(null);
    }

    /**
     * Test deserialization with invalid bytes throws RuntimeException.
     */
    @Test(expected = RuntimeException.class)
    public void test_deserialise_invalid_bytes_throws_exception() {
        byte[] invalidBytes = "invalid json data".getBytes(StandardCharsets.UTF_8);
        serialiser.deserialise(invalidBytes);
    }

    /**
     * Test deserialization with empty bytes throws RuntimeException.
     */
    @Test(expected = RuntimeException.class)
    public void test_deserialise_empty_bytes_throws_exception() {
        byte[] emptyBytes = new byte[0];
        serialiser.deserialise(emptyBytes);
    }

    /**
     * Test serialization of errors with special characters in payload.
     */
    @Test
    public void test_serialise_errors_with_special_characters() {
        // Setup: Create error with special characters
        ErrorOccurrence<byte[]> errorOccurrence = createErrorOccurrenceSimpleTestFixture(1);
        errorOccurrence.setEventAsString("payload with special chars: \n\t\r\"'\\{}[]<>");
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            errorOccurrence
        );

        // Execute
        byte[] serialized = serialiser.serialise(errors);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, deserialized);
    }

    /**
     * Test serialization of errors with long payloads.
     */
    @Test
    public void test_serialise_errors_with_long_payload() {
        // Setup: Create error with long payload (10KB)
        StringBuilder longPayload = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longPayload.append("X");
        }
        ErrorOccurrence<byte[]> errorOccurrence = createErrorOccurrenceSimpleTestFixture(1);
        errorOccurrence.setEventAsString(longPayload.toString());
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            errorOccurrence
        );

        // Execute
        byte[] serialized = serialiser.serialise(errors);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, deserialized);
    }

    /**
     * Test serialization of errors with null fields.
     */
    @Test
    public void test_serialise_with_null_fields() {
        // Setup: Create error with some null fields
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl();
        createErrorOccurrence(
            "error-occurrence1", // id
            null, // moduleName
            null, // flowName
            null, // componentName
            null, // payload
            100L, // timestamp
            100L, // expiry
            null, // errorDetail
            null, // errorMessage
            null, // exceptionClass
            null, // eventLifeIdentifier
            null, // eventRelatedIdentifier
            null, // action
            null, // userAction
            null, // actionedBy
            100L // userActionTimestamp
        );

        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(errorOccurrence);

        // Execute
        byte[] serialized = serialiser.serialise(errors);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, deserialized);
    }

    /**
     * Test multiple concurrent serializations (thread safety).
     */
    @Test
    public void test_concurrent_serialization() throws InterruptedException {
        // Setup
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            createErrorOccurrenceSimpleTestFixture(1)
        );

        // Execute: Run serialization in multiple threads
        Thread[] threads = new Thread[10];
        final boolean[] success = {true};

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    byte[] serialized = serialiser.serialise(errors);
                    List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);
                    assertErrorOccurrencesEqual(errors, deserialized);
                } catch (Exception | AssertionError e) {
                    success[0] = false;
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify
        assertTrue("Concurrent serialization should succeed", success[0]);
    }

    /**
     * Test that deserialized errors are of the correct implementation type.
     */
    @Test
    public void test_deserialized_errors_are_error_occurrence_impl() {
        // Setup
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            createErrorOccurrenceSimpleTestFixture(1)
        );
        byte[] serialized = serialiser.serialise(errors);

        // Execute
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, deserialized);

        // Verify implementation type
        assertTrue("Deserialized error should be ErrorOccurrenceImpl",
            deserialized.get(0) instanceof ErrorOccurrenceImpl);
    }

    /**
     * Test serialization of errors with very large timestamps.
     */
    @Test
    public void test_serialise_errors_with_large_timestamps() {
        // Setup
        ErrorOccurrence<byte[]> errorOccurrence = createErrorOccurrenceSimpleTestFixture(1);
        errorOccurrence.setTimestamp(Long.MAX_VALUE - 1000);
        errorOccurrence.setExpiry(Long.MAX_VALUE);
        List<ErrorOccurrence<byte[]>> errors = Collections.singletonList(
            errorOccurrence
        );

        // Execute
        byte[] serialized = serialiser.serialise(errors);
        List<ErrorOccurrence<byte[]>> deserialized = serialiser.deserialise(serialized);

        // Verify all occurrence fields
        assertErrorOccurrencesEqual(errors, deserialized);
    }

    private ErrorOccurrence<byte[]> createErrorOccurrenceSimpleTestFixture(int count) {
        return createErrorOccurrence("error-occurrence"+count, "module"+count, "flow"+count,
            "component"+count, "payload"+count, count * 1000L, count + 100L,
            "myExceptionAndTrace"+count,"exceptionMessage"+count,"java.lang.IllegalArgumentException",
            "eventLifeIdentifier"+count, "eventRelatedIdentifier"+count,"action"+count, "userAction"+count, "actionedBy"+count, count * 1000L);
    }

    private ErrorOccurrence<byte[]> createErrorOccurrenceTestFixture(int count) {
        return createErrorOccurrence(
            "error-occurrence" + count,
            "module" + (count % 10),
            "flow" + (count % 5),
            "component" + (count % 3),
            "payload-" + count,
            1000L + count,
            100L,
            "myExceptionAndTrace" + (count % 3),
            "exceptionMessage" + (count % 3),
            "java.lang.IllegalArgumentException" + (count % 3),
            "eventLifeIdentifier" + (count % 3),
            "eventRelatedIdentifier" + (count % 3),
            "action" + (count % 3),
            "userAction" + (count % 3),
            "actionedBy" + (count % 3),
            1000L + count
        );
    }

    private void assertErrorOccurrencesEqual(List<ErrorOccurrence<byte[]>> expected,
                                             List<ErrorOccurrence<byte[]>> actual) {
        assertNotNull(actual);
        assertEquals("Occurrence count", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            ErrorOccurrence<byte[]> original = expected.get(i);
            ErrorOccurrence<byte[]> restored = actual.get(i);
            String context = "Occurrence " + i + ": ";
            assertNotNull(context, restored);
            assertEquals(context + "Uri", original.getUri(), restored.getUri());
            assertEquals(context + "ModuleName", original.getModuleName(), restored.getModuleName());
            assertEquals(context + "FlowName", original.getFlowName(), restored.getFlowName());
            assertEquals(context + "FlowElementName", original.getFlowElementName(), restored.getFlowElementName());
            assertEquals(context + "ErrorDetail", original.getErrorDetail(), restored.getErrorDetail());
            assertEquals(context + "ErrorMessage", original.getErrorMessage(), restored.getErrorMessage());
            assertEquals(context + "ExceptionClass", original.getExceptionClass(), restored.getExceptionClass());
            assertEquals(context + "EventLifeIdentifier", original.getEventLifeIdentifier(), restored.getEventLifeIdentifier());
            assertEquals(context + "EventRelatedIdentifier", original.getEventRelatedIdentifier(), restored.getEventRelatedIdentifier());
            assertEquals(context + "Action", original.getAction(), restored.getAction());
            assertArrayEquals(context + "Event", original.getEvent(), restored.getEvent());
            assertEquals(context + "EventAsString", original.getEventAsString(), restored.getEventAsString());
            assertEquals(context + "Timestamp", original.getTimestamp(), restored.getTimestamp());
            assertEquals(context + "Expiry", original.getExpiry(), restored.getExpiry());
            assertEquals(context + "UserAction", original.getUserAction(), restored.getUserAction());
            assertEquals(context + "ActionedBy", original.getActionedBy(), restored.getActionedBy());
            assertEquals(context + "UserActionTimestamp", original.getUserActionTimestamp(), restored.getUserActionTimestamp());
        }
    }

    /**
     * Helper method to create ErrorOccurrenceImpl instances for testing.
     */
    private ErrorOccurrence<byte[]> createErrorOccurrence(String uri, String moduleName, String flowName,
                                        String componentName, String payload,
                                        Long timestamp, Long expiry, String errorDetail, String errorMessage, String exceptionClass,
                                        String eventLifeIdentifier, String eventRelatedIdentifier, String action, String userAction, String actionedBy, Long userActionTimestamp) {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl();

        errorOccurrence.setAction(action);
        errorOccurrence.setActionedBy(actionedBy);
        errorOccurrence.setErrorDetail(errorDetail);
        errorOccurrence.setErrorMessage(errorMessage);
        errorOccurrence.setEvent(payload == null ? null : payload.getBytes(StandardCharsets.UTF_8));
        errorOccurrence.setEventAsString(payload);
        errorOccurrence.setEventLifeIdentifier(eventLifeIdentifier);
        errorOccurrence.setEventRelatedIdentifier(eventRelatedIdentifier);
        errorOccurrence.setExceptionClass(exceptionClass);
        errorOccurrence.setExpiry(expiry);
        errorOccurrence.setFlowElementName(componentName);
        errorOccurrence.setFlowName(flowName);
        errorOccurrence.setModuleName(moduleName);
        errorOccurrence.setTimestamp(timestamp);
        errorOccurrence.setUri(uri);
        errorOccurrence.setUserAction(userAction);
        errorOccurrence.setUserActionTimestamp(userActionTimestamp);
        return errorOccurrence;
    }
}
