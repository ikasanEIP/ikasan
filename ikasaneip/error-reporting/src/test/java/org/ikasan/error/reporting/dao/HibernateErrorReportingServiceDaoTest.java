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

import jakarta.annotation.Resource;
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(locations = {
        "/ikasan-transaction-conf.xml",
        "/error-reporting-service-conf.xml",
        "/mock-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
})
class HibernateErrorReportingServiceDaoTest
{
    @Resource
    ErrorReportingServiceDao<ErrorOccurrence, String> errorReportingServiceDao;

    Exception exception = new Exception("failed error occurence msg");

    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
            @Test
    void test_save_and_find()
    {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "error detail", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        ErrorOccurrence persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        assertNull(persistedErrorOccurrence, "Should not be found");

        errorReportingServiceDao.save(errorOccurrence);
        persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        assertEquals(persistedErrorOccurrence, errorOccurrence, "Should be found");
    }

    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
            @Test
    void test_batch_save_and_find()
    {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "error detail", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();
        errorOccurrences.add(errorOccurrence);

        ErrorOccurrence persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        assertNull(persistedErrorOccurrence, "Should not be found");

        errorReportingServiceDao.save(errorOccurrences);

        persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        assertEquals(persistedErrorOccurrence, errorOccurrence, "Should be found");
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
            @Test
    void test_deleteExpired_operation()
    {
        // new event with 1 milli expiry
        ErrorOccurrenceImpl errorOccurrenceExpired = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "error detail", exception.getMessage(), exception.getClass().getName(), 1L);

        assertDoesNotThrow(() -> {
            Thread.sleep(1);
        }, "sleep woken early!");

        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "error detail", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        assertNull(errorReportingServiceDao.find(errorOccurrence.getUri()), "Non expired should not be found");
        assertNull(errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) , "Expired should not be found");

        errorReportingServiceDao.save(errorOccurrence);
        errorReportingServiceDao.save(errorOccurrenceExpired);
        assertNotNull(errorReportingServiceDao.find(errorOccurrence.getUri()), "Non expired should not be found");
        assertNotNull(errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) , "Expired should not be found");

        assertDoesNotThrow(() -> {
            Thread.sleep(100);
        }, "sleep woken early!");

        errorReportingServiceDao.deleteExpired();
        assertNull(errorReportingServiceDao.find(errorOccurrenceExpired.getUri()) , "Expired should not be found");
        assertNotNull(errorReportingServiceDao.find(errorOccurrence.getUri()), "Non expired should be found");
    }

}