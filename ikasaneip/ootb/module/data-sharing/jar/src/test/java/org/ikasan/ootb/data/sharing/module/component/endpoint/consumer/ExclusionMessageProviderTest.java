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
package org.ikasan.ootb.data.sharing.module.component.endpoint.consumer;

import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageConsumer;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.ExclusionConsumerConfiguration;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.WiretapConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.JobExecutionContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for exclusionMessageProvider.
 *
 * Tests cover:
 * - Configuration management
 * - Single batch retrieval
 * - Multiple batch retrieval with paging
 * - Timestamp and offset management
 * - Error handling
 * - CallBackMessageConsumer interaction
 */
public class ExclusionMessageProviderTest {

    private ExclusionMessageProvider exclusionMessageProvider;
    private DataSharingRestService mockDataSharingRestService;
    private CallBackMessageConsumer mockCallBackMessageConsumer;
    private ExclusionConsumerConfiguration configuration;
    private JobExecutionContext mockJobExecutionContext;

    @Before
    public void setUp() {
        mockDataSharingRestService = mock(DataSharingRestService.class);
        mockCallBackMessageConsumer = mock(CallBackMessageConsumer.class, withSettings().extraInterfaces(ConfiguredResource.class));
        mockJobExecutionContext = mock(JobExecutionContext.class);

        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);
        // Simulate the queue producer applying progress inside the flow callback.
        doAnswer(invocation -> {
            exclusionMessageProvider.applyPendingCheckpoint();
            return null;
        }).when(mockCallBackMessageConsumer).invoke(anyList());

        configuration = new ExclusionConsumerConfiguration();
        configuration.setQueryTimestamp(0L);
        configuration.setSafetyLagMilliseconds(0);
        configuration.setQueryWindowMilliseconds(100000L);
        configuration.setBatchSize(100);
        configuration.setOffset(0);
        configuration.setModuleNames(Arrays.asList("module1", "module2"));

        exclusionMessageProvider.setConfiguration(configuration);
    }

    @Test
    public void test_invoke_with_no_results() throws Exception {
        // Setup: count returns 0
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(0L);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, never()).queryExclusions(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer).invoke(List.of());
        assertEquals("An empty completed historical window must advance the checkpoint",
            100000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_empty_historical_window_does_not_block_events_in_next_window() throws Exception {
        when(mockDataSharingRestService.countExclusions(0L, 99999L, configuration.getModuleNames()))
            .thenReturn(0L);
        when(mockDataSharingRestService.countExclusions(100000L, 199999L, configuration.getModuleNames()))
            .thenReturn(1L);
        List<ExclusionEvent> events = createExclusionEvent(1);
        SearchResults<ExclusionEvent> results = createSearchResults(events, 1);
        when(mockDataSharingRestService.queryExclusions(100000L, 199999L,
            configuration.getModuleNames(), 100, 0))
            .thenReturn(results);

        // Both windows are historical: the second poll should reach the waiting event.
        exclusionMessageProvider.invoke(mockJobExecutionContext);
        exclusionMessageProvider.invoke(mockJobExecutionContext);

        ArgumentCaptor<Long> starts = ArgumentCaptor.forClass(Long.class);
        verify(mockDataSharingRestService, times(2))
            .countExclusions(starts.capture(), anyLong(), anyList());
        assertEquals("Polling must move past an empty window rather than query it forever",
            Arrays.asList(0L, 100000L), starts.getAllValues());
        verify(mockCallBackMessageConsumer, times(1)).invoke(events);
        assertEquals(200000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_invoke_with_single_batch() throws Exception {
        // Setup: count returns 50 (less than batch size of 100)
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);

        List<ExclusionEvent> events = createExclusionEvent(50);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 50);

        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0);

        ArgumentCaptor<List<ExclusionEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockCallBackMessageConsumer, times(1)).invoke(captor.capture());
        assertEquals(50, captor.getValue().size());

        // Verify timestamp was advanced and offset reset
        assertEquals(100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_exact_batch_size() throws Exception {
        // Setup: count returns exactly 100 (equals batch size)
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(100L);

        List<ExclusionEvent> events = createExclusionEvent(100);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 100);

        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0);
        verify(mockCallBackMessageConsumer, times(1)).invoke(anyList());

        assertEquals("A fully delivered exact batch must advance the checkpoint",
            100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_multiple_batches() throws Exception {
        // Setup: count returns 250 (requires 3 batches: 100, 100, 50)
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(250L);

        // First batch: 100 events
        List<ExclusionEvent> events1 = createExclusionEvent(100);
        SearchResults<ExclusionEvent> searchResults1 = createSearchResults(events1, 250);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults1);

        // Second batch: 100 events
        List<ExclusionEvent> events2 = createExclusionEvent(100);
        SearchResults<ExclusionEvent> searchResults2 = createSearchResults(events2, 250);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 100))
            .thenReturn(searchResults2);

        // Third batch: 50 events
        List<ExclusionEvent> events3 = createExclusionEvent(50);
        SearchResults<ExclusionEvent> searchResults3 = createSearchResults(events3, 250);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 200))
            .thenReturn(searchResults3);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(3)).queryExclusions(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer, times(3)).invoke(anyList());

        // Verify timestamp was advanced and offset reset after last batch
        assertEquals(100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_large_dataset() throws Exception {
        // Setup: count returns 1000 (requires 10 batches of 100)
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(1000L);

        // Create mocks for all 10 batches
        for (int i = 0; i < 10; i++) {
            List<ExclusionEvent> events = createExclusionEvent(100);
            SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 1000);
            when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, i * 100))
                .thenReturn(searchResults);
        }

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(10)).queryExclusions(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer, times(10)).invoke(anyList());

        assertEquals("A fully delivered exact multiple of batchSize must advance the checkpoint",
            100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_custom_batch_size() throws Exception {
        // Setup: custom batch size of 50
        configuration.setBatchSize(50);

        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(125L);

        // Mock 3 batches: 50, 50, 25
        for (int i = 0; i < 3; i++) {
            int size = (i < 2) ? 50 : 25;
            List<ExclusionEvent> events = createExclusionEvent(size);
            SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 125);
            when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 50, i * 50))
                .thenReturn(searchResults);
        }

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(3)).queryExclusions(anyLong(), anyLong(), anyList(), eq(50), anyInt());
        verify(mockCallBackMessageConsumer, times(3)).invoke(anyList());
    }

    @Test
    public void test_invoke_with_custom_time_window() throws Exception {
        // Setup: custom query window of 50000ms
        configuration.setQueryWindowMilliseconds(50000L);
        configuration.setQueryTimestamp(1000L);

        when(mockDataSharingRestService.countExclusions(1000L, 50999L, Arrays.asList("module1", "module2")))
            .thenReturn(75L);

        List<ExclusionEvent> events = createExclusionEvent(75);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 75);
        when(mockDataSharingRestService.queryExclusions(1000L, 50999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(1000L, 50999L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryExclusions(1000L, 50999L, Arrays.asList("module1", "module2"), 100, 0);

        // Verify timestamp was advanced by window size
        assertEquals(51000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_invoke_with_single_module() throws Exception {
        // Setup: single module
        configuration.setModuleNames(Arrays.asList("singleModule"));

        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("singleModule")))
            .thenReturn(30L);

        List<ExclusionEvent> events = createExclusionEvent(30);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 30);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("singleModule"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, Arrays.asList("singleModule"));
        verify(mockDataSharingRestService, times(1)).queryExclusions(0L, 99999L, Arrays.asList("singleModule"), 100, 0);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_count_error() throws Exception {
        // Setup: countExclusions throws exception
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenThrow(new RuntimeException("Connection error"));

        // Execute - should throw EndpointException
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_query_error() throws Exception {
        // Setup: count succeeds, but query throws exception
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(100L);

        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenThrow(new RuntimeException("Query failed"));

        // Execute - should throw EndpointException
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_consumer_error() throws Exception {
        // Setup: everything succeeds until consumer throws exception
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);

        List<ExclusionEvent> events = createExclusionEvent(50);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 50);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        doThrow(new RuntimeException("Consumer processing error"))
            .when(mockCallBackMessageConsumer).invoke(anyList());

        // Execute - should throw EndpointException
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test
    public void test_incomplete_window_waits_for_next_scheduled_poll() throws Exception {
        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService,
            Clock.fixed(Instant.ofEpochMilli(50000L), ZoneOffset.UTC));
        exclusionMessageProvider.setConfiguration(configuration);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);

        assertFalse(exclusionMessageProvider.invoke(mockJobExecutionContext));
        assertEquals(0L, configuration.getQueryTimestamp());
        verifyNoInteractions(mockDataSharingRestService, mockCallBackMessageConsumer);
    }

    @Test
    public void test_last_completed_window_disables_eager_polling() throws Exception {
        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService,
            Clock.fixed(Instant.ofEpochMilli(100000L), ZoneOffset.UTC));
        exclusionMessageProvider.setConfiguration(configuration);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);
        when(mockDataSharingRestService.countExclusions(0L, 99999L, configuration.getModuleNames()))
            .thenReturn(0L);

        assertFalse(exclusionMessageProvider.invoke(mockJobExecutionContext));
        assertEquals(100000L, configuration.getQueryTimestamp());
        verify(mockDataSharingRestService).countExclusions(0L, 99999L, configuration.getModuleNames());
        verify(mockCallBackMessageConsumer).invoke(List.of());
    }

    @Test
    public void test_final_batch_failure_restores_checkpoint_for_retry() throws Exception {
        configuration.setOffset(25);
        when(mockDataSharingRestService.countExclusions(0L, 99999L, configuration.getModuleNames()))
            .thenReturn(50L);
        List<ExclusionEvent> events = createExclusionEvent(50);
        SearchResults<ExclusionEvent> results = createSearchResults(events, 50);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, configuration.getModuleNames(), 100, 0))
            .thenReturn(results);
        doAnswer(invocation -> {
            exclusionMessageProvider.applyPendingCheckpoint();
            // The normal flow must see the staged checkpoint when saving configuration.
            assertEquals(100000L, configuration.getQueryTimestamp());
            assertEquals(0, configuration.getOffset());
            throw new RuntimeException("Queue write failed");
        }).when(mockCallBackMessageConsumer).invoke(events);

        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            fail("Expected failed delivery");
        } catch (EndpointException expected) {
            assertEquals(0L, configuration.getQueryTimestamp());
            assertEquals(25, configuration.getOffset());
        }

        doAnswer(invocation -> {
            exclusionMessageProvider.applyPendingCheckpoint();
            return null;
        }).when(mockCallBackMessageConsumer).invoke(events);
        assertTrue(exclusionMessageProvider.invoke(mockJobExecutionContext));
        assertEquals(100000L, configuration.getQueryTimestamp());
        verify(mockDataSharingRestService, times(2))
            .queryExclusions(0L, 99999L, configuration.getModuleNames(), 100, 0);
    }

    @Test(expected = EndpointException.class)
    public void test_zero_batch_size_is_rejected() throws Exception {
        configuration.setBatchSize(0);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test(expected = EndpointException.class)
    public void test_zero_window_size_is_rejected() throws Exception {
        configuration.setQueryWindowMilliseconds(0);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test
    public void test_incomplete_page_does_not_advance_checkpoint() throws Exception {
        when(mockDataSharingRestService.countExclusions(0L, 99999L, configuration.getModuleNames()))
            .thenReturn(100L);
        SearchResults<ExclusionEvent> results = createSearchResults(createExclusionEvent(50), 100);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, configuration.getModuleNames(), 100, 0))
            .thenReturn(results);
        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            fail("Expected incomplete page to fail rather than skip counted records");
        } catch (EndpointException expected) {
            assertEquals(0L, configuration.getQueryTimestamp());
            verifyNoInteractions(mockCallBackMessageConsumer);
        }
    }

    @Test
    public void test_initial_lookback_is_saved_once_and_survives_a_failed_query() throws Exception {
        configuration.setQueryTimestamp(-1);
        configuration.setInitialLookbackMilliseconds(200000L);
        configuration.setSafetyLagMilliseconds(10000L);
        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService,
            Clock.fixed(Instant.ofEpochMilli(510000L), ZoneOffset.UTC));
        exclusionMessageProvider.setConfiguration(configuration);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);
        when(mockDataSharingRestService.countExclusions(300000L, 399999L, configuration.getModuleNames()))
            .thenThrow(new RuntimeException("Dashboard unavailable"));

        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                exclusionMessageProvider.invoke(mockJobExecutionContext);
                fail("Expected query failure");
            } catch (EndpointException expected) {
                assertEquals(300000L, configuration.getQueryTimestamp());
            }
        }
        verify(mockCallBackMessageConsumer, times(1)).invoke(List.of());
        verify(mockDataSharingRestService, times(2))
            .countExclusions(300000L, 399999L, configuration.getModuleNames());
    }

    @Test
    public void test_safety_lag_waits_until_window_is_searchable() throws Exception {
        configuration.setSafetyLagMilliseconds(10000L);
        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService,
            Clock.fixed(Instant.ofEpochMilli(109999L), ZoneOffset.UTC));
        exclusionMessageProvider.setConfiguration(configuration);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);
        assertFalse(exclusionMessageProvider.invoke(mockJobExecutionContext));
        verifyNoInteractions(mockDataSharingRestService);

        exclusionMessageProvider = new ExclusionMessageProvider(mockDataSharingRestService,
            Clock.fixed(Instant.ofEpochMilli(110000L), ZoneOffset.UTC));
        exclusionMessageProvider.setConfiguration(configuration);
        exclusionMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);
        assertFalse(exclusionMessageProvider.invoke(mockJobExecutionContext));
        verify(mockDataSharingRestService).countExclusions(0L, 99999L, configuration.getModuleNames());
        assertEquals(100000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_adjacent_windows_do_not_deliver_boundary_record_twice() throws Exception {
        // Dashboard endpoints include both bounds; this record belongs to the second window.
        when(mockDataSharingRestService.countExclusions(anyLong(), anyLong(), anyList()))
            .thenAnswer(invocation -> {
                long from = invocation.getArgument(0);
                long to = invocation.getArgument(1);
                return from <= 100000L && to >= 100000L ? 1L : 0L;
            });
        List<ExclusionEvent> events = createExclusionEvent(1);
        SearchResults<ExclusionEvent> page = createSearchResults(events, 1);
        when(mockDataSharingRestService.queryExclusions(anyLong(), anyLong(), anyList(), anyInt(), anyInt()))
            .thenReturn(page);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
        verify(mockCallBackMessageConsumer, times(1)).invoke(events);
        verify(mockDataSharingRestService).queryExclusions(100000L, 199999L, configuration.getModuleNames(), 100, 0);
        assertEquals(200000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_empty_window_checkpoint_failure_does_not_advance() throws Exception {
        doThrow(new RuntimeException("Database unavailable"))
            .when(mockCallBackMessageConsumer).invoke(List.of());
        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            fail("Expected checkpoint failure");
        } catch (EndpointException expected) {
            assertEquals(0L, configuration.getQueryTimestamp());
        }
        verify(mockCallBackMessageConsumer).invoke(List.of());
    }

    @Test
    public void test_initial_checkpoint_callback_failure_restores_uninitialised_timestamp() throws Exception {
        configuration.setQueryTimestamp(-1L);
        doThrow(new RuntimeException("Callback failed"))
            .when(mockCallBackMessageConsumer).invoke(List.of());
        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            fail("Expected initial checkpoint callback failure");
        } catch (EndpointException expected) {
            assertEquals(-1L, configuration.getQueryTimestamp());
            verifyNoInteractions(mockDataSharingRestService);
        }
    }

    @Test
    public void test_saved_timestamp_takes_precedence_over_initial_lookback() throws Exception {
        configuration.setQueryTimestamp(200000L);
        configuration.setInitialLookbackMilliseconds(1L);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
        verify(mockDataSharingRestService).countExclusions(200000L, 299999L, configuration.getModuleNames());
        assertEquals(300000L, configuration.getQueryTimestamp());
        verify(mockCallBackMessageConsumer).invoke(List.of());
    }

    @Test
    public void test_changed_total_count_retries_without_advancing() throws Exception {
        when(mockDataSharingRestService.countExclusions(0L, 99999L, configuration.getModuleNames())).thenReturn(100L);
        SearchResults<ExclusionEvent> page = createSearchResults(createExclusionEvent(100), 101L);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, configuration.getModuleNames(), 100, 0))
            .thenReturn(page);
        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            fail("Expected changing result set to fail");
        } catch (EndpointException expected) {
            assertEquals(0L, configuration.getQueryTimestamp());
            verifyNoInteractions(mockCallBackMessageConsumer);
        }
    }

    @Test(expected = EndpointException.class)
    public void test_negative_safety_lag_is_rejected() throws Exception {
        configuration.setSafetyLagMilliseconds(-1L);
        exclusionMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test
    public void test_configuration_management() {
        // Test setConfiguration and getConfiguration
        ExclusionConsumerConfiguration newConfig = new ExclusionConsumerConfiguration();
        newConfig.setQueryTimestamp(5000L);
        newConfig.setQueryWindowMilliseconds(200000L);
        newConfig.setBatchSize(50);

        exclusionMessageProvider.setConfiguration(newConfig);

        ExclusionConsumerConfiguration retrieved = exclusionMessageProvider.getConfiguration();
        assertNotNull(retrieved);
        assertEquals(5000L, retrieved.getQueryTimestamp());
        assertEquals(200000L, retrieved.getQueryWindowMilliseconds());
        assertEquals(50, retrieved.getBatchSize());
    }

    @Test
    public void test_configured_resource_id_management() {
        // Test setConfiguredResourceId and getConfiguredResourceId
        String resourceId = "error-occurrence-provider-123";
        exclusionMessageProvider.setConfiguredResourceId(resourceId);

        assertEquals(resourceId, exclusionMessageProvider.getConfiguredResourceId());
    }

    @Test
    public void test_callback_consumer_management() {
        // Test setCallBackMessageConsumer
        CallBackMessageConsumer newConsumer = mock(CallBackMessageConsumer.class, withSettings().extraInterfaces(ConfiguredResource.class));
        exclusionMessageProvider.setCallBackMessageConsumer(newConsumer);

        // Verify by invoking - the new consumer should be called
        when(mockDataSharingRestService.countExclusions(anyLong(), anyLong(), anyList()))
            .thenReturn(10L);

        List<ExclusionEvent> events = createExclusionEvent(10);
        SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 10);
        when(mockDataSharingRestService.queryExclusions(anyLong(), anyLong(), anyList(), anyInt(), anyInt()))
            .thenReturn(searchResults);

        try {
            exclusionMessageProvider.invoke(mockJobExecutionContext);
            verify(newConsumer, times(1)).invoke(anyList());
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void test_timestamp_progression_across_multiple_invocations() throws Exception {
        // First invocation
        configuration.setQueryTimestamp(0L);
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);
        List<ExclusionEvent> events1 = createExclusionEvent(50);
        SearchResults<ExclusionEvent> searchResults1 = createSearchResults(events1, 50);
        when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults1);

        exclusionMessageProvider.invoke(mockJobExecutionContext);
        assertEquals(100000L, configuration.getQueryTimestamp());

        // Second invocation with new timestamp
        when(mockDataSharingRestService.countExclusions(100000L, 199999L, Arrays.asList("module1", "module2")))
            .thenReturn(75L);
        List<ExclusionEvent> events2 = createExclusionEvent(75);
        SearchResults<ExclusionEvent> searchResults2 = createSearchResults(events2, 75);
        when(mockDataSharingRestService.queryExclusions(100000L, 199999L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults2);

        exclusionMessageProvider.invoke(mockJobExecutionContext);
        assertEquals(200000L, configuration.getQueryTimestamp());

        // Verify both invocations succeeded
        verify(mockDataSharingRestService, times(2)).countExclusions(anyLong(), anyLong(), anyList());
        verify(mockCallBackMessageConsumer, times(2)).invoke(anyList());
    }

    @Test
    public void test_offset_reset_only_on_last_batch() throws Exception {
        // Setup: 250 events requiring 3 batches
        when(mockDataSharingRestService.countExclusions(0L, 99999L, Arrays.asList("module1", "module2")))
            .thenReturn(250L);

        for (int i = 0; i < 3; i++) {
            int size = (i < 2) ? 100 : 50;
            List<ExclusionEvent> events = createExclusionEvent(size);
            SearchResults<ExclusionEvent> searchResults = createSearchResults(events, 250);
            when(mockDataSharingRestService.queryExclusions(0L, 99999L, Arrays.asList("module1", "module2"), 100, i * 100))
                .thenReturn(searchResults);
        }

        // Initial offset
        assertEquals(0, configuration.getOffset());

        // Execute
        exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Offset should be reset to 0 after processing all batches
        assertEquals(0, configuration.getOffset());

        // Timestamp should be advanced
        assertEquals(100000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_empty_module_list() throws Exception {
        // Setup: empty module list
        configuration.setModuleNames(new ArrayList<>());

        when(mockDataSharingRestService.countExclusions(0L, 99999L, new ArrayList<>()))
            .thenReturn(0L);

        // Execute
        Boolean result = exclusionMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countExclusions(0L, 99999L, new ArrayList<>());
        verify(mockCallBackMessageConsumer).invoke(List.of());
    }

    /**
     * Helper method to create mock ExclusionEvent objects
     */
    private List<ExclusionEvent> createExclusionEvent(int count) {
        List<ExclusionEvent> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ExclusionEvent error = mock(ExclusionEvent.class);
            when(error.getIdentifier()).thenReturn(String.valueOf(i));
            events.add(error);
        }
        return events;
    }

    /**
     * Helper method to create mock SearchResults
     */
    private SearchResults<ExclusionEvent> createSearchResults(List<ExclusionEvent> events, long totalCount) {
        SearchResults<ExclusionEvent> searchResults = mock(SearchResults.class);
        when(searchResults.getResultList()).thenReturn(events);
        when(searchResults.getTotalNumberOfResults()).thenReturn(totalCount);
        return searchResults;
    }
}
