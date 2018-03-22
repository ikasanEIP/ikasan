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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapCustomQuery implements Query
{
	private Logger logger = LoggerFactory.getLogger(WiretapCustomQuery.class);
	
	public static final String WIRETAP_SERVICE = "wiretapService";
	
	public static final String MODULE_NAMES = "moduleNames";
	public static final String FLOW_NAMES = "flowNames";
	public static final String COMPONENT_NAMES = "componentNames";
	public static final String EVENT_ID = "eventId";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	
	private QueryDefinition queryDefinition;
	private HashMap<String, Object> queryConfiguration;

	/**
	 * @param queryDefinition
	 */
	public WiretapCustomQuery(QueryDefinition queryDefinition,
			HashMap<String, Object> queryConfiguration)
	{
		super();
		this.queryDefinition = queryDefinition;
		this.queryConfiguration = queryConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.Query#constructItem()
	 */
	@Override
	public Item constructItem()
	{
		 return new BeanItem<WiretapFlowEvent>(new WiretapFlowEvent());
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.Query#deleteAllItems()
	 */
	@Override
	public boolean deleteAllItems()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.Query#loadItems(int, int)
	 */
	@Override
	public List<Item> loadItems(int startIndex, int count1)
	{
		logger.debug("Load beans! " + startIndex + " " + count1);
		
		HashSet<String> moduleNames = (HashSet<String>)queryConfiguration.get(MODULE_NAMES);
		HashSet<String> flowNames = (HashSet<String>)queryConfiguration.get(FLOW_NAMES);
		HashSet<String> componentNames = (HashSet<String>)queryConfiguration.get(COMPONENT_NAMES);
		String eventId = (String)queryConfiguration.get(EVENT_ID);
		
		WiretapDao wiretapDao = (WiretapDao)queryConfiguration.get(WIRETAP_SERVICE);
		
		List<WiretapEvent> wiretapEvents = new ArrayList<WiretapEvent>();
		
		if(wiretapDao == null)
		{
			return null;
		}
		
		int pageNo = 0;
		
		if(startIndex > 0)
		{
			pageNo = startIndex / count1;
		}
		
		PagedSearchResult<WiretapEvent> events =  wiretapDao.findWiretapEvents(pageNo, count1, "timestamp", false, moduleNames, flowNames, 
				componentNames, null, null, null, null, null);
		
//    	PagedSearchResult<WiretapEvent> events = wiretapDao.findWiretapEvents(pageNo, count1, "timestamp", false, moduleNames
//    			, flowNames, componentNames, eventId, null, fromDate.getValue(), toDate.getValue(), payloadContent.getValue());
		
		logger.debug("Loaded from dao" + events.getResultSize());
		
//		for(final WiretapEvent<String> wiretapEvent: events.getPagedResults())
//    	{
//    		Date date = new Date(wiretapEvent.getTimestamp());
//    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
//    	    String timestamp = format.format(date);
//    	    
//    	    Item item = tableContainer.addItem(wiretapEvent);			            	    
//    	    
//    	    item.getItemProperty("Module Name").setValue(wiretapEvent.getModuleName());
//			item.getItemProperty("Flow Name").setValue(wiretapEvent.getFlowName());
//			item.getItemProperty("Component Name").setValue(wiretapEvent.getComponentName());
//			item.getItemProperty("Event Id / Payload Id").setValue(((WiretapFlowEvent)wiretapEvent).getEventId());
//			item.getItemProperty("Timestamp").setValue(timestamp);
//			
//			CheckBox cb = new CheckBox();
//			cb.setImmediate(true);
//			cb.setDescription("Select in order to add to bulk download.");
//			
//			item.getItemProperty("").setValue(cb);
//			
//			Button popupButton = new Button();
//			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
//			popupButton.setDescription("Open in new tab");
//			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
//			popupButton.setIcon(VaadinIcons.MODAL);
//			
//			BrowserWindowOpener popupOpener = new BrowserWindowOpener(WiretapPopup.class);
//	        popupOpener.extend(popupButton);
//	        
//	        popupButton.addClickListener(new Button.ClickListener() 
//	    	{
//	            public void buttonClick(ClickEvent event) 
//	            {
//	            	 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("wiretapEvent", (WiretapFlowEvent)wiretapEvent);
//	            }
//	        });
//	        
//	        item.getItemProperty(" ").setValue(popupButton);
//    	}
		
		for(final WiretapEvent<String> wiretapEvent: events.getPagedResults())
    	{
			wiretapEvents.add(wiretapEvent);
    	}

		logger.debug("Loaded " + wiretapEvents.size());
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.Query#saveItems(java.util.List, java.util.List, java.util.List)
	 */
	@Override
	public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.vaadin.addons.lazyquerycontainer.Query#size()
	 */
	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
