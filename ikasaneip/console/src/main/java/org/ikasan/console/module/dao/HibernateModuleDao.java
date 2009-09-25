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
package org.ikasan.console.module.dao;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.ikasan.console.module.Module;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>ModuleDao</code>
 * 
 * @author Ikasan Development Team
 */
public class HibernateModuleDao extends HibernateDaoSupport implements ModuleDao
{

    /** Query for finding all modules based on id */
    private static final String MODULES_BY_ID = "from Module m where m.id in (:ids) order by name";
    
    /**
     * @see org.ikasan.console.module.dao.ModuleDao#findAllModules()
     */
    @SuppressWarnings("unchecked")
    public Set<Module> findAllModules()
    {
        Set<Module> modules = new LinkedHashSet<Module>();
        modules.addAll(getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Module.class).addOrder(Order.asc("name"))));
        return modules;
    }

    /**
     * @see org.ikasan.console.module.dao.ModuleDao#findModules(Set)
     */
    public Set<Module> findModules(Set<Long> modulesIds)
    {
        Set<Module> modules = new LinkedHashSet<Module>();
        modules.addAll(getHibernateTemplate().findByNamedParam(MODULES_BY_ID, "ids", modulesIds));
        return modules;
    }
    
}
