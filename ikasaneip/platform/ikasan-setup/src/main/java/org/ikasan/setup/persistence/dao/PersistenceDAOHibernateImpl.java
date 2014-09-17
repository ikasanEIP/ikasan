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
package org.ikasan.setup.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;
import java.util.Properties;

/**
 * Implementation of the ProviderDAO contract
 * @author Ikasan Development Team
 */
public class PersistenceDAOHibernateImpl extends HibernateDaoSupport implements ProviderDAO
{
    private static String create = "create.";
    private static String delete = "drop.";
    private static String find = "find.";
    private static String getIkasanVersion = "get.ikasan.version";

    /** set of sql commands specific to this provider DAO */
    Properties properties;

    /**
     * Constructor
     * @param properties
     */
    public PersistenceDAOHibernateImpl(Properties properties) {
        this.properties = properties;
        if (properties == null)
        {
            throw new IllegalArgumentException("properties cannot be 'null");
        }
    }

    @Override
    public String getRuntimeVersion()
    {
        return getHibernateTemplate().execute(new HibernateCallback<String>() {
            public String doInHibernate(Session session) throws HibernateException {

                String sql = properties.getProperty(getIkasanVersion);
                Query query = session.createSQLQuery(sql);
                Object result = query.uniqueResult();
                if(result instanceof String) { return (String)result; } else { return null;}

            }
        });
    }

    @Override
    public void create(final String resourceName)
    {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {

                Query query = session.createSQLQuery( properties.getProperty(create + resourceName) );
                query.executeUpdate();
                return null;
            }
        });
    }


    @Override
    public void delete(final String resourceName)
    {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {

                Query query = session.createSQLQuery( properties.getProperty(delete + resourceName) );
                query.executeUpdate();
                return null;
            }
        });
    }


    @Override
    public List find(final String resourceName)
    {
        return getHibernateTemplate().execute(new HibernateCallback<List>() {

            public List doInHibernate(Session session) throws HibernateException {

                Query query = session.createSQLQuery( properties.getProperty(find + resourceName) );
                return query.list();
            }

        });
    }
}
