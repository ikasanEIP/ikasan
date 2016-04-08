/* 
 * $Id: 
 * $URL: 
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

package org.ikasan.spec.error.reporting;

import java.util.Date;
import java.util.List;

/**
 * This contract represents a platform level service for the management or errors.
 * 
 * @author Ikasan Development Team
 * 
 */
public interface ErrorReportingManagementService<ACTIONED_EVENT, NOTE, ERROR_OCCURRENCE_NOTE, MODULE_ERROR_COUNT>
{
    /** one year default time to live */
    public static final long DEFAULT_TIME_TO_LIVE = new Long(1000 * 60 * 60 * 24 * 365);

    /**
     * This message will associate a note and link with the errors referenced by the uris.
     * 
     * @param uris
     * @param note
     * @param link
     */
    public void update(List<String> uris, String note, String user);
    
    /**
     * This method will close errors and associate a note and link with them.
     * 
     * @param uris
     * @param note
     * @param link
     * @param user
     */
    public void close(List<String> uris, String note, String user);

    /**
     * Delete a note
     * 
     * @param note
     */
    public void deleteNote(NOTE note);
    
    /**
     * Update a note
     * 
     * @param note
     */
    public void updateNote(NOTE note);
    
    /**
     * Find an actioned error reporting events based on a list of moduleName, flowName and flowElementName
     * as well as a date range.
     * 
     * @param moduleName
     * @param flowName
     * @param flowElementname
     * @param startDate
     * @param endDate
     * @return
     */
    public List<ACTIONED_EVENT> find(List<String> moduleName, List<String> flowName, List<String> flowElementname,
    		Date startDate, Date endDate);
    
	/**
	 * Method to return all error uris that have a note.
	 * 
	 * @return
	 */
	public List<String> getAllErrorUrisWithNote();


    /**
     * Allow entities blacklisted to be marked with a timeToLive.
     * On expiry of the timeToLive the entity will no longer be blacklisted.
     *
     * @param timeToLive
     */
    public void setTimeToLive(Long timeToLive);

    /**
     * Housekeep expired exclusionEvents.
     */
    public void housekeep();
    
    /**
	 * 
	 * @param errorUri
	 * @return
	 */
	public List<NOTE> getNotesByErrorUri(String errorUri);
	
	
	/**
	 * 
	 * @param errorUri
	 * @return
	 */
	public List<ERROR_OCCURRENCE_NOTE> getErrorOccurrenceNotesByErrorUri(String errorUri);
	
	/**
	 * 
	 * @param moduleName
	 * @return
	 */
	public List<MODULE_ERROR_COUNT> getModuleErrorCount(List<String> moduleNames,  boolean excluded, boolean actioned, Date startDate, Date endDate);
}
