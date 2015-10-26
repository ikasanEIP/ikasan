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
package org.ikasan.topology.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>TopologyDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateTopologyDao extends HibernateDaoSupport implements TopologyDao
{

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllServers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Server> getAllServers()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Server.class);

        return (List<Server>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.model.Server)
	 */
	@Override
	public void save(Server server)
	{
		this.getHibernateTemplate().saveOrUpdate(server);		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllModules()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Module> getAllModules()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Module.class);

        return (List<Module>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.model.Module)
	 */
	@Override
	public void save(Module module)
	{
		this.getHibernateTemplate().saveOrUpdate(module);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllFlows()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Flow> getAllFlows()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Flow.class);

        return (List<Flow>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.model.Flow)
	 */
	@Override
	public void save(Flow flow)
	{
		this.getHibernateTemplate().saveOrUpdate(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllBusinessStreams()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessStream> getAllBusinessStreams()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(BusinessStream.class);

        return (List<BusinessStream>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#saveBusinessStream()
	 */
	@Override
	public void saveBusinessStream(BusinessStream businessStream)
	{
		this.getHibernateTemplate().saveOrUpdate(businessStream);		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getBusinessStreamsByUserId()
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(Long userId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getFlowsByServerIdAndModuleId(java.lang.Long, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Flow> getFlowsByServerIdAndModuleId(Long serverId, Long moduleId)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Flow.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if(serverId != null && moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId))
				.createCriteria("server").add(Restrictions.eq("id", serverId));
		}
		else if(moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId));
		}
		else if(serverId != null)
		{
			criteria.createCriteria("module").createCriteria("server"
					).add(Restrictions.eq("id", serverId));
		}
			

        return (List<Flow>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteBusinessStreamFlow(org.ikasan.topology.model.BusinessStreamFlow)
	 */
	@Override
	public void deleteBusinessStreamFlow(BusinessStreamFlow businessStreamFlow)
	{
		this.getHibernateTemplate().delete(businessStreamFlow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getModuleByName(java.lang.String)
	 */
	@Override
	public Module getModuleByName(String name)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Module.class);
		criteria.add(Restrictions.eq("name", name));

        return (Module)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getBusinessStreamsByUserId(java.util.List)
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(List<Long> ids)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(BusinessStream.class);
		criteria.add(Restrictions.in("id", ids));

        return (List<BusinessStream>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getFlowsByServerIdModuleIdAndFlowname(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public Flow getFlowByServerIdModuleIdAndFlowname(Long serverId,
			Long moduleId, String flowName)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Flow.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if(serverId != null && moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId))
				.createCriteria("server").add(Restrictions.eq("id", serverId));
		}
		else if(moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId));
		}
		else if(serverId != null)
		{
			criteria.createCriteria("module").createCriteria("server"
					).add(Restrictions.eq("id", serverId));
		}
			
		if(flowName != null)
		{
			criteria.add(Restrictions.eq("name", flowName));
		}

		return (Flow)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#delete(org.ikasan.topology.model.Flow)
	 */
	@Override
	public void delete(Flow flow)
	{
		this.getHibernateTemplate().delete(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#delete(org.ikasan.topology.model.Component)
	 */
	@Override
	public void delete(Component component)
	{
		this.getHibernateTemplate().delete(component);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.model.Component)
	 */
	@Override
	public void save(Component component)
	{
		this.getHibernateTemplate().saveOrUpdate(component);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteBusinessStream(org.ikasan.topology.model.BusinessStream)
	 */
	@Override
	public void deleteBusinessStream(BusinessStream businessStream)
	{
		this.getHibernateTemplate().delete(businessStream);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getFlowsByServerIdModuleIdAndNotInFlownames(java.lang.Long, java.lang.Long, java.util.List)
	 */
	@Override
	public List<Flow> getFlowsByServerIdModuleIdAndNotInFlownames(
			Long serverId, Long moduleId, List<String> flowName)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Flow.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if(serverId != null && moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId))
				.createCriteria("server").add(Restrictions.eq("id", serverId));
		}
		else if(moduleId != null)
		{
			criteria.createCriteria("module").add(Restrictions.eq("id", moduleId));
		}
		else if(serverId != null)
		{
			criteria.createCriteria("module").createCriteria("server"
					).add(Restrictions.eq("id", serverId));
		}
			
		if(flowName != null)
		{
			criteria.add(Restrictions.not(Restrictions.in("name", flowName)));
		}

		return (List<Flow>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<Component> getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(
			Long serverId, Long moduleId, String flowName, List<String> componentNames)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Component.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		criteria.createCriteria("flow").add(Restrictions.eq("name", flowName))
			.createCriteria("module").add(Restrictions.eq("id", moduleId))
				.createCriteria("server").add(Restrictions.eq("id", serverId));

		criteria.add(Restrictions.not(Restrictions.in("name", componentNames)));

		return (List<Component>)this.getHibernateTemplate().findByCriteria(criteria);
	}

    
   
}
