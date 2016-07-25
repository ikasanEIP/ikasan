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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.error.reporting.dao.ErrorManagementDao;
import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.ModuleErrorCount;
import org.ikasan.error.reporting.model.Note;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for ErrorReportingServiceDefaultImpl based on
 * the implementation of a ErrorReportingService contract.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/error-reporting-service-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
})

public class ErrorReportingManagementServiceImplTest {
    @Resource
    ErrorManagementDao errorManagementDao;

    @Resource
    ErrorReportingServiceDao<ErrorOccurrence, String> errorReportingServiceDao;

    @Resource 
    ErrorReportingManagementService<ErrorOccurrence, Note, ErrorOccurrenceNote, ModuleErrorCount> errorReportingManagementService;

    List<String> uris;

    @Before
    public void load() {
        uris = new ArrayList<String>();

        for (int i = 0; i < 1000; i++) {
            ErrorOccurrence eo = new ErrorOccurrence("moduleName", "flowName", "flowElementName", "errorDetail",
                    "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            errorReportingServiceDao.save(eo);

            uris.add(eo.getUri());
        }

    }

    @Test
    @DirtiesContext
    public void test_close_error_occurrences() {

        errorReportingManagementService.close(uris, "this is a note", "username");

        List<String> moduleNames = new ArrayList<>();
        moduleNames.add("moduleName");

        List<String> flowNames = new ArrayList<>();
        flowNames.add("flowName");

        List<String> flowElementNames = new ArrayList<>();
        flowElementNames.add("flowElementName");

        int numberOfErrorsFound = errorReportingServiceDao.find(moduleNames, flowNames, flowElementNames, null, null, 1000).size();
        Assert.assertTrue("Expected 0 errors after closing them all, got " + numberOfErrorsFound, numberOfErrorsFound == 0);
    }


}
