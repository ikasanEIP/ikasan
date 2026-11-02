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
package org.ikasan.ootb.data.sharing.module.component.endpoint.producer;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.replay.ReplayEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ReplayEventRestProducer.
 *
 * Tests cover:
 * - Constructor validation
 * - Successful event publishing (single and multiple events)
 * - Empty event list handling
 * - Failure scenarios (false return, exceptions)
 * - Interaction with DashboardRestService
 * - Error message content
 */
public class ReplayEventRestProducerTest {

    private ReplayEventRestProducer producer;
    private DashboardRestService<List<ReplayEvent>> mockDashboardRestService;

    @Before
    public void setUp() {
        mockDashboardRestService = mock(DashboardRestService.class);
        producer = new ReplayEventRestProducer(mockDashboardRestService);
    }

    /**
     * Test that constructor validates non-null DashboardRestService parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_throws_exception_when_service_is_null() {
        new ReplayEventRestProducer(null);
    }

    /**
     * Test that constructor accepts valid DashboardRestService.
     */
    @Test
    public void test_constructor_accepts_valid_service() {
        DashboardRestService<List<ReplayEvent>> service = mock(DashboardRestService.class);
        ReplayEventRestProducer testProducer = new ReplayEventRestProducer(service);
        assertNotNull(testProducer);
    }

    /**
     * Test successful publishing of a single event occurrence.
     */
    @Test
    public void test_invoke_publishes_single_event_successfully() throws EndpointException {
        // Setup
        ReplayEvent event = mock(ReplayEvent.class);
        List<ReplayEvent> events = Collections.singletonList(event);

        when(mockDashboardRestService.publish(events)).thenReturn(true);

        // Execute
        producer.invoke(events);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(events);
    }

    /**
     * Test successful publishing of multiple event occurrences.
     */
    @Test
    public void test_invoke_publishes_multiple_events_successfully() throws EndpointException {
        // Setup
        List<ReplayEvent> events = Arrays.asList(
            mock(ReplayEvent.class),
            mock(ReplayEvent.class),
            mock(ReplayEvent.class)
        );

        when(mockDashboardRestService.publish(events)).thenReturn(true);

        // Execute
        producer.invoke(events);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(events);
    }

    /**
     * Test publishing of empty event list.
     */
    @Test
    public void test_invoke_handles_empty_event_list() throws EndpointException {
        // Setup
        List<ReplayEvent> emptyList = Collections.emptyList();

        // Execute
        producer.invoke(emptyList);

        // Verify
        verifyNoInteractions(mockDashboardRestService);
    }

    /**
     * Test publishing of large batch of events.
     */
    @Test
    public void test_invoke_publishes_large_batch_successfully() throws EndpointException {
        // Setup: Create a large list of events
        List<ReplayEvent> largeEventList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeEventList.add(mock(ReplayEvent.class));
        }

        when(mockDashboardRestService.publish(largeEventList)).thenReturn(true);

        // Execute
        producer.invoke(largeEventList);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(largeEventList);
    }

    /**
     * Test that EndpointException is thrown when publish returns false.
     */
    @Test
    public void test_invoke_throws_exception_when_publish_returns_false() {
        // Setup
        List<ReplayEvent> events = Collections.singletonList(mock(ReplayEvent.class));
        when(mockDashboardRestService.publish(events)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(events);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertTrue(e.getMessage().contains("Could not publish a replay event to the dashboard."));
            assertTrue(e.getMessage().contains("dashboard extract is enabled"));
        }

        verify(mockDashboardRestService, times(1)).publish(events);
    }

    /**
     * Test that EndpointException is thrown when DashboardRestService throws RuntimeException.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_runtime_exception() {
        // Setup
        List<ReplayEvent> events = Collections.singletonList(mock(ReplayEvent.class));
        RuntimeException cause = new RuntimeException("Connection timeout");
        when(mockDashboardRestService.publish(events)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(events);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }

        verify(mockDashboardRestService, times(1)).publish(events);
    }

    /**
     * Test that EndpointException is thrown for IllegalArgumentException from service.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_illegal_argument() {
        // Setup
        List<ReplayEvent> events = Collections.singletonList(mock(ReplayEvent.class));
        IllegalArgumentException cause = new IllegalArgumentException("Invalid event format");
        when(mockDashboardRestService.publish(events)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(events);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }
    }

    /**
     * Test that EndpointException is thrown for NullPointerException from service.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_null_pointer() {
        // Setup
        List<ReplayEvent> events = Collections.singletonList(mock(ReplayEvent.class));
        NullPointerException cause = new NullPointerException("Service configuration is null");
        when(mockDashboardRestService.publish(events)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(events);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }
    }

    /**
     * Test that the exact event list is passed to DashboardRestService.
     */
    @Test
    public void test_invoke_passes_exact_event_list_to_service() throws EndpointException {
        // Setup
        ReplayEvent event1 = mock(ReplayEvent.class);
        ReplayEvent event2 = mock(ReplayEvent.class);
        List<ReplayEvent> events = Arrays.asList(event1, event2);

        ArgumentCaptor<List<ReplayEvent>> captor = ArgumentCaptor.forClass(List.class);
        when(mockDashboardRestService.publish(any())).thenReturn(true);

        // Execute
        producer.invoke(events);

        // Verify
        verify(mockDashboardRestService).publish(captor.capture());
        List<ReplayEvent> capturedEvents = captor.getValue();
        assertEquals(2, capturedEvents.size());
        assertSame(event1, capturedEvents.get(0));
        assertSame(event2, capturedEvents.get(1));
    }

    /**
     * Test multiple successful invocations.
     */
    @Test
    public void test_multiple_invocations_all_successful() throws EndpointException {
        // Setup
        List<ReplayEvent> events1 = Collections.singletonList(mock(ReplayEvent.class));
        List<ReplayEvent> events2 = Arrays.asList(mock(ReplayEvent.class), mock(ReplayEvent.class));
        List<ReplayEvent> events3 = Collections.singletonList(mock(ReplayEvent.class));

        when(mockDashboardRestService.publish(any())).thenReturn(true);

        // Execute
        producer.invoke(events1);
        producer.invoke(events2);
        producer.invoke(events3);

        // Verify
        verify(mockDashboardRestService, times(3)).publish(any());
    }

    /**
     * Test that producer can recover after a failed invocation.
     */
    @Test
    public void test_producer_recovers_after_failure() throws EndpointException {
        // Setup
        List<ReplayEvent> events1 = Collections.singletonList(mock(ReplayEvent.class));
        List<ReplayEvent> events2 = Collections.singletonList(mock(ReplayEvent.class));

        when(mockDashboardRestService.publish(events1)).thenReturn(false);
        when(mockDashboardRestService.publish(events2)).thenReturn(true);

        // Execute first invocation (should fail)
        try {
            producer.invoke(events1);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            // Expected
        }

        // Execute second invocation (should succeed)
        producer.invoke(events2);

        // Verify both attempts were made
        verify(mockDashboardRestService, times(1)).publish(events1);
        verify(mockDashboardRestService, times(1)).publish(events2);
    }

    /**
     * Test that null event list is passed through to service.
     * The service should handle null appropriately.
     */
    @Test
    public void test_invoke_with_null_event_list() {
        // Setup
        when(mockDashboardRestService.publish(null)).thenThrow(new NullPointerException("Event list cannot be null"));

        // Execute and verify
        try {
            producer.invoke(null);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }

        verify(mockDashboardRestService, times(1)).publish(null);
    }

    /**
     * Test error message format when publish returns false.
     */
    @Test
    public void test_error_message_when_publish_fails() {
        // Setup
        List<ReplayEvent> events = Collections.singletonList(mock(ReplayEvent.class));
        when(mockDashboardRestService.publish(events)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(events);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            String message = e.getMessage();
            Assert.assertNotNull(message);
            Assert.assertTrue("Message should mention dashboard", message.contains("dashboard"));
            Assert.assertTrue("Message should mention publish failure", message.contains("Could not publish"));
            Assert.assertTrue("Message should mention dashboard extract", message.contains("dashboard extract is enabled"));
        }
    }
}
