/*
 * $Id: Authority.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/security/model/Authority.java $
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
package org.ikasan.framework.security.model;

import org.springframework.security.GrantedAuthority;

/**
 * Implementation of <code>GrantedAuthority</code> adding a description field
 * and identity field suitable for ORM 
 * 
 * @author Ikasan Development Team
 *
 */
public class Authority implements GrantedAuthority
{


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8215891256811078920L;

    /**
     * Identitiy field required for ORM
     */
    private Long id;
    
    /**
     * Name of the authority
     */
    private String authority;
    
    
    /**
     * Description of this authority
     */
    private String description;



    /**
     * No args constructor required by ORM
     */
    @SuppressWarnings("unused")
    private Authority(){}
    
    /**
     * Constructor
     * 
     * @param authority
     */
    public Authority(String authority)
    {
        super();
        this.authority = authority;
    }

    /**
     * Constructor
     * 
     * @param authority
     * @param description
     */
    public Authority(String authority, String description)
    {
        this(authority);
        this.description = description;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        return this.authority.compareTo(((Authority)o).getAuthority());
    }



    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other)
    {
        return authority.equals( ((Authority)other).getAuthority());
    }



    /* (non-Javadoc)
     * @see org.springframework.security.GrantedAuthority#getAuthority()
     */
    public String getAuthority()
    {
        return authority;
    }



    /**
     * Accessor method for description
     * 
     * @return description
     */
    public String getDescription()
    {
        return description;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return authority.hashCode();
    }



    /**
     * Setter method for Authority
     * 
     * @param authority
     */
    public void setAuthority(String authority)
    {
        this.authority = authority;
    }



    /**
     * Setter method for description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * Accessor method for id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Setter method for id, used by ORM
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }   
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer(" Authority [");
        sb.append("authority=");sb.append(authority);sb.append(", ");
        sb.append("description=");sb.append(description);
        sb.append("]");
        return sb.toString();
    }
}
