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
package org.ikasan.framework.security.dao;

import java.util.List;

import org.ikasan.framework.security.model.Authority;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>AuthorityDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateAuthorityDao extends HibernateDaoSupport implements AuthorityDao
{
    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.AuthorityDao#getAuthorities()
     */
    @SuppressWarnings("unchecked")
    public List<Authority> getAuthorities()
    {
        return getHibernateTemplate().loadAll(Authority.class);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.AuthorityDao#getAuthority(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Authority getAuthority(String authority)
    {
        List<Authority> results = getHibernateTemplate().find("from Authority where authority  = ?",authority);
        Authority result = null;
        if (!results.isEmpty()){
            result = results.get(0);
        }
        
        return result;    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.AuthorityDao#save(org.ikasan.framework.security.model.Authority)
     */
    public void save(Authority newAuthority)
    {
        getHibernateTemplate().save(newAuthority);
        
    }
}
