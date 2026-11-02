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
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
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
 * Comprehensive unit tests for ConfigurationMetaDataRestProducer.
 *
 * Tests cover:
 * - Constructor validation
 * - Successful configuration metadata publishing (single and multiple configuration metadata)
 * - Empty configuration metadata list handling
 * - Failure scenarios (false return, exceptions)
 * - Interaction with DashboardRestService
 * - Error message content
 */
public class ConfigurationMetaDataRestProducerTest {

    private ConfigurationMetaDataRestProducer producer;
    private DashboardRestService<List<ConfigurationMetaData>> mockDashboardRestService;

    @Before
    public void setUp() {
        mockDashboardRestService = mock(DashboardRestService.class);
        producer = new ConfigurationMetaDataRestProducer(mockDashboardRestService);
    }

    /**
     * Test that constructor validates non-null DashboardRestService parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_throws_exception_when_service_is_null() {
        new ConfigurationMetaDataRestProducer(null);
    }

    /**
     * Test that constructor accepts valid DashboardRestService.
     */
    @Test
    public void test_constructor_accepts_valid_service() {
        DashboardRestService<List<ConfigurationMetaData>> service = mock(DashboardRestService.class);
        ConfigurationMetaDataRestProducer testProducer = new ConfigurationMetaDataRestProducer(service);
        assertNotNull(testProducer);
    }

    /**
     * Test successful publishing of a single configuration metadata.
     */
    @Test
    public void test_invoke_publishes_single_configuration_metadata_successfully() throws EndpointException {
        // Setup
        ConfigurationMetaData configurationMetaData = mock(ConfigurationMetaData.class);
        List<ConfigurationMetaData> configurationMetaDatas = Collections.singletonList(configurationMetaData);

        when(mockDashboardRestService.publish(configurationMetaDatas)).thenReturn(true);

        // Execute
        producer.invoke(configurationMetaDatas);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas);
    }

    /**
     * Test successful publishing of multiple configuration metadata.
     */
    @Test
    public void test_invoke_publishes_multiple_configuration_metadata_successfully() throws EndpointException {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas = Arrays.asList(
            mock(ConfigurationMetaData.class),
            mock(ConfigurationMetaData.class),
            mock(ConfigurationMetaData.class)
        );

        when(mockDashboardRestService.publish(configurationMetaDatas)).thenReturn(true);

        // Execute
        producer.invoke(configurationMetaDatas);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas);
    }

    /**
         * Test publishing of empty configuration metadata list.
     */
    @Test
    public void test_invoke_handles_empty_configuration_metadata_list() throws EndpointException {
        // Setup
        List<ConfigurationMetaData> emptyList = Collections.emptyList();
        when(mockDashboardRestService.publish(emptyList)).thenReturn(true);

        // Execute
        producer.invoke(emptyList);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(emptyList);
    }

    /**
     * Test publishing of large batch of configuration metadata.
     */
    @Test
    public void test_invoke_publishes_large_batch_successfully() throws EndpointException {
        // Setup: Create a large list of configuration metadata
        List<ConfigurationMetaData> largeConfigurationMetaDataList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeConfigurationMetaDataList.add(mock(ConfigurationMetaData.class));
        }

        when(mockDashboardRestService.publish(largeConfigurationMetaDataList)).thenReturn(true);

        // Execute
        producer.invoke(largeConfigurationMetaDataList);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(largeConfigurationMetaDataList);
    }

    /**
     * Test that EndpointException is thrown when publish returns false.
     */
    @Test
    public void test_invoke_throws_exception_when_publish_returns_false() {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas = Collections.singletonList(mock(ConfigurationMetaData.class));
        when(mockDashboardRestService.publish(configurationMetaDatas)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(configurationMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertTrue(e.getMessage().contains("Could not publish configuration metadata to the dashboard."));
            assertTrue(e.getMessage().contains("dashboard extract is enabled"));
        }

        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas);
    }

    /**
     * Test that EndpointException is thrown when DashboardRestService throws RuntimeException.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_runtime_exception() {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas = Collections.singletonList(mock(ConfigurationMetaData.class));
        RuntimeException cause = new RuntimeException("Connection timeout");
        when(mockDashboardRestService.publish(configurationMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(configurationMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }

        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas);
    }

    /**
     * Test that EndpointException is thrown for IllegalArgumentException from service.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_illegal_argument() {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas = Collections.singletonList(mock(ConfigurationMetaData.class));
        IllegalArgumentException cause = new IllegalArgumentException("Invalid configuration metadata format");
        when(mockDashboardRestService.publish(configurationMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(configurationMetaDatas);
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
        List<ConfigurationMetaData> configurationMetaDatas = Collections.singletonList(mock(ConfigurationMetaData.class));
        NullPointerException cause = new NullPointerException("Service configuration is null");
        when(mockDashboardRestService.publish(configurationMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(configurationMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }
    }

    /**
     * Test that the exact configuration metadata list is passed to DashboardRestService.
     */
    @Test
    public void test_invoke_passes_exact_configuration_metadata_list_to_service() throws EndpointException {
        // Setup
        ConfigurationMetaData configurationMetaData1 = mock(ConfigurationMetaData.class);
        ConfigurationMetaData configurationMetaData2 = mock(ConfigurationMetaData.class);
        List<ConfigurationMetaData> configurationMetaDatas = Arrays.asList(configurationMetaData1, configurationMetaData2);

        ArgumentCaptor<List<ConfigurationMetaData>> captor = ArgumentCaptor.forClass(List.class);
        when(mockDashboardRestService.publish(any())).thenReturn(true);

        // Execute
        producer.invoke(configurationMetaDatas);

        // Verify
        verify(mockDashboardRestService).publish(captor.capture());
        List<ConfigurationMetaData> capturedconfigurationMetaDatas = captor.getValue();
        assertEquals(2, capturedconfigurationMetaDatas.size());
        assertSame(configurationMetaData1, capturedconfigurationMetaDatas.get(0));
        assertSame(configurationMetaData2, capturedconfigurationMetaDatas.get(1));
    }

    /**
     * Test multiple successful invocations.
     */
    @Test
    public void test_multiple_invocations_all_successful() throws EndpointException {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas1 = Collections.singletonList(mock(ConfigurationMetaData.class));
        List<ConfigurationMetaData> configurationMetaDatas2 = Arrays.asList(mock(ConfigurationMetaData.class), mock(ConfigurationMetaData.class));
        List<ConfigurationMetaData> configurationMetaDatas3 = Collections.singletonList(mock(ConfigurationMetaData.class));

        when(mockDashboardRestService.publish(any())).thenReturn(true);

        // Execute
        producer.invoke(configurationMetaDatas1);
        producer.invoke(configurationMetaDatas2);
        producer.invoke(configurationMetaDatas3);

        // Verify
        verify(mockDashboardRestService, times(3)).publish(any());
    }

    /**
     * Test that producer can recover after a failed invocation.
     */
    @Test
    public void test_producer_recovers_after_failure() throws EndpointException {
        // Setup
        List<ConfigurationMetaData> configurationMetaDatas1 = Collections.singletonList(mock(ConfigurationMetaData.class));
        List<ConfigurationMetaData> configurationMetaDatas2 = Collections.singletonList(mock(ConfigurationMetaData.class));

        when(mockDashboardRestService.publish(configurationMetaDatas1)).thenReturn(false);
        when(mockDashboardRestService.publish(configurationMetaDatas2)).thenReturn(true);

        // Execute first invocation (should fail)
        try {
            producer.invoke(configurationMetaDatas1);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            // Expected
        }

        // Execute second invocation (should succeed)
        producer.invoke(configurationMetaDatas2);

        // Verify both attempts were made
        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas1);
        verify(mockDashboardRestService, times(1)).publish(configurationMetaDatas2);
    }

    /**
     * Test that null configurationMetaData list is passed through to service.
     * The service should handle null appropriately.
     */
    @Test
    public void test_invoke_with_null_configurationMetaData_list() {
        // Setup
        when(mockDashboardRestService.publish(null)).thenThrow(new NullPointerException("ConfigurationMetaData list cannot be null"));

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
        List<ConfigurationMetaData> configurationMetaData = Collections.singletonList(mock(ConfigurationMetaData.class));
        when(mockDashboardRestService.publish(configurationMetaData)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(configurationMetaData);
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
