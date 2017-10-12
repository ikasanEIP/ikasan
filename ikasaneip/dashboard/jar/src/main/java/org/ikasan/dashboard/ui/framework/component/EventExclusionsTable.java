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
package org.ikasan.dashboard.ui.framework.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;

import com.vaadin.ui.Component;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class EventExclusionsTable extends DashboardTable
{
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	/**
	 * Constructor
	 * 
	 * @param caption
	 */
	public EventExclusionsTable(String caption, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService)
	{
		super(caption);
		
		this.exclusionManagementService = exclusionManagementService;
		if(exclusionManagementService == null)
		{
			throw new IllegalArgumentException("exclusionManagementService cannot be null!");
		}
		
		init();
	}
	
	protected void init()
	{
		addContainerProperty("Module Name", String.class,  null);
        addContainerProperty("Flow Name", String.class,  null);
        addContainerProperty("Timestamp", String.class,  null);
        
        setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        
        setItemDescriptionGenerator(new ItemDescriptionGenerator() 
        {                             
	    	public String generateDescription(Component source, Object itemId, Object propertyId) 
	    	{
	    	    return ((ExclusionEvent)itemId).getIdentifier();
	    	}
	    });
	}
	
	public void populate()
	{
		List<ExclusionEvent> exclusionEvents = exclusionManagementService.findAll();
		
		for(ExclusionEvent exclusionEvent: exclusionEvents)
		{
			Date date = new Date(exclusionEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    	    String timestamp = format.format(date);
    	    
			this.addItem(new Object[] {exclusionEvent.getModuleName(),
					exclusionEvent.getFlowName(), timestamp}, exclusionEvent);
		}
	}
}
