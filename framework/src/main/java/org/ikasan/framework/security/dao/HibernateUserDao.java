/*
 * $Id: HibernateUserDao.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/security/dao/HibernateUserDao.java $
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
package org.ikasan.framework.security.dao;

import java.util.List;

import org.ikasan.framework.security.model.User;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateUserDao extends HibernateDaoSupport implements UserDao
{

    
    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#getUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public User getUser(String username)
    {
        List<User> results = getHibernateTemplate().find("from User where username  = ?",username);
        User result = null;
        if (!results.isEmpty()){
            result = results.get(0);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#getUsers()
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers()
    {
        return getHibernateTemplate().loadAll(User.class);
    }

    public void save(User user)
    {
        getHibernateTemplate().saveOrUpdate(user);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#delete(org.ikasan.framework.security.model.User)
     */
    public void delete(User user)
    {
        getHibernateTemplate().delete(user);
        
    }


    
}
