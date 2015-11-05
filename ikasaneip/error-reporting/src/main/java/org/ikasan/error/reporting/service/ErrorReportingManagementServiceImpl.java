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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.error.reporting.dao.ErrorManagementDao;
import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.Link;
import org.ikasan.error.reporting.model.Note;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorReportingManagementServiceImpl implements ErrorReportingManagementService<ErrorOccurrence, Note, ErrorOccurrenceNote>
{
	private static Logger logger = Logger.getLogger(ErrorReportingManagementServiceImpl.class);
	
	public static final String CLOSE = "close";

	private ErrorManagementDao errorManagementDao;
	
	private ErrorReportingServiceDao errorReportingServiceDao;
	
	private int batchSize = 100;
	
	
	/**
	 * Constructor
	 * 
	 * @param errorManagementDao
	 */
	public ErrorReportingManagementServiceImpl(ErrorManagementDao errorManagementDao,
			ErrorReportingServiceDao errorReportingServiceDao)
	{
		super();
		this.errorManagementDao = errorManagementDao;
		if(this.errorManagementDao == null)
		{
			throw new IllegalArgumentException("errorManagementDao cannot be null!");
		}
		this.errorReportingServiceDao = errorReportingServiceDao;
		if(this.errorReportingServiceDao == null)
		{
			throw new IllegalArgumentException("errorManagementDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#update(java.util.List, java.lang.String)
	 */
	@Override
	public void update(List<String> uris, String noteString, String user)
	{
		Note note = null;
		
		if(noteString != null && noteString.length() > 0)
		{
			note = new Note(noteString, user);
			this.errorManagementDao.saveNote(note);
		}
		
		for(String uri: uris)
		{			
			if(note != null)
			{
				ErrorOccurrenceNote errorOccurrenceNote = new ErrorOccurrenceNote(uri, note);
				
				this.errorManagementDao.saveErrorOccurrenceNote(errorOccurrenceNote);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#close(java.util.List, java.lang.String)
	 */
	@Override
	public void close(List<String> uris, String noteString, String user)
	{
		Note note = null;
		
		if(noteString != null && noteString.length() > 0)
		{
			note = new Note(noteString, user);
			this.errorManagementDao.saveNote(note);
		}
			
		
		for(String uri: uris)
		{			
			ErrorOccurrenceNote errorOccurrenceNote = new ErrorOccurrenceNote(uri, note);
			
			this.errorManagementDao.saveErrorOccurrenceNote(errorOccurrenceNote);
		}
				
		for(int i=0; i<uris.size();)
		{
			List<String> batchUris = new ArrayList<String>();
			int endMarker = 0;
			
			if(i + batchSize < uris.size())
			{
				endMarker = i + batchSize;
			}
			else
			{
				endMarker = uris.size();
			}
			
			batchUris.addAll(uris.subList(i, endMarker));
			
			this.errorManagementDao.close(batchUris, user);
			
			if(i + batchSize < uris.size())
			{
				i= i + batchSize;
			}
			else
			{
				i = uris.size();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ErrorOccurrence> find(List<String> moduleName,
			List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate)
	{
		return this.errorManagementDao.findActionErrorOccurrences(moduleName, flowName, flowElementname, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#setTimeToLive(java.lang.Long)
	 */
	@Override
	public void setTimeToLive(Long timeToLive)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#housekeep()
	 */
	@Override
	public void housekeep()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#deleteNote(java.lang.Object)
	 */
	@Override
	public void deleteNote(Note note)
	{
		this.errorManagementDao.deleteNote(note);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagermentService#updateNote(java.lang.Object)
	 */
	@Override
	public void updateNote(Note note)
	{
		this.errorManagementDao.saveNote(note);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagementService#getAllErrorUrisWithNote()
	 */
	@Override
	public List<String> getAllErrorUrisWithNote()
	{
		return this.errorManagementDao.getAllErrorUrisWithNote();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagementService#getNotesByErrorUri(java.lang.String)
	 */
	@Override
	public List<Note> getNotesByErrorUri(String errorUri)
	{
		return this.errorManagementDao.getNotesByErrorUri(errorUri);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagementService#getErrorOccurrenceNotesByErrorUri(java.lang.String)
	 */
	@Override
	public List<ErrorOccurrenceNote> getErrorOccurrenceNotesByErrorUri(
			String errorUri)
	{
		return this.errorManagementDao.getErrorOccurrenceNotesByErrorUri(errorUri);
	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize()
	{
		return batchSize;
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

}
