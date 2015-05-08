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
package org.ikasan.error.reporting.dao;

import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/error-reporting-service-conf.xml",
        "/mock-conf.xml",
        "/h2db-datasource-conf.xml"
        })

public class HibernateErrorReportingServiceDaoTest
{
    @Resource
    ErrorReportingServiceDao<ErrorOccurrence> errorReportingServiceDao;

    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_save_and_find()
    {
        ErrorOccurrence errorOccurrence = new ErrorOccurrence("moduleName", "flowName", "componentName", "error detail", ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        ErrorOccurrence persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        Assert.assertNull("Should not be found", persistedErrorOccurrence);

        errorReportingServiceDao.save(errorOccurrence);
        persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        Assert.assertTrue("Should be found", persistedErrorOccurrence.equals(errorOccurrence));
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_deleteExpired_operation()
    {
        // new event with 1 milli expiry
        ErrorOccurrence errorOccurrenceExpired = new ErrorOccurrence("moduleName", "flowName", "componentName", "error detail", 1L);

        try
        {
            Thread.sleep(1);
        }
        catch(InterruptedException e)
        {
            Assert.fail("sleep woken early!");
        }

        ErrorOccurrence errorOccurrence = new ErrorOccurrence("moduleName", "flowName", "componentName", "error detail", ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        Assert.assertNull("Non expired should not be found", errorReportingServiceDao.find(errorOccurrence.getUri()));
        Assert.assertNull("Expired should not be found", errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) );

        errorReportingServiceDao.save(errorOccurrence);
        errorReportingServiceDao.save(errorOccurrenceExpired);
        Assert.assertNotNull("Non expired should not be found", errorReportingServiceDao.find(errorOccurrence.getUri()));
        Assert.assertNotNull("Expired should not be found", errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) );

        try
        {
            Thread.sleep(100);
        }
        catch(InterruptedException e)
        {
            Assert.fail("sleep woken early!");
        }

        errorReportingServiceDao.deleteExpired();
        Assert.assertNull("Expired should not be found", errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) );
        Assert.assertNotNull("Non expired should be found", errorReportingServiceDao.find(errorOccurrence.getUri()));
    }

}