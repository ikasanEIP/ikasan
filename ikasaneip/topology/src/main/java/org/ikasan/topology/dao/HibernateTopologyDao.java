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

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.topology.model.*;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of <code>TopologyDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateTopologyDao extends HibernateDaoSupport implements TopologyDao
{

	private static final String DELETE_FILTER_COMPONENT_BY_FILTER_ID = "delete from FilterComponent where id.filterId = :filterId"; 
	private static final String DELETE_FILTER_COMPONENT_BY_COMPONENT_ID = "delete from FilterComponent where id.componentId = :componentId";
	private static final String DELETE_BUSINESS_STREAM_FLOW_BY_FLOW_ID = "delete from BusinessStreamFlow where id.flowId = :flowId";

	private static final String GET_FLOWS_BY_SERVERID_AND_MODULEID_QUERY = "select distinct(f) from Flow as f " +
        " LEFT JOIN f.module m LEFT JOIN m.server s " +
        " where  "
        + "" ;


    private static final String GET_COMPONENTS_BY_SERVERID_AND_MODULEID_QUERY = "select distinct(c) from Component c " +
        " LEFT JOIN c.flow f LEFT JOIN f.module m LEFT JOIN m.server s " +
        " where  "
        + "" ;

    private static final String GET_ROLE_FILTER_BY_ROLEID_QUERY = "select r from RoleFilter r" +
        " where  "
        + " id.roleId in (:roleIds)" ;

    private static final String GET_ROLE_FILTER_FILTER_ID_QUERY = "select r from RoleFilter r" +
        " where  "
        + " id.filterId = :filterId" ;

    /* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllServers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Server> getAllServers()
	{
		return getHibernateTemplate().loadAll(Server.class);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.window.Server)
	 */
	@Override
	public void save(Server server)
	{
		server.setUpdatedDateTime(new Date());
		this.getHibernateTemplate().saveOrUpdate(server);		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllModules()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Module> getAllModules()
	{
        return getHibernateTemplate().loadAll(Module.class);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.window.Module)
	 */
	@Override
	public void save(Module module)
	{
		module.setUpdatedDateTime(new Date());
		this.getHibernateTemplate().saveOrUpdate(module);
	}

	@Override
	public void delete(Module module)
	{
		this.getHibernateTemplate().delete(module);
	}

	/* (non-Javadoc)
         * @see org.ikasan.topology.dao.TopologyDao#getAllFlows()
         */
	@SuppressWarnings("unchecked")
	@Override
	public List<Flow> getAllFlows()
	{
        return getHibernateTemplate().loadAll(Flow.class);
	}

	@Override
	public List<Component> getAllComponents()
	{
		return getHibernateTemplate().loadAll(Component.class);
	}

	/* (non-Javadoc)
         * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.window.Flow)
         */
	@Override
	public void save(Flow flow)
	{
		flow.setUpdatedDateTime(new Date());
		this.getHibernateTemplate().saveOrUpdate(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllBusinessStreams()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessStream> getAllBusinessStreams()
	{
		return getHibernateTemplate().loadAll(BusinessStream.class);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#saveBusinessStream()
	 */
	@Override
	public void saveBusinessStream(BusinessStream businessStream)
	{
		businessStream.setUpdatedDateTime(new Date());
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
        return getHibernateTemplate().execute((session) -> {

            StringBuilder queryBuffer = new StringBuilder(GET_FLOWS_BY_SERVERID_AND_MODULEID_QUERY);
            if (serverId != null && moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
                queryBuffer.append(" and s.id = :serverId ");
            }
            else if (moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
            }
            else if (serverId != null)
            {
                queryBuffer.append(" s.id = :serverId ");
            }

            Query query = session.createQuery(queryBuffer.toString());


            if (serverId != null && moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
                query.setParameter("serverId", serverId);
            }
            else if (moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
            }
            else if (serverId != null)
            {
                query.setParameter("serverId", serverId);
            }

            return (List<Flow>) query.list();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteBusinessStreamFlow(org.ikasan.topology.window.BusinessStreamFlow)
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
	    return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Module> criteriaQuery = builder.createQuery(Module.class);
            Root<Module> root = criteriaQuery.from(Module.class);
            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));
            Query<Module> query = session.createQuery(criteriaQuery);
            List<Module> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }
            return results.get(0);
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getBusinessStreamsByUserId(java.util.List)
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(List<Long> ids)
	{

        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<BusinessStream> criteriaQuery = builder.createQuery(BusinessStream.class);
            Root<BusinessStream> root = criteriaQuery.from(BusinessStream.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("id").in(ids));

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]));

            Query<BusinessStream> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getFlowsByServerIdModuleIdAndFlowname(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public Flow getFlowByServerIdModuleIdAndFlowname(Long serverId,
			Long moduleId, String flowName)
	{

		return getHibernateTemplate().execute((session) -> {

            StringBuilder queryBuffer = new StringBuilder(GET_FLOWS_BY_SERVERID_AND_MODULEID_QUERY);
            if (serverId != null && moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
                queryBuffer.append(" and s.id = :serverId ");
            }
            else if (moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
            }
            else if (serverId != null)
            {
                queryBuffer.append(" s.id = :serverId ");
            }
            if (flowName != null)
            {
                queryBuffer.append(" and f.name = :flowName ");
            }

            Query query = session.createQuery(queryBuffer.toString());

            if (serverId != null && moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
                query.setParameter("serverId", serverId);
            }
            else if (moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
            }
            else if (serverId != null)
            {
                query.setParameter("serverId", serverId);
            }
            if (flowName != null)
            {
                query.setParameter("flowName", flowName);
            }

            List<Flow> result =  query.list();
            if(result!=null && !result.isEmpty()){
                return result.get(0);
            }else{
                return null;
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#delete(org.ikasan.topology.window.Flow)
	 */
	@Override
	public void delete(Flow flow)
	{
		this.getHibernateTemplate().delete(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#delete(org.ikasan.topology.window.Component)
	 */
	@Override
	public void delete(Component component)
	{
		this.getHibernateTemplate().delete(component);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.window.Component)
	 */
	@Override
	public void save(Component component)
	{
		component.setUpdatedDateTime(new Date());
		this.getHibernateTemplate().saveOrUpdate(component);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteBusinessStream(org.ikasan.topology.window.BusinessStream)
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
        return getHibernateTemplate().execute((session) -> {

            StringBuilder queryBuffer = new StringBuilder(GET_FLOWS_BY_SERVERID_AND_MODULEID_QUERY);
            if (serverId != null && moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
                queryBuffer.append(" and s.id = :serverId ");
            }
            else if (moduleId != null)
            {
                queryBuffer.append(" m.id = :moduleId ");
            }
            else if (serverId != null)
            {
                queryBuffer.append(" s.id = :serverId ");
            }
            if (flowName != null)
            {
                queryBuffer.append(" and f.name not in (:flowName) ");
            }

            Query query = session.createQuery(queryBuffer.toString());


            if (serverId != null && moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
                query.setParameter("serverId", serverId);
            }
            else if (moduleId != null)
            {
                query.setParameter("moduleId", moduleId);
            }
            else if (serverId != null)
            {
                query.setParameter("serverId", serverId);
            }
            if (flowName != null)
            {
                query.setParameter("flowName", flowName);
            }

            return (List<Flow>) query.list();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<Component> getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(
			Long serverId, Long moduleId, String flowName, List<String> componentNames)
	{

        return getHibernateTemplate().execute((session) -> {

            StringBuilder queryBuffer = new StringBuilder(GET_COMPONENTS_BY_SERVERID_AND_MODULEID_QUERY);
            if (serverId != null && moduleId != null && flowName!=null && componentNames!=null)
            {
                queryBuffer.append(" f.name = :flowName ");
                queryBuffer.append(" and m.id = :moduleId ");

                queryBuffer.append(" and s.id = :serverId ");
                queryBuffer.append(" and c.name not in (:componentNames) ");
            }

            Query query = session.createQuery(queryBuffer.toString());


            if (serverId != null && moduleId != null)
            {
                query.setParameter("flowName", flowName);
                query.setParameter("serverId", serverId);
                query.setParameter("moduleId", moduleId);
                query.setParameter("componentNames", componentNames);
            }

            return (List<Component>) query.list();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#saveFilter(com.unboundid.ldap.sdk.Filter)
	 */
	@Override
	public void saveFilter(Filter filter)
	{
		filter.setUpdatedDateTime(new Date());
		this.getHibernateTemplate().saveOrUpdate(filter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getFilterByName(java.lang.String)
	 */
	@Override
	public Filter getFilterByName(String name)
	{
		return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Filter> criteriaQuery = builder.createQuery(Filter.class);
            Root<Filter> root = criteriaQuery.from(Filter.class);
            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));
            Query<Filter> query = session.createQuery(criteriaQuery);
            List<Filter> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }
            return results.get(0);
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllFilters()
	 */
	@Override
	public List<Filter> getAllFilters()
	{
		return getHibernateTemplate().loadAll(Filter.class);

    }

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#saveRoleFilter(org.ikasan.topology.window.RoleFilter)
	 */
	@Override
	public void saveRoleFilter(RoleFilter roleFilter)
	{
		this.getHibernateTemplate().saveOrUpdate(roleFilter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getRoleFilterByRoleId(java.lang.Long)
	 */
	@Override
	public List<RoleFilter> getRoleFiltersByRoleId(List<Long> roleIds)
	{
		if(roleIds == null || roleIds.isEmpty())
		{
			return new ArrayList<RoleFilter>();
		}

        return getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(GET_ROLE_FILTER_BY_ROLEID_QUERY);
            query.setParameter("roleIds", roleIds);
            return (List<RoleFilter>) query.list();
        });
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getRoleFilterByRoleId(java.lang.Long)
	 */
	@Override
	public RoleFilter getRoleFilterByFilterId(Long filterId)
	{
        return getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(GET_ROLE_FILTER_FILTER_ID_QUERY);
            query.setParameter("filterId", filterId);
            List<RoleFilter> result = query.list();
            if (result!=null && !result.isEmpty())
                return result.get(0);
            else
                return null;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteFilter(org.ikasan.topology.window.Filter)
	 */
	@Override
	public void deleteFilter(Filter filter)
	{
		getHibernateTemplate().delete(filter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteRoleFilter(org.ikasan.topology.window.RoleFilter)
	 */
	@Override
	public void deleteRoleFilter(RoleFilter roleFilter)
	{
		this.getHibernateTemplate().delete(roleFilter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteFilterComponents(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteFilterComponentsByFilterId(final Long filterId)
	{
        this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(DELETE_FILTER_COMPONENT_BY_FILTER_ID);
            query.setParameter("filterId", filterId);
            query.executeUpdate();
            return null;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteFilterComponentsByComponentId(java.lang.Long)
	 */
	@Override
	public void deleteFilterComponentsByComponentId(final Long componentId)
	{
        this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(DELETE_FILTER_COMPONENT_BY_COMPONENT_ID);
            query.setParameter("componentId", componentId);
            query.executeUpdate();
            return null;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#deleteBusinessStreamFlowByFlowId(java.lang.Long)
	 */
	@Override
	public void deleteBusinessStreamFlowByFlowId(final Long flowId)
	{
        this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(DELETE_BUSINESS_STREAM_FLOW_BY_FLOW_ID);
            query.setParameter("flowId", flowId);
            query.executeUpdate();
            return null;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#save(org.ikasan.topology.window.Notification)
	 */
	@Override
	public void save(Notification notification)
	{
		this.getHibernateTemplate().saveOrUpdate(notification);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#delete(org.ikasan.topology.window.Notification)
	 */
	@Override
	public void delete(Notification notification)
	{
		this.getHibernateTemplate().delete(notification);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getNotificationByName(java.lang.String)
	 */
	@Override
	public Notification getNotificationByName(String name)
	{
	    return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Notification> criteriaQuery = builder.createQuery(Notification.class);
            Root<Notification> root = criteriaQuery.from(Notification.class);
            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));
            Query<Notification> query = session.createQuery(criteriaQuery);
            List<Notification> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }
            return results.get(0);
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.dao.TopologyDao#getAllNotifications()
	 */
	@Override
	public List<Notification> getAllNotifications()
	{
		 return getHibernateTemplate().loadAll(Notification.class);
    }




}
