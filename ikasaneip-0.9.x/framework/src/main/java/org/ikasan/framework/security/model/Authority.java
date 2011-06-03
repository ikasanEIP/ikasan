/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
