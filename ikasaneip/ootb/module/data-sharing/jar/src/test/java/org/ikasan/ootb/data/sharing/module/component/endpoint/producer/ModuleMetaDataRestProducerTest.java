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
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ModuleMetaDataRestProducer.
 *
 * Tests cover:
 * - Constructor validation
 * - Successful module metadata publishing (single and multiple module metadata)
 * - Failure scenarios (false return, exceptions)
 * - Interaction with DashboardRestService
 * - Error message content
 */
public class ModuleMetaDataRestProducerTest {

    private ModuleMetaDataRestProducer producer;
    private DashboardRestService<ModuleMetaData> mockDashboardRestService;

    @Before
    public void setUp() {
        mockDashboardRestService = mock(DashboardRestService.class);
        producer = new ModuleMetaDataRestProducer(mockDashboardRestService);
    }

    /**
     * Test that constructor validates non-null DashboardRestService parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_throws_exception_when_service_is_null() {
        new ModuleMetaDataRestProducer(null);
    }

    /**
     * Test that constructor accepts valid DashboardRestService.
     */
    @Test
    public void test_constructor_accepts_valid_service() {
        DashboardRestService<ModuleMetaData> service = mock(DashboardRestService.class);
        ModuleMetaDataRestProducer testProducer = new ModuleMetaDataRestProducer(service);
        assertNotNull(testProducer);
    }

    /**
     * Test successful publishing of a single module metadata.
     */
    @Test
    public void test_invoke_publishes_single_module_metadata_successfully() throws EndpointException {
        // Setup
        ModuleMetaData moduleMetaData = mock(ModuleMetaData.class);
        ModuleMetaData moduleMetaDatas = moduleMetaData;

        when(mockDashboardRestService.publish(moduleMetaDatas)).thenReturn(true);

        // Execute
        producer.invoke(moduleMetaDatas);

        // Verify
        verify(mockDashboardRestService, times(1)).publish(moduleMetaDatas);
    }

    /**
     * Test separate publishing calls for multiple modules.
     */
    @Test
    public void test_invoke_publishes_multiple_modules_as_separate_calls() {
        ModuleMetaData first = mock(ModuleMetaData.class);
        ModuleMetaData second = mock(ModuleMetaData.class);
        when(mockDashboardRestService.publish(any())).thenReturn(true);
        producer.invoke(first);
        producer.invoke(second);
        verify(mockDashboardRestService).publish(first);
        verify(mockDashboardRestService).publish(second);
    }

    /**
     * Test that EndpointException is thrown when publish returns false.
     */
    @Test
    public void test_invoke_throws_exception_when_publish_returns_false() {
        // Setup
        ModuleMetaData moduleMetaDatas = mock(ModuleMetaData.class);
        when(mockDashboardRestService.publish(moduleMetaDatas)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(moduleMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertTrue(e.getMessage().contains("Could not publish module metadata to the dashboard."));
            assertTrue(e.getMessage().contains("dashboard extract is enabled"));
        }

        verify(mockDashboardRestService, times(1)).publish(moduleMetaDatas);
    }

    /**
     * Test that EndpointException is thrown when DashboardRestService throws RuntimeException.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_runtime_exception() {
        // Setup
        ModuleMetaData moduleMetaDatas = mock(ModuleMetaData.class);
        RuntimeException cause = new RuntimeException("Connection timeout");
        when(mockDashboardRestService.publish(moduleMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(moduleMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }

        verify(mockDashboardRestService, times(1)).publish(moduleMetaDatas);
    }

    /**
     * Test that EndpointException is thrown for IllegalArgumentException from service.
     */
    @Test
    public void test_invoke_throws_exception_when_service_throws_illegal_argument() {
        // Setup
        ModuleMetaData moduleMetaDatas = mock(ModuleMetaData.class);
        IllegalArgumentException cause = new IllegalArgumentException("Invalid module metadata format");
        when(mockDashboardRestService.publish(moduleMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(moduleMetaDatas);
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
        ModuleMetaData moduleMetaDatas = mock(ModuleMetaData.class);
        NullPointerException cause = new NullPointerException("Service configuration is null");
        when(mockDashboardRestService.publish(moduleMetaDatas)).thenThrow(cause);

        // Execute and verify
        try {
            producer.invoke(moduleMetaDatas);
            fail("Expected EndpointException to be thrown");
        } catch (EndpointException e) {
            assertEquals(cause, e.getCause());
        }
    }

    /**
     * Test that the exact module metadata object is passed to DashboardRestService.
     */
    @Test
    public void test_invoke_passes_exact_module_metadata_to_service() {
        ModuleMetaData metadata = mock(ModuleMetaData.class);
        when(mockDashboardRestService.publish(any())).thenReturn(true);
        producer.invoke(metadata);
        verify(mockDashboardRestService).publish(same(metadata));
    }

    /**
     * Test multiple successful invocations.
     */
    @Test
    public void test_multiple_invocations_all_successful() throws EndpointException {
        // Setup
        ModuleMetaData moduleMetaDatas1 = mock(ModuleMetaData.class);
        ModuleMetaData moduleMetaDatas2 = mock(ModuleMetaData.class);
        ModuleMetaData moduleMetaDatas3 = mock(ModuleMetaData.class);

        when(mockDashboardRestService.publish(any())).thenReturn(true);

        // Execute
        producer.invoke(moduleMetaDatas1);
        producer.invoke(moduleMetaDatas2);
        producer.invoke(moduleMetaDatas3);

        // Verify
        verify(mockDashboardRestService, times(3)).publish(any());
    }

    /**
     * Test that producer can recover after a failed invocation.
     */
    @Test
    public void test_producer_recovers_after_failure() throws EndpointException {
        // Setup
        ModuleMetaData moduleMetaDatas1 = mock(ModuleMetaData.class);
        ModuleMetaData moduleMetaDatas2 = mock(ModuleMetaData.class);

        when(mockDashboardRestService.publish(moduleMetaDatas1)).thenReturn(false);
        when(mockDashboardRestService.publish(moduleMetaDatas2)).thenReturn(true);

        // Execute first invocation (should fail)
        try {
            producer.invoke(moduleMetaDatas1);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            // Expected
        }

        // Execute second invocation (should succeed)
        producer.invoke(moduleMetaDatas2);

        // Verify both attempts were made
        verify(mockDashboardRestService, times(1)).publish(moduleMetaDatas1);
        verify(mockDashboardRestService, times(1)).publish(moduleMetaDatas2);
    }

    /**
     * Test that null module metadata is passed through to service.
     * The service should handle null appropriately.
     */
    @Test
    public void test_invoke_with_null_moduleMetaData() {
        // Setup
        when(mockDashboardRestService.publish(null)).thenThrow(new NullPointerException("ModuleMetaData cannot be null"));

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
        ModuleMetaData moduleMetaData = mock(ModuleMetaData.class);
        when(mockDashboardRestService.publish(moduleMetaData)).thenReturn(false);

        // Execute and verify
        try {
            producer.invoke(moduleMetaData);
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
