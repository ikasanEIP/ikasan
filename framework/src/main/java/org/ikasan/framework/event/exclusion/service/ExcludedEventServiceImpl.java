/**
 * 
 */
package org.ikasan.framework.event.exclusion.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;

/**
 * @author The Ikasan Development Service
 *
 */
public class ExcludedEventServiceImpl implements ExcludedEventService {
	
	private List<ExcludedEventListener> excludedEventListeners = new ArrayList<ExcludedEventListener>();

	private ExcludedEventDao excludedEventDao;
	
	/**
	 * @param excludedEventDao
	 * @param listeners
	 */
	public ExcludedEventServiceImpl(ExcludedEventDao excludedEventDao,
			List<ExcludedEventListener> listeners) {
		this.excludedEventDao = excludedEventDao;
		excludedEventListeners.addAll(listeners);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.event.exclusion.service.EventExclusionService#excludeEvent(org.ikasan.framework.component.Event)
	 */
	public void excludeEvent(Event event) {
		for (ExcludedEventListener excludedEventListener : excludedEventListeners){
			excludedEventListener.notifyExcludedEvent(event);
		}

	}

}
