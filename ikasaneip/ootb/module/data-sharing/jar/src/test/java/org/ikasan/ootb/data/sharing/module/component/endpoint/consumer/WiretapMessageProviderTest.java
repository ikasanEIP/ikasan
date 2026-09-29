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
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.WiretapConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for WiretapMessageProvider.
 *
 * Tests cover:
 * - Configuration management
 * - Single batch retrieval
 * - Multiple batch retrieval with paging
 * - Timestamp and offset management
 * - Error handling
 * - CallBackMessageConsumer interaction
 */
public class WiretapMessageProviderTest {

    private WiretapMessageProvider wiretapMessageProvider;
    private DataSharingRestService mockDataSharingRestService;
    private CallBackMessageConsumer mockCallBackMessageConsumer;
    private WiretapConsumerConfiguration configuration;
    private JobExecutionContext mockJobExecutionContext;

    @Before
    public void setUp() {
        mockDataSharingRestService = mock(DataSharingRestService.class);
        mockCallBackMessageConsumer = mock(CallBackMessageConsumer.class);
        mockJobExecutionContext = mock(JobExecutionContext.class);

        wiretapMessageProvider = new WiretapMessageProvider(mockDataSharingRestService);
        wiretapMessageProvider.setCallBackMessageConsumer(mockCallBackMessageConsumer);

        configuration = new WiretapConsumerConfiguration();
        configuration.setQueryTimestamp(0L);
        configuration.setQueryWindowMilliseconds(100000L);
        configuration.setBatchSize(100);
        configuration.setOffset(0);
        configuration.setModuleNames(Arrays.asList("module1", "module2"));

        wiretapMessageProvider.setConfiguration(configuration);
    }

    @Test
    public void test_invoke_with_no_results() throws Exception {
        // Setup: count returns 0
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(0L);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, never()).queryWiretap(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer, never()).invoke(anyList());
    }

    @Test
    public void test_invoke_with_single_batch() throws Exception {
        // Setup: count returns 50 (less than batch size of 100)
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);

        List<WiretapEvent> events = createWiretapEvents(50);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 50);

        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0);

        ArgumentCaptor<List<WiretapEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockCallBackMessageConsumer, times(1)).invoke(captor.capture());
        assertEquals(50, captor.getValue().size());

        // Verify timestamp was advanced and offset reset
        assertEquals(100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_exact_batch_size() throws Exception {
        // Setup: count returns exactly 100 (equals batch size)
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(100L);

        List<WiretapEvent> events = createWiretapEvents(100);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 100);

        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0);
        verify(mockCallBackMessageConsumer, times(1)).invoke(anyList());

        // Note: When count equals batchSize exactly, the timestamp is NOT advanced
        // because the condition is (offset + batchSize > count), not (>=)
        assertEquals(0L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_multiple_batches() throws Exception {
        // Setup: count returns 250 (requires 3 batches: 100, 100, 50)
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(250L);

        // First batch: 100 events
        List<WiretapEvent> events1 = createWiretapEvents(100);
        SearchResults<WiretapEvent> searchResults1 = createSearchResults(events1, 250);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults1);

        // Second batch: 100 events
        List<WiretapEvent> events2 = createWiretapEvents(100);
        SearchResults<WiretapEvent> searchResults2 = createSearchResults(events2, 250);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 100))
            .thenReturn(searchResults2);

        // Third batch: 50 events
        List<WiretapEvent> events3 = createWiretapEvents(50);
        SearchResults<WiretapEvent> searchResults3 = createSearchResults(events3, 250);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 200))
            .thenReturn(searchResults3);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(3)).queryWiretap(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer, times(3)).invoke(anyList());

        // Verify timestamp was advanced and offset reset after last batch
        assertEquals(100000L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_large_dataset() throws Exception {
        // Setup: count returns 1000 (requires 10 batches of 100)
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(1000L);

        // Create mocks for all 10 batches
        for (int i = 0; i < 10; i++) {
            List<WiretapEvent> events = createWiretapEvents(100);
            SearchResults<WiretapEvent> searchResults = createSearchResults(events, 1000);
            when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, i * 100))
                .thenReturn(searchResults);
        }

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(10)).queryWiretap(anyLong(), anyLong(), anyList(), anyInt(), anyInt());
        verify(mockCallBackMessageConsumer, times(10)).invoke(anyList());

        // Note: When count is exactly divisible by batchSize, timestamp is NOT advanced
        // because the condition is (offset + batchSize > count), not (>=)
        assertEquals(0L, configuration.getQueryTimestamp());
        assertEquals(0, configuration.getOffset());
    }

    @Test
    public void test_invoke_with_custom_batch_size() throws Exception {
        // Setup: custom batch size of 50
        configuration.setBatchSize(50);

        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(125L);

        // Mock 3 batches: 50, 50, 25
        for (int i = 0; i < 3; i++) {
            int size = (i < 2) ? 50 : 25;
            List<WiretapEvent> events = createWiretapEvents(size);
            SearchResults<WiretapEvent> searchResults = createSearchResults(events, 125);
            when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 50, i * 50))
                .thenReturn(searchResults);
        }

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(3)).queryWiretap(anyLong(), anyLong(), anyList(), eq(50), anyInt());
        verify(mockCallBackMessageConsumer, times(3)).invoke(anyList());
    }

    @Test
    public void test_invoke_with_custom_time_window() throws Exception {
        // Setup: custom query window of 50000ms
        configuration.setQueryWindowMilliseconds(50000L);
        configuration.setQueryTimestamp(1000L);

        when(mockDataSharingRestService.countWiretap(1000L, 51000L, Arrays.asList("module1", "module2")))
            .thenReturn(75L);

        List<WiretapEvent> events = createWiretapEvents(75);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 75);
        when(mockDataSharingRestService.queryWiretap(1000L, 51000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(1000L, 51000L, Arrays.asList("module1", "module2"));
        verify(mockDataSharingRestService, times(1)).queryWiretap(1000L, 51000L, Arrays.asList("module1", "module2"), 100, 0);

        // Verify timestamp was advanced by window size
        assertEquals(51000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_invoke_with_single_module() throws Exception {
        // Setup: single module
        configuration.setModuleNames(Arrays.asList("singleModule"));

        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("singleModule")))
            .thenReturn(30L);

        List<WiretapEvent> events = createWiretapEvents(30);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 30);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("singleModule"), 100, 0))
            .thenReturn(searchResults);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, Arrays.asList("singleModule"));
        verify(mockDataSharingRestService, times(1)).queryWiretap(0L, 100000L, Arrays.asList("singleModule"), 100, 0);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_count_error() throws Exception {
        // Setup: countWiretap throws exception
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenThrow(new RuntimeException("Connection error"));

        // Execute - should throw EndpointException
        wiretapMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_query_error() throws Exception {
        // Setup: count succeeds but query throws exception
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(100L);

        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenThrow(new RuntimeException("Query failed"));

        // Execute - should throw EndpointException
        wiretapMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_throws_exception_on_consumer_error() throws Exception {
        // Setup: everything succeeds until consumer throws exception
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);

        List<WiretapEvent> events = createWiretapEvents(50);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 50);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults);

        doThrow(new RuntimeException("Consumer processing error"))
            .when(mockCallBackMessageConsumer).invoke(anyList());

        // Execute - should throw EndpointException
        wiretapMessageProvider.invoke(mockJobExecutionContext);
    }

    @Test
    public void test_configuration_management() {
        // Test setConfiguration and getConfiguration
        WiretapConsumerConfiguration newConfig = new WiretapConsumerConfiguration();
        newConfig.setQueryTimestamp(5000L);
        newConfig.setQueryWindowMilliseconds(200000L);
        newConfig.setBatchSize(50);

        wiretapMessageProvider.setConfiguration(newConfig);

        WiretapConsumerConfiguration retrieved = wiretapMessageProvider.getConfiguration();
        assertNotNull(retrieved);
        assertEquals(5000L, retrieved.getQueryTimestamp());
        assertEquals(200000L, retrieved.getQueryWindowMilliseconds());
        assertEquals(50, retrieved.getBatchSize());
    }

    @Test
    public void test_configured_resource_id_management() {
        // Test setConfiguredResourceId and getConfiguredResourceId
        String resourceId = "wiretap-provider-123";
        wiretapMessageProvider.setConfiguredResourceId(resourceId);

        assertEquals(resourceId, wiretapMessageProvider.getConfiguredResourceId());
    }

    @Test
    public void test_callback_consumer_management() {
        // Test setCallBackMessageConsumer
        CallBackMessageConsumer newConsumer = mock(CallBackMessageConsumer.class);
        wiretapMessageProvider.setCallBackMessageConsumer(newConsumer);

        // Verify by invoking - the new consumer should be called
        when(mockDataSharingRestService.countWiretap(anyLong(), anyLong(), anyList()))
            .thenReturn(10L);

        List<WiretapEvent> events = createWiretapEvents(10);
        SearchResults<WiretapEvent> searchResults = createSearchResults(events, 10);
        when(mockDataSharingRestService.queryWiretap(anyLong(), anyLong(), anyList(), anyInt(), anyInt()))
            .thenReturn(searchResults);

        try {
            wiretapMessageProvider.invoke(mockJobExecutionContext);
            verify(newConsumer, times(1)).invoke(anyList());
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void test_timestamp_progression_across_multiple_invocations() throws Exception {
        // First invocation
        configuration.setQueryTimestamp(0L);
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(50L);
        List<WiretapEvent> events1 = createWiretapEvents(50);
        SearchResults<WiretapEvent> searchResults1 = createSearchResults(events1, 50);
        when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults1);

        wiretapMessageProvider.invoke(mockJobExecutionContext);
        assertEquals(100000L, configuration.getQueryTimestamp());

        // Second invocation with new timestamp
        when(mockDataSharingRestService.countWiretap(100000L, 200000L, Arrays.asList("module1", "module2")))
            .thenReturn(75L);
        List<WiretapEvent> events2 = createWiretapEvents(75);
        SearchResults<WiretapEvent> searchResults2 = createSearchResults(events2, 75);
        when(mockDataSharingRestService.queryWiretap(100000L, 200000L, Arrays.asList("module1", "module2"), 100, 0))
            .thenReturn(searchResults2);

        wiretapMessageProvider.invoke(mockJobExecutionContext);
        assertEquals(200000L, configuration.getQueryTimestamp());

        // Verify both invocations succeeded
        verify(mockDataSharingRestService, times(2)).countWiretap(anyLong(), anyLong(), anyList());
        verify(mockCallBackMessageConsumer, times(2)).invoke(anyList());
    }

    @Test
    public void test_offset_reset_only_on_last_batch() throws Exception {
        // Setup: 250 events requiring 3 batches
        when(mockDataSharingRestService.countWiretap(0L, 100000L, Arrays.asList("module1", "module2")))
            .thenReturn(250L);

        for (int i = 0; i < 3; i++) {
            int size = (i < 2) ? 100 : 50;
            List<WiretapEvent> events = createWiretapEvents(size);
            SearchResults<WiretapEvent> searchResults = createSearchResults(events, 250);
            when(mockDataSharingRestService.queryWiretap(0L, 100000L, Arrays.asList("module1", "module2"), 100, i * 100))
                .thenReturn(searchResults);
        }

        // Initial offset
        assertEquals(0, configuration.getOffset());

        // Execute
        wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Offset should be reset to 0 after processing all batches
        assertEquals(0, configuration.getOffset());

        // Timestamp should be advanced
        assertEquals(100000L, configuration.getQueryTimestamp());
    }

    @Test
    public void test_empty_module_list() throws Exception {
        // Setup: empty module list
        configuration.setModuleNames(new ArrayList<>());

        when(mockDataSharingRestService.countWiretap(0L, 100000L, new ArrayList<>()))
            .thenReturn(0L);

        // Execute
        Boolean result = wiretapMessageProvider.invoke(mockJobExecutionContext);

        // Assert
        assertTrue(result);
        verify(mockDataSharingRestService, times(1)).countWiretap(0L, 100000L, new ArrayList<>());
        verify(mockCallBackMessageConsumer, never()).invoke(anyList());
    }

    /**
     * Helper method to create mock WiretapEvent objects
     */
    private List<WiretapEvent> createWiretapEvents(int count) {
        List<WiretapEvent> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            WiretapEvent event = mock(WiretapEvent.class);
            when(event.getIdentifier()).thenReturn((long) i);
            events.add(event);
        }
        return events;
    }

    /**
     * Helper method to create mock SearchResults
     */
    private SearchResults<WiretapEvent> createSearchResults(List<WiretapEvent> events, long totalCount) {
        SearchResults<WiretapEvent> searchResults = mock(SearchResults.class);
        when(searchResults.getResultList()).thenReturn(events);
        when(searchResults.getTotalNumberOfResults()).thenReturn(totalCount);
        return searchResults;
    }
}
