/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
