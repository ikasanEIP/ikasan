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
package org.ikasan.setup.persistence.service.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import liquibase.change.custom.CustomTaskChange;
import liquibase.change.custom.CustomTaskRollback;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

import org.apache.log4j.Logger;
import org.ikasan.security.service.AuthenticationServiceImpl;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceCustomDataTypeMigrationTask implements
		CustomTaskChange, CustomTaskRollback
{
	private static Logger logger = Logger.getLogger(ErrorOccurrenceCustomDataTypeMigrationTask.class);

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage()
	{
		return "ErrorOccurrenceCustomDataTypeMigrationTask ran successfully!";
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#setFileOpener(liquibase.resource.ResourceAccessor)
	 */
	@Override
	public void setFileOpener(ResourceAccessor arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#setUp()
	 */
	@Override
	public void setUp() throws SetupException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#validate(liquibase.database.Database)
	 */
	@Override
	public ValidationErrors validate(Database arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomTaskRollback#rollback(liquibase.database.Database)
	 */
	@Override
	public void rollback(Database arg0) throws CustomChangeException,
			RollbackImpossibleException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomTaskChange#execute(liquibase.database.Database)
	 */
	@Override
	public void execute(Database database) throws CustomChangeException
	{
		JdbcConnection connection = (JdbcConnection) database.getConnection();
	    DataSource dataSource = new SingleConnectionDataSource(connection.getUnderlyingConnection(), true);
	    int BATCH_SIZE = 5000;
	    Statement s = null;
	    
	    try
		{
			s = dataSource.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			s.setFetchSize(5000);
			
			ResultSet rs = s.executeQuery("SELECT * FROM ErrorOccurrence");
			
			while(rs.next())
			{
				rs.updateString("ErrorDetailNew", rs.getString("ErrorDetail"));
				rs.updateString("ErrorMessageNew", rs.getString("ErrorMessage"));
				rs.updateRow();
				
				if(rs.getRow() % BATCH_SIZE == 0)
				{
					logger.info("Number of rows migrated: " + rs.getRow());
				}
			}
			
		} 
	    catch (Exception e)
		{
			logger.error("An error occurred attempting to migrate error occurrence data types to UNITEXT!", e);
			
			throw new CustomChangeException(e);
		} 
	    finally
	    {
	    	try
			{
				s.close();
			} 
	    	catch (Exception e)
			{
	    		logger.error("An error occurred attempting to close the statement!", e);
				
				throw new CustomChangeException(e);
			}
	    }
	}

}
