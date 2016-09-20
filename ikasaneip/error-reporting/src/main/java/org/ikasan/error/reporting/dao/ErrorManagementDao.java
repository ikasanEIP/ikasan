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

import java.util.Date;
import java.util.List;

import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceAction;
import org.ikasan.error.reporting.model.ErrorOccurrenceLink;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.Link;
import org.ikasan.error.reporting.model.Note;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface ErrorManagementDao
{
	/**
	 * 
	 * @param errorOccurrenceAction
	 */
	public void saveErrorOccurrenceAction(ErrorOccurrenceAction errorOccurrenceAction);
	
	/**
	 * 
	 * @param note
	 */
	public void saveNote(Note note);
	
	/**
	 * 
	 * @param note
	 */
	public void deleteNote(Note note);
	
	
	/**
	 * 
	 * @param errorOccurrenceLink
	 */
	public void saveErrorOccurrenceLink(ErrorOccurrenceLink errorOccurrenceLink);
	
	/**
	 * 
	 * @param errorOccurrenceNote
	 */
	public void saveErrorOccurrenceNote(ErrorOccurrenceNote errorOccurrenceNote);
	
	/**
	 * 
	 * @param errorOccurrence
	 */
	public void deleteErrorOccurence(ErrorOccurrence errorOccurrence);
	
	/**
	 * 
	 * @param errorUris
	 * @return
	 */
	public List<ErrorOccurrence> findErrorOccurrences(List<String> errorUris);
	
	
	/**
	 * 
	 * @param errorUri
	 * @return
	 */
	public List<ErrorOccurrenceNote> getErrorOccurrenceNotesByErrorUri(String errorUri);
	
	/**
	 * 
	 * @param errorUri
	 * @return
	 */
	public List<Note> getNotesByErrorUri(String errorUri);
	
	
	/**
	 * 
	 * @return
	 */
	public List<String> getAllErrorUrisWithNote();
	
	/**
	 * 
	 * @param uris
	 * @param user
	 */
	public void close(final List<String> uris, final String user);
	
	/**
	 * 
	 * @param moduleName
	 * @param flowName
	 * @param flowElementname
	 * @param startDate
	 * @param endDate
	 * @return
	 */
    public List<ErrorOccurrence> findActionErrorOccurrences(List<String> moduleName, List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate);
    /**
     * 
     * @return
     */
    public List<ErrorOccurrenceAction> houseKeepErrorOccurrenceActions();
    
    /**
     * 
     * @param moduleName
     * @return
     */
    public Long getNumberOfModuleErrors(String moduleName, boolean excluded, boolean actioned, Date startDate, Date endDate);

	/**
	 * House keep associated entities.
     */
	public void housekeep(final Integer numToHousekeep);
}
