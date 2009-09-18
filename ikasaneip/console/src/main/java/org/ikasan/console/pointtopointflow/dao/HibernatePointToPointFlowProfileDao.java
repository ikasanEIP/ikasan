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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ikasan.console.pointtopointflow.PointToPointFlow;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.ikasan.console.module.Module;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>PointToPointFlowDao</code>
 * 
 * TODO Could make code more efficient
 * 
 * @author Ikasan Development Team
 */
public class HibernatePointToPointFlowProfileDao extends HibernateDaoSupport implements PointToPointFlowProfileDao
{

    /** Query for finding all point to point flow profiles based on name */
    private static final String POINT_TO_POINT_FLOW_PROFILES_BY_NAME = "from PointToPointFlowProfile p where p.name in (:names)";
    
    /**
     * Find all of the PointToPointFlowProfiles
     * 
     * @return List of PointToPointFlowProfiles
     * 
     * @see org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findAllPointToPointFlowProfiles()
     */
    @SuppressWarnings("unchecked")
    public List<PointToPointFlowProfile> findAllPointToPointFlowProfiles()
    {
        List<PointToPointFlowProfile> pointToPointFlowProfiles = null;
        pointToPointFlowProfiles = getHibernateTemplate().loadAll(PointToPointFlowProfile.class);
        return pointToPointFlowProfiles;
    }

    /**
     * @see org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findModuleNames()
     */
    public Set<String> findModuleNames()
    {
        List<PointToPointFlowProfile> pointToPointFlowProfiles = this.findAllPointToPointFlowProfiles();
        Set<String> moduleNames = new HashSet<String>();        
        if (pointToPointFlowProfiles != null && !pointToPointFlowProfiles.isEmpty())
        {
            moduleNames = getModuleNamesFromPointToPointFLowProfiles(pointToPointFlowProfiles);
        }
        return moduleNames;
    }
    
    /**
     * @see org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findModuleNames(Set)
     */
    public Set<String> findModuleNames(Set<String> pointToPointFlowProfileNames)
    {
        Set<String> moduleNames = new HashSet<String>();
        List<PointToPointFlowProfile> pointToPointFlowProfiles = this.findPointToPointFlowProfiles(pointToPointFlowProfileNames);
        if (pointToPointFlowProfiles != null && !pointToPointFlowProfiles.isEmpty())
        {
            moduleNames = getModuleNamesFromPointToPointFLowProfiles(pointToPointFlowProfiles);
        }
        return moduleNames;
    }
    
    /**
     * Helper DAO method, returns a list of PointToPointFlowProfiles given their names
     * 
     * @param pointToPointFlowProfileNames - Names to search on
     * @return list of PointToPointFlowProfiles
     */
    private List<PointToPointFlowProfile> findPointToPointFlowProfiles(Set<String> pointToPointFlowProfileNames)
    {
        List<PointToPointFlowProfile> pointToPointFlowProfiles = (List<PointToPointFlowProfile>) getHibernateTemplate().findByNamedParam(POINT_TO_POINT_FLOW_PROFILES_BY_NAME, "names", pointToPointFlowProfileNames);
        // List<PointToPointFlowProfile> pointToPointFlowProfiles = (List<PointToPointFlowProfile>) getHibernateTemplate().find(POINT_TO_POINT_FLOW_PROFILES_BY_NAME, pointToPointFlowProfileNames);
        return pointToPointFlowProfiles;
    }
    
    /**
     * Helper method to extract a Set of Module names from the given list of PointToPointFlowProfiles
     * 
     * @param pointToPointFlowProfiles - Set of PointToPointFlowProfiles to get the module names from 
     * @return A Set of module names
     */
    private Set<String> getModuleNamesFromPointToPointFLowProfiles(List<PointToPointFlowProfile> pointToPointFlowProfiles)
    {
        Set<String> moduleNames = new HashSet<String>();
        for (PointToPointFlowProfile profile : pointToPointFlowProfiles)
        {
            Set<PointToPointFlow> pointToPointFlows = profile.getPointToPointFlows();
            for (PointToPointFlow pointToPointFlow : pointToPointFlows)
            {
                Module fromModule = pointToPointFlow.getFromModule();
                if (fromModule != null)
                {
                    if (fromModule.getName() != null)
                    {
                        moduleNames.add(fromModule.getName());
                    }
                }
                Module toModule = pointToPointFlow.getToModule();
                if (toModule != null)
                {
                    if (toModule.getName() != null)
                    {
                        moduleNames.add(toModule.getName());
                    }
                }
            }
        }
        return moduleNames;
    }
    
}
