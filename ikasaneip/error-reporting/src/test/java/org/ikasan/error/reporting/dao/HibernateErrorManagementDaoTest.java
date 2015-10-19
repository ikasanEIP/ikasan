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
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceLink;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.Link;
import org.ikasan.error.reporting.model.Note;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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

public class HibernateErrorManagementDaoTest
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
    public void test_save_find_and_delete_Link()
    {
    	Link link = new Link("www.somelink.com", "user");
    	
    	this.errorManagementDao.saveLink(link);
    	
    	ErrorOccurrenceLink eoLink = new ErrorOccurrenceLink("uri", link.getId());

    	this.errorManagementDao.saveErrorOccurrenceLink(eoLink);
    	
    	List<Link> links = this.errorManagementDao.getLinksByErrorUri("uri");
    	
    	Assert.assertTrue(links.size() == 1);
    	
    	link = new Link("www.somelink.com", "user");
    	
    	this.errorManagementDao.saveLink(link);
    	
    	eoLink = new ErrorOccurrenceLink("uri", link.getId());

    	this.errorManagementDao.saveErrorOccurrenceLink(eoLink);
    	
    	link = new Link("www.somelink.com", "user");
    	
    	this.errorManagementDao.saveLink(link);
    	
    	eoLink = new ErrorOccurrenceLink("uri", link.getId());

    	this.errorManagementDao.saveErrorOccurrenceLink(eoLink);
    	
    	links = this.errorManagementDao.getLinksByErrorUri("uri");
    	
    	Assert.assertTrue(links.size() == 3);
    	
    	this.errorManagementDao.deleteLink(link);
    	
    	links = this.errorManagementDao.getLinksByErrorUri("uri");
    	
    	Assert.assertTrue(links.size() == 2);
    }
    
    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_save_find_and_delete_note()
    {
    	Note note = new Note("some notes", "user");
    	Link link = new Link("www.somelink.com", "user");
    	
    	this.errorManagementDao.saveNote(note);
    	
    	ErrorOccurrenceNote eoNote = new ErrorOccurrenceNote("uri", note.getId());
    	eoNote.setLink(link);

    	this.errorManagementDao.saveErrorOccurrenceNote(eoNote);
    	
    	List<ErrorOccurrenceNote> eonotes = this.errorManagementDao.getErrorOccurrenceNotesByErrorUri("uri");
    	
    	Assert.assertTrue(eonotes.size() == 1);
    	
    	Assert.assertTrue(eonotes.get(0).getLink().getLink().equals("www.somelink.com"));
    	Assert.assertTrue(eonotes.get(0).getNote().getNote().equals("some notes"));
    	
    	note = new Note("some notes", "user");
    	
    	this.errorManagementDao.saveNote(note);
    	
    	eoNote = new ErrorOccurrenceNote("uri", note.getId());

    	this.errorManagementDao.saveErrorOccurrenceNote(eoNote);
    	
    	List<Note> notes = this.errorManagementDao.getNotesByErrorUri("uri");
    	
    	Assert.assertTrue(notes.size() == 2);
    	
    	note = new Note("some notes", "user");
    	
    	this.errorManagementDao.saveNote(note);
    	
    	eoNote = new ErrorOccurrenceNote("uri", note.getId());

    	this.errorManagementDao.saveErrorOccurrenceNote(eoNote);
    	
    	notes = this.errorManagementDao.getNotesByErrorUri("uri");
    	
    	Assert.assertTrue(notes.size() == 3);
    	
    	this.errorManagementDao.deleteNote(note);
    	
    	notes = this.errorManagementDao.getNotesByErrorUri("uri");
    	
    	Assert.assertTrue(notes.size() == 2);
    	
    	List<String> uris = this.errorManagementDao.getAllErrorUrisWithNote();
    	
    	Assert.assertTrue(uris.size() == 2);
    }
    
    /**
     * Test save of errorOccurrence
     */
    @DirtiesContext
    @Test
    public void test_save_find_and_delete_error_occurrence()
    {
    	ErrorOccurrence eo = new ErrorOccurrence("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100]);
    	ErrorOccurrence eo1 = new ErrorOccurrence("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100]);
    	ErrorOccurrence eo2 = new ErrorOccurrence("moduleName", "flowName", "flowElementName", "errorDetail", "errorMessage", "exceptionClass", 100, new byte[100]);
    	
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
    
}