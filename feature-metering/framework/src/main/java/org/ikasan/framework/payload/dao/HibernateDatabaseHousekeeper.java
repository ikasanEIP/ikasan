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
package org.ikasan.framework.payload.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.payload.service.DatabaseHousekeeper;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementation of <code>DatabaseHousekeeper</code> that uses Hibernate and identifies housekeepable items by running
 * a configured hibernate query
 * 
 * @author Ikasan Development Team
 */
public class HibernateDatabaseHousekeeper extends HibernateDaoSupport implements DatabaseHousekeeper
{
    /** Constant representing all records */
    private final static int ALL_RECORDS = -1;

    /** Query to find housekeepable items */
    private String hibernateQuery;

    /** The maximum number of results to return (-1 == all) */
    private int maxResultSetSize = ALL_RECORDS;

    /** Cannot use inherited logger because it is a Commons Logger */
    private Logger localLogger = Logger.getLogger(HibernateDatabaseHousekeeper.class);

    /**
     * Constructor
     * 
     * @param maxResultSetSize The maximum result set to apply
     * @param hibernateQuery The query to run
     */
    public HibernateDatabaseHousekeeper(int maxResultSetSize, String hibernateQuery)
    {
        super();
        this.maxResultSetSize = maxResultSetSize;
        this.hibernateQuery = hibernateQuery;
    }

    public void housekeep()
    {
        localLogger.info("Querying for housekeepables...");
        if (maxResultSetSize != ALL_RECORDS)
        {
            localLogger.info("Limiting the size of housekeepables to [" + this.maxResultSetSize + "]");
            getHibernateTemplate().setMaxResults(this.maxResultSetSize);
        }
        List<?> housekeepables = getHibernateTemplate().find(hibernateQuery);
        localLogger.info("found [" + housekeepables.size() + "] housekeepables");
        for (Object housekeepable : housekeepables)
        {
            localLogger.debug("about to housekeep [" + housekeepable + "]");
            getHibernateTemplate().delete(housekeepable);
        }
    }
}
