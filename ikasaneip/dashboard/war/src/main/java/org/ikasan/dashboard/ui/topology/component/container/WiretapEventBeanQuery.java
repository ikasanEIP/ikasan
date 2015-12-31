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
package org.ikasan.dashboard.ui.topology.component.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.dao.WiretapDao;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapEventBeanQuery extends AbstractBeanQuery<WiretapEvent>
{
	public static final String WIRETAP_SERVICE = "wiretapService";
	
	public static final String MODULE_NAMES = "moduleNames";
	public static final String FLOW_NAMES = "flowNames";
	public static final String COMPONENT_NAMES = "componentNames";
	public static final String EVENT_ID = "eventId";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	
	
	private Logger logger = Logger.getLogger(WiretapEventBeanQuery.class);
		
	private WiretapDao wiretapDao;

	/**
	 * @param queryDefinition
	 * @param queryConfiguration
	 * @param sortPropertyIds
	 * @param sortStates
	 */
	public WiretapEventBeanQuery(QueryDefinition queryDefinition,
			Map<String, Object> queryConfiguration, Object[] sortPropertyIds,
			boolean[] sortStates)
	{
		super(queryDefinition, queryConfiguration, sortPropertyIds, sortStates);
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery#constructBean()
	 */
	@Override
	protected WiretapEvent constructBean()
	{
		logger.info("Construct bean!");
		
		return new WiretapFlowEvent();
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery#loadBeans(int, int)
	 */
	@Override
	protected List<WiretapEvent> loadBeans(int startIndex, int count1)
	{
		logger.info("Load beans! " + startIndex + " " + count1);
		
		HashSet<String> moduleNames = (HashSet<String>)super.getQueryConfiguration().get(MODULE_NAMES);
		HashSet<String> flowNames = (HashSet<String>)super.getQueryConfiguration().get(FLOW_NAMES);
		HashSet<String> componentNames = (HashSet<String>)super.getQueryConfiguration().get(COMPONENT_NAMES);
		String eventId = (String)super.getQueryConfiguration().get(EVENT_ID);
		
		this.wiretapDao = (WiretapDao)super.getQueryConfiguration().get(WIRETAP_SERVICE);
		
		List<WiretapEvent> wiretapEvents = new ArrayList<WiretapEvent>();
		
		if(this.wiretapDao == null)
		{
			return wiretapEvents;
		}
		
		int pageNo = 0;
		
		if(startIndex > 0)
		{
			pageNo = startIndex / count1;
		}
		
		PagedSearchResult<WiretapEvent> events =  this.wiretapDao.findWiretapEvents(pageNo, count1, "timestamp", false, moduleNames, flowNames, 
				componentNames, null, null, null, null, null);
		
//    	PagedSearchResult<WiretapEvent> events = wiretapDao.findWiretapEvents(pageNo, count1, "timestamp", false, moduleNames
//    			, flowNames, componentNames, eventId, null, fromDate.getValue(), toDate.getValue(), payloadContent.getValue());
		
		logger.info("Loaded from dao" + events.getResultSize());
		
		for(final WiretapEvent<String> wiretapEvent: events.getPagedResults())
    	{
			wiretapEvents.add(wiretapEvent);
    	}

		logger.info("Loaded " + wiretapEvents.size());
		
		return wiretapEvents;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery#saveBeans(java.util.List, java.util.List, java.util.List)
	 */
	@Override
	protected void saveBeans(List<WiretapEvent> arg0,
			List<WiretapEvent> arg1, List<WiretapEvent> arg2)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery#size()
	 */
	@Override
	public int size()
	{
		logger.info("Size! ");
		return 15000;
	}

}
