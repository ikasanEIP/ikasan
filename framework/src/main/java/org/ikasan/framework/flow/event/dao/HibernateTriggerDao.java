/*
 * $Id: HibernateTriggerDao.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/event/dao/HibernateTriggerDao.java $
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
package org.ikasan.framework.flow.event.dao;

import java.util.List;

import org.ikasan.framework.flow.event.model.Trigger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation for the <code>TriggerDao</code> interface
 * 
 * @author Ikasan Development Team
 */
public class HibernateTriggerDao extends HibernateDaoSupport implements TriggerDao
{
    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#delete(org.ikasan.framework.flow.event.model.Trigger)
     */
    public void delete(Trigger trigger)
    {
        getHibernateTemplate().delete(trigger);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<Trigger> findAll()
    {
        return getHibernateTemplate().loadAll(Trigger.class);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#findById(java.lang.Long)
     */
    public Trigger findById(Long triggerId)
    {
        return (Trigger) getHibernateTemplate().get(Trigger.class, triggerId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#save(org.ikasan.framework.flow.event.model.Trigger)
     */
    public void save(Trigger trigger)
    {
        getHibernateTemplate().saveOrUpdate(trigger);
    }
}
