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
package org.ikasan.setup.persistence.service;

import java.io.StringWriter;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PersistenceServiceImpl implements PersistenceService
{
	private static final String BASELINE = "baseline";
	private static final String POST_BASELINE = "postBaseline";
	
	private Liquibase generalLiquibase;
	private Liquibase fileTransferLiquibase;

	/**
	 * @param generalLiquibase
	 */
	public PersistenceServiceImpl(Liquibase generalLiquibase,
			Liquibase fileTransferLiquibase)
	{
		super();
		this.generalLiquibase = generalLiquibase;
		if(this.generalLiquibase == null)
		{
			throw new IllegalArgumentException("liquibase cannot be null");
		}
		this.fileTransferLiquibase = fileTransferLiquibase;
		if(this.fileTransferLiquibase == null)
		{
			throw new IllegalArgumentException("fileTransferLiquibase cannot be null");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.setup.persistence.service.PersistenceService#createPersistence()
	 */
	@Override
	public void createBaselinePersistence()
	{
		try
		{
			Contexts contexts 
				= new Contexts(BASELINE, POST_BASELINE);
			
			this.generalLiquibase.update(contexts);
		} 
		catch (LiquibaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.setup.persistence.service.PersistenceService#createFileTransferPersistence()
	 */
	@Override
	public void createFileTransferPersistence()
	{
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.setup.persistence.service.PersistenceService#createPostBaselinePersistence()
	 */
	@Override
	public void createPostBaselinePersistence()
	{
		try
		{
			Contexts contexts 
				= new Contexts(POST_BASELINE);
			
			this.generalLiquibase.update(contexts);
		} 
		catch (LiquibaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.setup.persistence.service.PersistenceService#getStatus()
	 */
	@Override
	public String getBaselineStatus()
	{
		StringWriter out = new StringWriter();
		
		Contexts contexts 
			= new Contexts(BASELINE, POST_BASELINE);
		try
		{
			this.generalLiquibase.reportStatus(true, contexts, out);
		} 
		catch (LiquibaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out.toString();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.setup.persistence.service.PersistenceService#getPostBaselineStatus()
	 */
	@Override
	public String getPostBaselineStatus()
	{
		StringWriter out = new StringWriter();
		
		Contexts contexts 
			= new Contexts(BASELINE, POST_BASELINE);
		try
		{
			this.generalLiquibase.reportStatus(true, contexts, out);
		} 
		catch (LiquibaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out.toString();
	}
}
