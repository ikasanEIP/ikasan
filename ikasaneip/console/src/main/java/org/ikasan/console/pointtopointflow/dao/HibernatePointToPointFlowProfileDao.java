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
package org.ikasan.console.pointtopointflow.dao;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.console.pointtopointflow.PointToPointFlow;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.ikasan.framework.module.Module;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>PointToPointFlowDao</code>
 * 
 * @author Ikasan Development Team
 */
public class HibernatePointToPointFlowProfileDao extends HibernateDaoSupport implements PointToPointFlowProfileDao
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(HibernatePointToPointFlowProfileDao.class);
    
    /**
     * Find all of the PointToPointFlowProfiles
     * 
     * @return List of PointToPointFlowProfiles
     * 
     * @see
     * org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findAllPointToPointFlowProfiles()
     */
    @SuppressWarnings("unchecked")
    public List<PointToPointFlowProfile> findAllPointToPointFlowProfiles()
    {
        List<PointToPointFlowProfile> pointToPointFlowProfiles = null;
        logger.info("about to call search");
        pointToPointFlowProfiles = getHibernateTemplate().loadAll(PointToPointFlowProfile.class);
        logger.info("Made it.");
        
        for (PointToPointFlowProfile profile : pointToPointFlowProfiles)
        {
            logger.info("PointToPointFlowProfile.getName() [" + profile.getName() +"]");
            Set<PointToPointFlow> pointToPointFlows = profile.getPointToPointFlows();
            for (PointToPointFlow pointToPointFlow : pointToPointFlows)
            {
                logger.info("PointToPointFlow.getPointToPointFlowProfile().getName() [" + pointToPointFlow.getPointToPointFlowProfile().getName() + "]");
                Module fromModule = pointToPointFlow.getFromModule();
                if (fromModule != null)
                {
                    logger.info("PointToPointFlow.getFromModule().getName() [" + fromModule.getName() + "]");
                }
                Module toModule = pointToPointFlow.getToModule();
                if (toModule != null)
                {
                    logger.info("PointToPointFlow.getToModule().getName() [" + toModule.getName() + "]");
                }
            }
        }
        
        return pointToPointFlowProfiles;
    }
    
}
