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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/ikasan-transaction-conf.xml",
        "/error-reporting-service-conf.xml",
        "/mock-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
        })

public class ErrorCategorisationDaoTest
{
    @Resource
    ErrorCategorisationDao errorCategorisationDao;
    
    @Resource
    ErrorReportingServiceDao errorReportingServiceDao;

    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_save_and_find()
    {
    	ErrorCategorisationLink link = new ErrorCategorisationLink("moduleName", "flowName", 
        		"flowElementName", "", "");
    	
        ErrorCategorisation errorCategorisation = new ErrorCategorisation(ErrorCategorisation.TRIVIAL, "This is the error message");
        
        this.errorCategorisationDao.save(errorCategorisation);
        
        link.setErrorCategorisation(errorCategorisation);
        
        this.errorCategorisationDao.save(link);

        
        ErrorCategorisationLink foundErrorCategorisationLink = this.errorCategorisationDao.find("moduleName", "flowName", 
        		"flowElementName", "");
        
        Assert.assertEquals(link, foundErrorCategorisationLink);
    }
    
    @DirtiesContext
    @Test(expected=DataIntegrityViolationException.class)
    public void test_exception_add_duplicate()
    {
    	ErrorCategorisationLink link = new ErrorCategorisationLink("moduleName", "flowName", 
        		"flowElementName", "action", "");
        
        this.errorCategorisationDao.save(link);
        
        link = new ErrorCategorisationLink("moduleName", "flowName", 
        		"flowElementName", "action", "");
        
        this.errorCategorisationDao.save(link);
    }  
    
    @DirtiesContext
    @Test
    @Ignore
    public void test_save_and_find_categorised_error()
    {
    	ErrorOccurrence errorOccurrence = new ErrorOccurrence
    			("moduleName", "flowName", "flowElementName", "error detail", 
    					ErrorReportingService.DEFAULT_TIME_TO_LIVE);
    	errorOccurrence.setAction("Retry");

        errorReportingServiceDao.save(errorOccurrence);
        
        errorOccurrence = new ErrorOccurrence
    			("moduleName", "anotherFlowName", "anotherFlowElementName", "error detail", 
    					ErrorReportingService.DEFAULT_TIME_TO_LIVE);
        errorOccurrence.setAction("Retry");
        
        errorReportingServiceDao.save(errorOccurrence);
        
        ErrorCategorisation errorCategorisation1 = new ErrorCategorisation(ErrorCategorisation.TRIVIAL, "This is the error message");
        
        this.errorCategorisationDao.save(errorCategorisation1);
        
        ErrorCategorisation errorCategorisation2 = new ErrorCategorisation(ErrorCategorisation.BLOCKER, "This is a blocker error message");
        
        this.errorCategorisationDao.save(errorCategorisation2);
        
        ErrorCategorisationLink link = new ErrorCategorisationLink("moduleName", "flowName", 
        		"flowElementName", "Retry", "");
        
        
        link.setErrorCategorisation(errorCategorisation2);
        
        this.errorCategorisationDao.save(link);
        
        link = new ErrorCategorisationLink("moduleName", "", 
        		"", "Retry", "");
        
        link.setErrorCategorisation(errorCategorisation1);
        
        this.errorCategorisationDao.save(link);
        
        link = new ErrorCategorisationLink("moduleName", "flowName", 
        		"", "Retry", "");
        
        link.setErrorCategorisation(errorCategorisation1);
        
        this.errorCategorisationDao.save(link);
        
        link = new ErrorCategorisationLink("", "", 
        		"", "Retry", "");
        
        link.setErrorCategorisation(errorCategorisation2);
        
        this.errorCategorisationDao.save(link);

        ArrayList<String> moduleNames = new ArrayList<String>();
        moduleNames.add("moduleName");
        ArrayList<String> flowNames = new ArrayList<String>();
        flowNames.add("flowName");
        ArrayList<String> componentNames = new ArrayList<String>();
        componentNames.add("flowElementName");
        
        List<CategorisedErrorOccurrence> categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, flowNames, componentNames, null, null, null, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        System.out.println(categorisedErrorOccurences.get(0).getErrorCategorisation());
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 1);
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, flowNames, componentNames, null, null, ErrorCategorisation.TRIVIAL, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 0);
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, flowNames, componentNames, null, null, ErrorCategorisation.BLOCKER, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 1);
        
        System.out.println(categorisedErrorOccurences.get(0).getErrorCategorisation());
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, null, null, null, null, ErrorCategorisation.TRIVIAL, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 2);
        
        System.out.println(categorisedErrorOccurences.get(0).getErrorCategorisation());
        System.out.println(categorisedErrorOccurences.get(1).getErrorCategorisation());
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, null, null, null, null, ErrorCategorisation.BLOCKER, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 0);
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(moduleNames, flowNames, null, null, null, ErrorCategorisation.TRIVIAL, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 1);
        
        System.out.println(categorisedErrorOccurences.get(0).getErrorCategorisation());
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(null, null, null, null, null, ErrorCategorisation.BLOCKER, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 2);
        
        System.out.println(categorisedErrorOccurences.get(0).getErrorCategorisation());
        System.out.println(categorisedErrorOccurences.get(1).getErrorCategorisation());
        
        categorisedErrorOccurences = this.errorCategorisationDao
        		.findCategorisedErrorOccurences(null, null, null, null, null, ErrorCategorisation.TRIVIAL, new Date(100), 
        				new Date(new Long("10000000000000")));
        
        Assert.assertEquals(categorisedErrorOccurences.size(), 0);
    } 

}