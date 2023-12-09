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

import org.ikasan.error.reporting.ErrorReportingAutoConfiguration;
import org.ikasan.error.reporting.ErrorReportingTestAutoConfiguration;
import org.ikasan.error.reporting.dao.ErrorManagementDao;
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.error.reporting.model.ModuleErrorCount;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.persistence.BatchInsert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * Test class for ErrorReportingServiceDefaultImpl based on
 * the implementation of a ErrorReportingService contract.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(classes = {ErrorReportingAutoConfiguration.class, ErrorReportingTestAutoConfiguration.class})
@Sql(scripts = {"/modifyErrorOccurrenceTable.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ErrorReportingManagementServiceImplTest {
    @Resource
    ErrorManagementDao errorManagementDao;

    @Resource
    ErrorReportingServiceDao<ErrorOccurrenceImpl, String> errorReportingServiceDao;

    @Resource 
    ErrorReportingManagementService<ErrorOccurrenceImpl, ModuleErrorCount> errorReportingManagementService;

    Exception exception = new Exception("failed error occurence msg");

    List<String> uris;

    @Before
    public void load() {
        uris = new ArrayList<String>();

        for (int i = 0; i < 1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail",
                    "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            errorReportingServiceDao.save(eo);

            uris.add(eo.getUri());
        }

    }


    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_batch_save_and_find()
    {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl("moduleName", "flowName", "componentName", "error detail", exception.getMessage(), exception.getClass().getName(), ErrorReportingService.DEFAULT_TIME_TO_LIVE);

        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();
        errorOccurrences.add(errorOccurrence);

        ErrorOccurrence persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        Assert.assertNull("Should not be found", persistedErrorOccurrence);

        BatchInsert<ErrorOccurrence> batchInsert = new ErrorReportingManagementServiceImpl(errorManagementDao, errorReportingServiceDao);

        batchInsert.insert(errorOccurrences);

        persistedErrorOccurrence = errorReportingServiceDao.find(errorOccurrence.getUri());
        Assert.assertTrue("Should be found", persistedErrorOccurrence.equals(errorOccurrence));
    }

}
