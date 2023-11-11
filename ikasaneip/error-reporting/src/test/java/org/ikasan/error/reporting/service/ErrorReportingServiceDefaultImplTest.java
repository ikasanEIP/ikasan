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
package org.ikasan.error.reporting.service;

import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import jakarta.annotation.Resource;
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for ErrorReportingServiceDefaultImpl based on
 * the implementation of a ErrorReportingService contract.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(locations = {
        "/error-reporting-service-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
})
class ErrorReportingServiceDefaultImplTest
{
    @Resource
    SerialiserFactory serialiserFactory;

    @Resource
    ErrorReportingServiceDao errorReportingServiceDao;

    @Test
    void test_failed_constructor_null_serialiser()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ErrorReportingServiceDefaultImpl(null, null);
        });
    }

    @Test
    void test_failed_constructor_null_dao()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ErrorReportingServiceDefaultImpl(serialiserFactory.getDefaultSerialiser(), null);
        });
    }

    /**
     * Test notify
     */
    @DirtiesContext
            @Test
    void test_errorReporting_notify_no_inflight_event()
    {
        Serialiser serialiser = serialiserFactory.getDefaultSerialiser();
        ErrorReportingService<?,ErrorOccurrenceImpl> errorReportingService = new ErrorReportingServiceDefaultImpl("moduleName", "flowName", serialiser, errorReportingServiceDao);
        String uri = errorReportingService.notify("flowElementName", new Exception("test"));
        ErrorOccurrenceImpl errorOccurrence = errorReportingService.find(uri);
        assertNotNull(errorOccurrence, "Should not be null");
    }

    /**
     * Test notify
     */
    @DirtiesContext
            @Test
    void test_errorReporting_notify_with_inflight_string_event()
    {
        final String event = new String("string based event");

        Serialiser serialiser = serialiserFactory.getDefaultSerialiser();
        ErrorReportingService<String,ErrorOccurrenceImpl> errorReportingService = new ErrorReportingServiceDefaultImpl("moduleName", "flowName", serialiser, errorReportingServiceDao);
        String uri = errorReportingService.notify("flowElementName", event, new Exception("test"));
        ErrorOccurrenceImpl errorOccurrence = errorReportingService.find(uri);
        assertNotNull(errorOccurrence, "Should not be null");
        Object failedEventBytes = errorOccurrence.getEvent();
        Object failedEvent = serialiser.deserialise(failedEventBytes);
        assertEquals(failedEvent, event, "Should be equals");
    }

    /**
     * Test error reporting housekeep
     */
    @DirtiesContext
            @Test
    void test_exclusionService_housekeep()
    {
        final String event = new String("string based event");

        Serialiser serialiser = serialiserFactory.getDefaultSerialiser();
        ErrorReportingService<String,ErrorOccurrenceImpl> errorReportingService = new ErrorReportingServiceDefaultImpl("moduleName", "flowName", serialiser, errorReportingServiceDao);
        String uri = errorReportingService.notify("flowElementName", event, new Exception("test"));
        ErrorOccurrenceImpl errorOccurrence = errorReportingService.find(uri);
        assertNotNull(errorOccurrence, "Should not be null");
        errorReportingService.housekeep();
        errorOccurrence = errorReportingService.find(uri);
        assertNotNull(errorOccurrence, "Should not be null");
    }

}
