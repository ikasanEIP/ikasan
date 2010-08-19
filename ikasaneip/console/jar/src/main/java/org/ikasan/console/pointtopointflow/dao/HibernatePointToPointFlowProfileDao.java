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
package org.ikasan.console.pointtopointflow.dao;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>PointToPointFlowDao</code>
 * 
 * @author Ikasan Development Team
 */
public class HibernatePointToPointFlowProfileDao extends HibernateDaoSupport implements PointToPointFlowProfileDao
{

    /** Query for finding all point to point flow profiles based on id */
    private static final String POINT_TO_POINT_FLOW_PROFILES_BY_ID = "from PointToPointFlowProfile p where p.id in (:ids) order by name";
    
    /**
     * @see org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findAllPointToPointFlowProfiles()
     */
    @SuppressWarnings("unchecked")
    public Set<PointToPointFlowProfile> findAllPointToPointFlowProfiles()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
        pointToPointFlowProfiles.addAll(getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(PointToPointFlowProfile.class).addOrder(Order.asc("name"))));
        return pointToPointFlowProfiles;
    }

    /**
     * @see org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao#findPointToPointFlowProfiles(Set)
     */
    public Set<PointToPointFlowProfile> findPointToPointFlowProfiles(Set<Long> pointToPointFlowProfileIds)
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
        pointToPointFlowProfiles.addAll(getHibernateTemplate().findByNamedParam(POINT_TO_POINT_FLOW_PROFILES_BY_ID, "ids", pointToPointFlowProfileIds));
        return pointToPointFlowProfiles;
    }
    
}
