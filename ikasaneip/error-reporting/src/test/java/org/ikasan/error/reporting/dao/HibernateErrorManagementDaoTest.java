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

import org.ikasan.error.reporting.ErrorReportingAutoConfiguration;
import org.ikasan.error.reporting.ErrorReportingTestAutoConfiguration;
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(classes = {ErrorReportingAutoConfiguration.class, ErrorReportingTestAutoConfiguration.class})
@Sql(scripts = {"/modifyErrorOccurrenceTable.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class HibernateErrorManagementDaoTest
{    
    @Resource
    ErrorManagementDao errorManagementDao;

    @Resource
    ErrorManagementDao deleteOnceHarvestedErrorManagementDao;
    
    @Resource
	ErrorReportingServiceDao errorReportingServiceDao;

    Exception exception = new Exception("error message");


    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_save_find_and_delete_error_occurrence()
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
    	
    	Assert.assertTrue(eoList.size() == 3);
    	
    	this.errorManagementDao.deleteErrorOccurence(eo);
    	
    	eoList = this.errorManagementDao.findErrorOccurrences(uris);
    	
    	Assert.assertTrue(eoList.size() == 2);
    }
    
    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_count_error_occurrence_for_module()
    {
    	ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo1 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	ErrorOccurrenceImpl eo2 = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
    	
    	this.errorReportingServiceDao.save(eo);
    	this.errorReportingServiceDao.save(eo1);
    	this.errorReportingServiceDao.save(eo2);

    	Assert.assertTrue(this.errorManagementDao.getNumberOfModuleErrors("moduleName", false, false, new Date(System.currentTimeMillis() - 100000000), new Date(System.currentTimeMillis() + 100000000)) == 3);
    }

    @Test
    @DirtiesContext
    public void test_harvest_success()
    {
        this.errorManagementDao.setHarvestQueryOrdered(true);
        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            this.errorReportingServiceDao.save(eo);

            errorOccurrences.add(eo);
        }

        Assert.assertEquals("Harvestable records == 1000", 1000, this.errorManagementDao.getHarvestableRecords(5000).size());

        this.errorManagementDao.updateAsHarvested(errorOccurrences);

        Assert.assertEquals("Harvestable records == 0", 0, this.errorManagementDao.getHarvestableRecords(5000).size());
    }

    @Test
    @DirtiesContext
    public void test_housekeep_success()
    {
        this.errorManagementDao.setHarvestQueryOrdered(true);

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");
            this.errorReportingServiceDao.save(eo);
        }

        Assert.assertEquals("Harvestable records == 1000", this.errorManagementDao.getHarvestableRecords(5000).size(), 1000);

        this.errorManagementDao.housekeep(100);

        Assert.assertEquals("Harvestable records == 900", this.errorManagementDao.getHarvestableRecords(5000).size(), 900);
    }

    @Test
    @DirtiesContext
    public void test_housekeep_once_harvested_success()
    {
        this.deleteOnceHarvestedErrorManagementDao.setHarvestQueryOrdered(true);

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100000000L, new byte[100], "errorString");
            eo.setHarvested(true);
            this.errorReportingServiceDao.save(eo);
        }

        Assert.assertEquals("Harvestable records == 1000", this.errorManagementDao.getHarvestableRecords(5000).size(), 1000);

        this.deleteOnceHarvestedErrorManagementDao.housekeep(100);

        Assert.assertEquals("Harvestable records == 900", this.errorManagementDao.getHarvestableRecords(5000).size(), 900);
    }

    @Test
    @DirtiesContext
    public void test_harvest_success_no_order_by()
    {
        this.errorManagementDao.setHarvestQueryOrdered(false);
        List<ErrorOccurrence> errorOccurrences = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ErrorOccurrenceImpl eo = new ErrorOccurrenceImpl("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100], "errorString");

            this.errorReportingServiceDao.save(eo);

            errorOccurrences.add(eo);
        }

        Assert.assertEquals("Harvestable records == 1000", this.errorManagementDao.getHarvestableRecords(5000).size(), 1000);

        this.errorManagementDao.updateAsHarvested(errorOccurrences);

        Assert.assertEquals("Harvestable records == 0", this.errorManagementDao.getHarvestableRecords(5000).size(), 0);
    }
}