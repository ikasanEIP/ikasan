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
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class HibernateErrorManagementDaoTest
{    
    @Resource
    ErrorManagementDao errorManagementDao;
    
    @Resource
	ErrorReportingServiceDao errorReportingServiceDao;

    Exception exception = new Exception("error message");


    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
            @Test
    void test_save_find_and_delete_error_occurrence()
    {
    	ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo1 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo2 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	
    	this.errorReportingServiceDao.save(eo);
    	this.errorReportingServiceDao.save(eo1);
    	this.errorReportingServiceDao.save(eo2);
    	
    	List<String> uris = new ArrayList<String>();
    	uris.add(eo.getUri());
    	uris.add(eo1.getUri());
    	uris.add(eo2.getUri());
    	
    	List<ErrorOccurrence> eoList = this.errorManagementDao.findErrorOccurrences(uris);

        assertEquals(3, eoList.size());
    	
    	this.errorManagementDao.deleteErrorOccurence(eo);
    	
    	eoList = this.errorManagementDao.findErrorOccurrences(uris);

        assertEquals(2, eoList.size());
    }

    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
            @Test
    void test_count_error_occurrence_for_module()
    {
    	ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo1 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo2 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	
    	this.errorReportingServiceDao.save(eo);
    	this.errorReportingServiceDao.save(eo1);
    	this.errorReportingServiceDao.save(eo2);
    	
    	System.out.println(this.errorManagementDao.getNumberOfModuleErrors("moduleName", false, false, new Date(System.currentTimeMillis() - 100000000), new Date(System.currentTimeMillis() + 100000000)));
    	
    	assertTrue(this.errorManagementDao.getNumberOfModuleErrors("moduleName", false, false, new Date(System.currentTimeMillis() - 100000000), new Date(System.currentTimeMillis() + 100000000)) == 3);
    }

    @Test
            @DirtiesContext
    void test_harvest_success()
    {
        this.errorManagementDao.setHarvestQueryOrdered(true);
        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            this.errorReportingServiceDao.save(eo);

            errorOccurrences.add(eo);
        }

        assertEquals(1000, this.errorManagementDao.getHarvestableRecords(5000).size(), "Harvestable records == 1000");

        this.errorManagementDao.updateAsHarvested(errorOccurrences);

        assertEquals(0, this.errorManagementDao.getHarvestableRecords(5000).size(), "Harvestable records == 0");
    }

    @Test
            @DirtiesContext
    void test_harvest_success_no_order_by()
    {
        this.errorManagementDao.setHarvestQueryOrdered(false);
        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            this.errorReportingServiceDao.save(eo);

            errorOccurrences.add(eo);
        }

        assertEquals(1000, this.errorManagementDao.getHarvestableRecords(5000).size(), "Harvestable records == 1000");

        this.errorManagementDao.updateAsHarvested(errorOccurrences);

        assertEquals(0, this.errorManagementDao.getHarvestableRecords(5000).size(), "Harvestable records == 0");
    }
}