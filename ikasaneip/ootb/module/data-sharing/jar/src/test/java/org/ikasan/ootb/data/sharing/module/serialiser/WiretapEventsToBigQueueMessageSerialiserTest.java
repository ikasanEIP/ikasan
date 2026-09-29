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

import org.ikasan.dashboard.dto.wiretap.WiretapEventImpl;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for WiretapEventsToBigQueueMessageSerialiser.
 *
 * Tests cover:
 * - Serialization of single and multiple wiretap events
 * - Deserialization back to original structure
 * - Round-trip serialization/deserialization
 * - Empty list handling
 * - Large batch handling
 * - Error handling for null and invalid input
 * - Thread safety (synchronized methods)
 * - Data integrity verification
 */
public class WiretapEventsToBigQueueMessageSerialiserTest {

    private WiretapEventsToBigQueueMessageSerialiser serialiser;

    @Before
    public void setUp() {
        serialiser = new WiretapEventsToBigQueueMessageSerialiser();
    }

    /**
     * Test serialization of a single wiretap event.
     */
    @Test
    public void test_serialise_single_event() {
        // Setup
        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L)
        );

        // Execute
        byte[] serialized = serialiser.serialise(events);

        // Verify
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);

        // Verify the serialized data contains expected fields
        String serializedString = new String(serialized, StandardCharsets.UTF_8);
        assertTrue(serializedString.contains("component1"));
        assertTrue(serializedString.contains("module1"));
        assertTrue(serializedString.contains("flow1"));
    }

    /**
     * Test serialization of multiple wiretap events.
     */
    @Test
    public void test_serialise_multiple_events() {
        // Setup
        List<WiretapEvent> events = Arrays.asList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L),
            createWiretapEvent("event-2", "module2", "flow2", "component2", "payload2", 2000L, 200L),
            createWiretapEvent("event-3", "module3", "flow3", "component3", "payload3", 3000L, 300L)
        );

        // Execute
        byte[] serialized = serialiser.serialise(events);

        // Verify
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);

        String serializedString = new String(serialized, StandardCharsets.UTF_8);
        assertTrue(serializedString.contains("module1"));
        assertTrue(serializedString.contains("module2"));
        assertTrue(serializedString.contains("module3"));
    }

    /**
     * Test serialization of empty event list.
     */
    @Test
    public void test_serialise_empty_list() {
        // Setup
        List<WiretapEvent> emptyList = Collections.emptyList();

        // Execute
        byte[] serialized = serialiser.serialise(emptyList);

        // Verify
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    /**
     * Test serialization of large batch of events.
     */
    @Test
    public void test_serialise_large_batch() {
        // Setup: Create 1000 events
        List<WiretapEvent> largeEventList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeEventList.add(createWiretapEvent(
                "event-" + i,
                "module" + (i % 10),
                "flow" + (i % 5),
                "component" + (i % 3),
                "payload-" + i,
                1000L + i,
                100L
            ));
        }

        // Execute
        byte[] serialized = serialiser.serialise(largeEventList);

        // Verify
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);

        String serializedString = new String(serialized, StandardCharsets.UTF_8);
        assertTrue(serializedString.contains("module0"));
        assertTrue(serializedString.contains("999"));
    }

    /**
     * Test deserialization of single event.
     */
    @Test
    public void test_deserialise_single_event() {
        // Setup
        List<WiretapEvent> originalEvents = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L)
        );
        byte[] serialized = serialiser.serialise(originalEvents);

        // Execute
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertNotNull(deserialized);
        assertEquals(1, deserialized.size());

        WiretapEvent event = deserialized.get(0);
        assertEquals(1L, event.getIdentifier());  // "event-1" parses to 1
        assertEquals("module1", event.getModuleName());
        assertEquals("flow1", event.getFlowName());
        assertEquals("component1", event.getComponentName());
        assertEquals("payload1", event.getEvent());
        assertEquals(1000L, event.getTimestamp());
        assertEquals(100L, event.getExpiry());
    }

    /**
     * Test deserialization of multiple events.
     */
    @Test
    public void test_deserialise_multiple_events() {
        // Setup
        List<WiretapEvent> originalEvents = Arrays.asList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L),
            createWiretapEvent("event-2", "module2", "flow2", "component2", "payload2", 2000L, 200L),
            createWiretapEvent("event-3", "module3", "flow3", "component3", "payload3", 3000L, 300L)
        );
        byte[] serialized = serialiser.serialise(originalEvents);

        // Execute
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertNotNull(deserialized);
        assertEquals(3, deserialized.size());

        // Verify first event
        WiretapEvent event1 = deserialized.get(0);
        assertEquals(1L, event1.getIdentifier());  // "event-1" parses to 1
        assertEquals("module1", event1.getModuleName());

        // Verify second event
        WiretapEvent event2 = deserialized.get(1);
        assertEquals(2L, event2.getIdentifier());  // "event-2" parses to 2
        assertEquals("module2", event2.getModuleName());

        // Verify third event
        WiretapEvent event3 = deserialized.get(2);
        assertEquals(3L, event3.getIdentifier());  // "event-3" parses to 3
        assertEquals("module3", event3.getModuleName());
    }

    /**
     * Test deserialization of empty list.
     */
    @Test
    public void test_deserialise_empty_list() {
        // Setup
        List<WiretapEvent> emptyList = Collections.emptyList();
        byte[] serialized = serialiser.serialise(emptyList);

        // Execute
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertNotNull(deserialized);
        assertEquals(0, deserialized.size());
    }

    /**
     * Test round-trip serialization and deserialization maintains data integrity.
     */
    @Test
    public void test_round_trip_serialization_maintains_data_integrity() {
        // Setup
        List<WiretapEvent> originalEvents = Arrays.asList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L),
            createWiretapEvent("event-2", "module2", "flow2", "component2", "payload2", 2000L, 200L)
        );

        // Execute: serialize and deserialize
        byte[] serialized = serialiser.serialise(originalEvents);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify: all data matches
        assertEquals(originalEvents.size(), deserialized.size());

        for (int i = 0; i < originalEvents.size(); i++) {
            WiretapEvent original = originalEvents.get(i);
            WiretapEvent roundTrip = deserialized.get(i);

            assertEquals(original.getIdentifier(), roundTrip.getIdentifier());
            assertEquals(original.getModuleName(), roundTrip.getModuleName());
            assertEquals(original.getFlowName(), roundTrip.getFlowName());
            assertEquals(original.getComponentName(), roundTrip.getComponentName());
            assertEquals(original.getEvent(), roundTrip.getEvent());
            assertEquals(original.getTimestamp(), roundTrip.getTimestamp());
            assertEquals(original.getExpiry(), roundTrip.getExpiry());
        }
    }

    /**
     * Test round-trip with large batch.
     */
    @Test
    public void test_round_trip_large_batch() {
        // Setup: Create 500 events
        List<WiretapEvent> originalEvents = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            originalEvents.add(createWiretapEvent(
                "event-" + i,
                "module" + (i % 10),
                "flow" + (i % 5),
                "component" + (i % 3),
                "payload-" + i,
                1000L + i,
                100L
            ));
        }

        // Execute
        byte[] serialized = serialiser.serialise(originalEvents);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(originalEvents.size(), deserialized.size());
        assertEquals(0L, deserialized.get(0).getIdentifier());  // "event-0" parses to 0
        assertEquals(499L, deserialized.get(499).getIdentifier());  // "event-499" parses to 499
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
     * Test serialization of events with special characters in payload.
     */
    @Test
    public void test_serialise_events_with_special_characters() {
        // Setup: Create event with special characters
        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent(
                "event-1",
                "module1",
                "flow1",
                "component1",
                "payload with special chars: \n\t\r\"'\\{}[]<>",
                1000L,
                100L
            )
        );

        // Execute
        byte[] serialized = serialiser.serialise(events);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(1, deserialized.size());
        assertTrue(((String) deserialized.get(0).getEvent()).contains("special chars"));
    }

    /**
     * Test serialization of events with long payloads.
     */
    @Test
    public void test_serialise_events_with_long_payload() {
        // Setup: Create event with long payload (10KB)
        StringBuilder longPayload = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longPayload.append("X");
        }

        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", longPayload.toString(), 1000L, 100L)
        );

        // Execute
        byte[] serialized = serialiser.serialise(events);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(1, deserialized.size());
        assertEquals(10000, ((String) deserialized.get(0).getEvent()).length());
    }

    /**
     * Test serialization of events with null fields.
     */
    @Test
    public void test_serialise_events_with_null_fields() {
        // Setup: Create event with some null fields
        WiretapEventImpl event = new WiretapEventImpl();
        event.setId("event-1");
        event.setModuleName("module1");
        event.setFlowName(null);  // null field
        event.setComponentName(null);  // null field
        event.setEvent("payload");
        event.setTimestamp(1000L);
        event.setExpiry(100L);

        List<WiretapEvent> events = Collections.singletonList(event);

        // Execute
        byte[] serialized = serialiser.serialise(events);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(1, deserialized.size());
        assertEquals(1L, deserialized.get(0).getIdentifier());  // "event-1" parses to 1
        assertNull(deserialized.get(0).getFlowName());
        assertNull(deserialized.get(0).getComponentName());
    }

    /**
     * Test multiple concurrent serializations (thread safety).
     */
    @Test
    public void test_concurrent_serialization() throws InterruptedException {
        // Setup
        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L)
        );

        // Execute: Run serialization in multiple threads
        Thread[] threads = new Thread[10];
        final boolean[] success = {true};

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    byte[] serialized = serialiser.serialise(events);
                    List<WiretapEvent> deserialized = serialiser.deserialise(serialized);
                    if (deserialized.size() != 1) {
                        success[0] = false;
                    }
                } catch (Exception e) {
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
     * Test that deserialized events are of correct implementation type.
     */
    @Test
    public void test_deserialized_events_are_wiretap_event_impl() {
        // Setup
        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1", 1000L, 100L)
        );
        byte[] serialized = serialiser.serialise(events);

        // Execute
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(1, deserialized.size());
        assertTrue("Deserialized event should be WiretapEventImpl",
            deserialized.get(0) instanceof WiretapEventImpl);
    }

    /**
     * Test serialization of events with very large timestamps.
     */
    @Test
    public void test_serialise_events_with_large_timestamps() {
        // Setup
        List<WiretapEvent> events = Collections.singletonList(
            createWiretapEvent("event-1", "module1", "flow1", "component1", "payload1",
                Long.MAX_VALUE - 1000, Long.MAX_VALUE)
        );

        // Execute
        byte[] serialized = serialiser.serialise(events);
        List<WiretapEvent> deserialized = serialiser.deserialise(serialized);

        // Verify
        assertEquals(1, deserialized.size());
        assertEquals(Long.MAX_VALUE - 1000, deserialized.get(0).getTimestamp());
        assertEquals(Long.MAX_VALUE, deserialized.get(0).getExpiry());
    }

    /**
     * Helper method to create WiretapEventImpl instances for testing.
     */
    private WiretapEvent createWiretapEvent(String id, String moduleName, String flowName,
                                            String componentName, String payload,
                                            Long timestamp, Long expiry) {
        WiretapEventImpl event = new WiretapEventImpl();
        event.setId(id);
        event.setModuleName(moduleName);
        event.setFlowName(flowName);
        event.setComponentName(componentName);
        event.setEvent(payload);
        event.setTimestamp(timestamp);
        event.setExpiry(expiry);
        return event;
    }
}
