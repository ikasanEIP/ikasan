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

package org.ikasan.connector.basefiletransfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.ikasan.connector.base.command.HibernateTransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.outbound.persistence.HibernateBaseFileTransferDaoImpl;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.connector.util.chunking.model.dao.HibernateFileChunkDao;

/**
 * static util class for resolving hibernate backed data access objects for file
 * transfer connectors
 * 
 * @author Ikasan Development Team
 * 
 */
public class DataAccessUtil {

	/**
	 * static BaseFileTrasferDao instance, used for checking file filtering
	 */
	private static BaseFileTransferDao baseFileTransferDao;

	/**
	 * static FileChunkDao instance, used for chunking large files
	 */
	private static FileChunkDao fileChunkDao;

	/**
	 * static TransactionalResourceCommandDAO used for transaction journalling
	 */
	private static TransactionalResourceCommandDAO transactionalResourceCommandDAO;

	/**
	 * static accessor for singleton BaseFileTransferDao
	 * 
	 * @return singleton instance of BaseFileTransferDao
	 */
	public static BaseFileTransferDao getBaseFileTransferDao() {
		if (baseFileTransferDao == null) {

			Configuration cfg = generateConfiguration();
			cfg.setProperty(Environment.DATASOURCE,
					"java:ikasan/framework/defaultXA/xads");
			cfg.addClass(FileFilter.class); // this will expect to find
											// FileFilter.hbm.xml in the fully
											// qualified package for FileFilter

			SessionFactory baseFileTrasferHibernateSessionFactory = cfg
					.buildSessionFactory();

			baseFileTransferDao = new HibernateBaseFileTransferDaoImpl(
					baseFileTrasferHibernateSessionFactory);
		}
		return baseFileTransferDao;
	}

	/**
	 * static accessor for singleton FileChunkDao
	 * 
	 * @return singleton instance of FileChunkDao
	 */
	public static FileChunkDao getFileChunkDao() {
		if (fileChunkDao == null) {

			Configuration cfg = generateConfiguration();
			cfg.setProperty(Environment.DATASOURCE,
					"java:ikasan/connector/file/ds");

			cfg.addClass(FileChunk.class); // this will expect to find
											// FileChunk.hbm.xml in the fully
											// qualified package for FileFilter
			cfg.addClass(FileChunkHeader.class); // this will expect to find
													// FileChunkHeader.hbm.xml
													// in the fully qualified
													// package for FileFilter

			SessionFactory fileChunkSessionFactory = cfg.buildSessionFactory();

			fileChunkDao = new HibernateFileChunkDao(fileChunkSessionFactory);

		}
		return fileChunkDao;

	}

	/**
	 * static accessor for singleton TransactionalResourceCommandDAO
	 * 
	 * @return singleton instance of TransactionalResourceCommandDAO
	 */
	public static TransactionalResourceCommandDAO getTransactionalResourceCommandDAO() {
		if (transactionalResourceCommandDAO == null) {
			InputStream transactionalResourceCommandMappings = Thread
					.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("TransactionalResourceCommand.hbm.xml");
			InputStream xidImplMappings = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(
							"XidImpl.hbm.xml");

			Configuration cfg = generateConfiguration();
			cfg.setProperty(Environment.DATASOURCE,
					"java:ikasan/framework/default/ds");
			cfg.addInputStream(transactionalResourceCommandMappings);
			cfg.addInputStream(xidImplMappings);

			transactionalResourceCommandDAO = new HibernateTransactionalResourceCommandDAO(
					cfg.buildSessionFactory());
		}

		return transactionalResourceCommandDAO;
	}

	/**
	 * Creates a base <code>Configuration</code> instance incorporating any
	 * properties found in the classpath resource ikasan-hibernate.properties.
	 * 
	 * A <code>RuntimeException</code> will be thrown if this does not exist, or
	 * does not contain a hibernate.dialect setting
	 * 
	 * @return base Configuration
	 */
	private static Configuration generateConfiguration() {
		Properties properties = new Properties();
		InputStream resourceAsStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(
						"ikasan-hibernate.properties");
		try {
			properties.load(resourceAsStream);
		} catch (IOException e) {
			throw new RuntimeException(
					"problem loading ikasan-hibernate.properties", e);
		}
		if (!properties.containsKey("hibernate.dialect")) {
			throw new RuntimeException(
					"ikasan-hibernate.properties must contain at least hibernate.dialect setting");
		}
		// properties.put("hibernate.transaction.factory_class",
		// "org.hibernate.transaction.JTATransactionFactory");

		Configuration cfg = new Configuration();
		cfg.setProperties(properties);
		return cfg;
	}
}
