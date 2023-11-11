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
package org.ikasan.error.reporting.model;

import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ErrorOccurrence.
 * 
 * @author Ikasan Development Team
 */
class ErrorOccurrenceImplTest
{
    Exception exception = new Exception("failed error occurence msg");

    /**
     * Test error occurrence window instance
     */
    @Test
    void test_new_errorOccurrence()
    {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), 1000L, "event".getBytes(), "errorString");
        errorOccurrence.setEventLifeIdentifier("lifeId");
        errorOccurrence.setEventRelatedIdentifier("relatedLifeId");

        assertEquals("moduleName", errorOccurrence.getModuleName());
        assertEquals("flowName", errorOccurrence.getFlowName());
        assertEquals("componentName", errorOccurrence.getFlowElementName());
        assertEquals("failed error occurrence text", errorOccurrence.getErrorDetail());
        assertEquals("failed error occurence msg", errorOccurrence.getErrorMessage());
        assertEquals("event", new String(errorOccurrence.getEvent()));
        assertTrue(errorOccurrence.getExpiry() > System.currentTimeMillis());
        assertEquals("lifeId", errorOccurrence.getEventLifeIdentifier());
        assertEquals("relatedLifeId", errorOccurrence.getEventRelatedIdentifier());
        assertTrue(errorOccurrence.getTimestamp() > 0);
        assertNotNull(errorOccurrence.getUri());
    }

    /**
     * Test error occurrence window instance
     */
    @Test
    void test_equals()
    {
        ErrorOccurrenceImpl errorOccurrence1 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrenceImpl errorOccurrence2 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrenceImpl errorOccurrence3 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        assertNotEquals(errorOccurrence1, errorOccurrence2);
        assertNotEquals(errorOccurrence2, errorOccurrence3);
        assertNotEquals(errorOccurrence3, errorOccurrence1);
    }

    /**
     * Test error occurrence window instance
     */
    @Test
    void test_hashcode()
    {
        ErrorOccurrenceImpl errorOccurrence1 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrenceImpl errorOccurrence2 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrenceImpl errorOccurrence3 = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        HashMap<ErrorOccurrenceImpl, String> map = new HashMap<ErrorOccurrenceImpl, String>();
        map.put(errorOccurrence1, "1");
        map.put(errorOccurrence2, "2");
        map.put(errorOccurrence3, "3");
        map.put(errorOccurrence1, "one");

        assertEquals(3, map.size());
        assertTrue(map.containsKey(errorOccurrence1));
        assertTrue(map.containsKey(errorOccurrence2));
        assertTrue(map.containsKey(errorOccurrence3));
    }

    private void pause(long period)
    {
        assertDoesNotThrow(() -> {
            Thread.sleep(period);
        });
    }
}
