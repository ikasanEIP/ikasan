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
package org.ikasan.console.pointtopointflow.service;

import java.util.HashSet;
import java.util.Set;

import org.ikasan.console.module.Module;
import org.ikasan.console.pointtopointflow.PointToPointFlow;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao;

/**
 * Console implementation of <code>PointToPointFlowProfileService</code>
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowProfileServiceImpl implements PointToPointFlowProfileService
{

    /** DAO for this service to use */
    private PointToPointFlowProfileDao pointToPointFlowProfileDao = null;
    
    /** 
     * Constructor 
     * 
     * @param pointToPointFlowProfileDao - DAO for this service to use
     */
    public PointToPointFlowProfileServiceImpl(PointToPointFlowProfileDao pointToPointFlowProfileDao)
    {
        super();
        this.pointToPointFlowProfileDao = pointToPointFlowProfileDao;
    }

    /**
     * Get a list of all PointToPointFlowProfiles
     * 
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getAllPointToPointFlowProfiles()
     */
    public Set<PointToPointFlowProfile> getAllPointToPointFlowProfiles()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = new HashSet<PointToPointFlowProfile>();
        pointToPointFlowProfiles = pointToPointFlowProfileDao.findAllPointToPointFlowProfiles();
        return pointToPointFlowProfiles;
    }

    /**
     * Get a list of all PointToPointFlowProfile Names
     * 
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getAllPointToPointFlowProfileNames()
     */
    public Set<String> getAllPointToPointFlowProfileNames()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = this.getAllPointToPointFlowProfiles();
        Set<String> pointToPointFlowProfileNames = new HashSet<String>();
        for (PointToPointFlowProfile pointToPointFlowProfile : pointToPointFlowProfiles)
        {
            pointToPointFlowProfileNames.add(pointToPointFlowProfile.getName());
        }
        return pointToPointFlowProfileNames;
    }
    
    /**
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getAllModuleNames()
     */
    public Set<String> getAllModuleNames()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = this.getAllPointToPointFlowProfiles();
        return this.getModuleNamesFromProfiles(pointToPointFlowProfiles);
    }
    
    /**
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getModuleNames(Set)
     */
    public Set<String> getModuleNames(Set<String> pointToPointFlowProfileNames)
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileDao.findPointToPointFlowProfiles(pointToPointFlowProfileNames);
        return this.getModuleNamesFromProfiles(pointToPointFlowProfiles);
    }

    /**
     * Helper method, retrieves MethodNames from the PointToPointFlowProfiles given
     * 
     * @param pointToPointFlowProfiles - PointToPointFlowProfiles to get Module Names from
     * @return Set of Module names
     */
    private Set<String> getModuleNamesFromProfiles(Set<PointToPointFlowProfile> pointToPointFlowProfiles)
    {
        Set<String> moduleNames = new HashSet<String>();
        for (PointToPointFlowProfile profile : pointToPointFlowProfiles)
        {
            for (PointToPointFlow pointToPointFlow : profile.getPointToPointFlows())
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
