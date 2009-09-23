/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
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
