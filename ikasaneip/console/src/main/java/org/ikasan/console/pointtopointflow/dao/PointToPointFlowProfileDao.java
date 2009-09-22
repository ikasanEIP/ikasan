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

import java.util.Set;

import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;

/**
 * Interface for all PointToPointFlowProfile data access.
 * 
 * @author Ikasan Development Team
 */
public interface PointToPointFlowProfileDao
{

    /**
     * Get a list of all PointToPointFlowProfiles
     * 
     * @return A list of PointToPointFlowProfiles
     */
    public Set<PointToPointFlowProfile> findAllPointToPointFlowProfiles();

    /**
     * Get a list of all PointToPointFlowProfiles matching the Set of names passed in
     * 
     * @param pointToPointFlowProfileNames - Names of the point to point profiles 
     * @return A list of PointToPointFlowProfiles
     */
    public Set<PointToPointFlowProfile> findPointToPointFlowProfiles(Set<String> pointToPointFlowProfileNames);

}
