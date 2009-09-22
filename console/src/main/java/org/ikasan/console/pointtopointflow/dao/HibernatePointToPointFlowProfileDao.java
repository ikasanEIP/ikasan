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
import java.util.Set;

import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
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
    public Set<PointToPointFlowProfile> findAllPointToPointFlowProfiles()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = new HashSet<PointToPointFlowProfile>();
        pointToPointFlowProfiles.addAll(getHibernateTemplate().loadAll(PointToPointFlowProfile.class));
        return pointToPointFlowProfiles;
    }

    /**
     * Returns a list of PointToPointFlowProfiles given their names
     * 
     * @param pointToPointFlowProfileNames - Names to search on
     * @return list of PointToPointFlowProfiles
     */
    public Set<PointToPointFlowProfile> findPointToPointFlowProfiles(Set<String> pointToPointFlowProfileNames)
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = new HashSet<PointToPointFlowProfile>();
        pointToPointFlowProfiles.addAll(getHibernateTemplate().findByNamedParam(POINT_TO_POINT_FLOW_PROFILES_BY_NAME, "names", pointToPointFlowProfileNames));
        return pointToPointFlowProfiles;
    }
    
}
