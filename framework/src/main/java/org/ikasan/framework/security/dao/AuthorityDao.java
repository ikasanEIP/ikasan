/*
 * $Id: AuthorityDao.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/security/dao/AuthorityDao.java $
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

import org.ikasan.framework.security.model.Authority;

/**
 * Data Access interface for <code>Authority</code> instances
 * 
 * @author Ikasan Development Team
 *
 */
public interface AuthorityDao
{
    /**
     * Retrieves all <code>Authority</code>s known to the system
     * 
     * @return List of <code>Authority</code>s
     */
    public List<Authority> getAuthorities();
    
    /**
     * Retrieves a specific <code>Authority</code> by name
     * 
     * @param authority
     * @return named <code>Authority</code> or null if it does not exist
     */
    public Authority getAuthority(String authority);

    /**
     * Saves a new <code>Authority</code> to persistent storage
     * 
     * @param newAuthority
     */
    public void save(Authority newAuthority);
}
