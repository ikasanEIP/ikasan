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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * Test class for ErrorOccurrence.
 * 
 * @author Ikasan Development Team
 */
public class ErrorOccurrenceTest
{
    Exception exception = new Exception("failed error occurence msg");

    /**
     * Test error occurrence model instance
     */
    @Test
    public void test_new_errorOccurrence()
    {
        ErrorOccurrence<String> errorOccurrence = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), 1000L, "event");
        errorOccurrence.setEventLifeIdentifier("lifeId");
        errorOccurrence.setEventRelatedIdentifier("relatedLifeId");

        Assert.assertTrue(errorOccurrence.getModuleName().equals("moduleName"));
        Assert.assertTrue(errorOccurrence.getFlowName().equals("flowName"));
        Assert.assertTrue(errorOccurrence.getFlowElementName().equals("componentName"));
        Assert.assertTrue(errorOccurrence.getErrorDetail().equals("failed error occurrence text"));
        Assert.assertTrue(errorOccurrence.getErrorMessage().equals("failed error occurence msg"));
        Assert.assertTrue(errorOccurrence.getEvent().equals("event"));
        Assert.assertTrue(errorOccurrence.getExpiry() > System.currentTimeMillis());
        Assert.assertTrue(errorOccurrence.getEventLifeIdentifier().equals("lifeId"));
        Assert.assertTrue(errorOccurrence.getEventRelatedIdentifier().equals("relatedLifeId"));
        Assert.assertTrue(errorOccurrence.getTimestamp() > 0);
        Assert.assertNotNull(errorOccurrence.getUri());
    }

    /**
     * Test error occurrence model instance
     */
    @Test
    public void test_equals()
    {
        ErrorOccurrence<String> errorOccurrence1 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrence<String> errorOccurrence2 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrence<String> errorOccurrence3 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        Assert.assertFalse(errorOccurrence1.equals(errorOccurrence2));
        Assert.assertFalse(errorOccurrence2.equals(errorOccurrence3));
        Assert.assertFalse(errorOccurrence3.equals(errorOccurrence1));
    }

    /**
     * Test error occurrence model instance
     */
    @Test
    public void test_hashcode()
    {
        ErrorOccurrence<String> errorOccurrence1 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrence<String> errorOccurrence2 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        pause(1);
        ErrorOccurrence<String> errorOccurrence3 = new ErrorOccurrence<String>("moduleName", "flowName", "componentName", "failed error occurrence text", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        HashMap<ErrorOccurrence, String> map = new HashMap<ErrorOccurrence, String>();
        map.put(errorOccurrence1, "1");
        map.put(errorOccurrence2, "2");
        map.put(errorOccurrence3, "3");
        map.put(errorOccurrence1, "one");

        Assert.assertTrue(map.size() == 3);
        Assert.assertTrue(map.containsKey(errorOccurrence1));
        Assert.assertTrue(map.containsKey(errorOccurrence2));
        Assert.assertTrue(map.containsKey(errorOccurrence3));
    }

    private void pause(long period)
    {
        try
        {
            Thread.sleep(period);
        }
        catch(InterruptedException e)
        {
            Assert.fail(e.getMessage());
        }
    }
}
