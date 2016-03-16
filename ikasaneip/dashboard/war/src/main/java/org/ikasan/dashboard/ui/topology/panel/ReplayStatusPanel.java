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
package org.ikasan.dashboard.ui.topology.panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayListener;
import org.ikasan.spec.replay.ReplayService;
import org.tepi.filtertable.FilterTable;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayStatusPanel extends Panel implements ReplayListener<ReplayEvent>
{
	private List<ReplayEvent> replayEvents;
	
	private IndexedContainer tableContainer;
	
	private FilterTable replayEventsTable;
	
	private ReplayService<ReplayEvent> replayService;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private ComboBox targetServerComboBox;
	
	public ReplayStatusPanel(List<ReplayEvent> replayEvents,
			ReplayService<ReplayEvent> replayService,
			PlatformConfigurationService platformConfigurationService) 
	{
		super();
		
		this.replayEvents = replayEvents;
		if(this.replayEvents == null)
		{
			throw new IllegalArgumentException("replayEvents cannot be null!");
		}
		this.replayService = replayService;
		if(this.replayService == null)
		{
			throw new IllegalArgumentException("replayService cannot be null!");
		}
		this.platformConfigurationService = platformConfigurationService;
		if(this.platformConfigurationService == null)
		{
			throw new IllegalArgumentException("platformConfigurationService cannot be null!");
		}
		
		init();
	}
	
	protected IndexedContainer buildContainer() 
	{			
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Event Id / Payload Id", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", CheckBox.class,  null);
		
        return cont;
    }

	public void init()
	{
		this.setSizeFull();
		
		GridLayout formLayout = new GridLayout(2, 6);
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setColumnExpandRatio(0, 0.2f);
		formLayout.setColumnExpandRatio(1, 0.8f);
		
		Label wiretapDetailsLabel = new Label("Replay");
		wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		formLayout.addComponent(wiretapDetailsLabel);
		
		
		Label moduleCountLabel = new Label("Number of events to replay:");
		moduleCountLabel.setSizeUndefined();
		
		formLayout.addComponent(moduleCountLabel, 0, 1);
		formLayout.setComponentAlignment(moduleCountLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleCount = new TextField();
		
		if(this.replayEvents != null)
		{
			moduleCount.setValue(Integer.toString(this.replayEvents.size()));
		}
		else
		{
			moduleCount.setValue("0");
		}
		
		moduleCount.setReadOnly(true);
		moduleCount.setWidth("80%");
		formLayout.addComponent(moduleCount, 1, 1);
		
		
		Label targetServerLabel = new Label("Target server:");
		targetServerLabel.setSizeUndefined();
		
		formLayout.addComponent(targetServerLabel, 0, 2);
		formLayout.setComponentAlignment(targetServerLabel, Alignment.MIDDLE_RIGHT);
		
		this.initialiseTargetServerCombo();
		
		this.targetServerComboBox.setWidth("80%");
		formLayout.addComponent(this.targetServerComboBox, 1, 2);
		
		this.replayEventsTable = new FilterTable();
		this.replayEventsTable.setFilterBarVisible(true);
		this.replayEventsTable.setSizeFull();
		this.replayEventsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.replayEventsTable.addStyleName("ikasan");
		
		this.replayEventsTable.setColumnExpandRatio("Module Name", .14f);
		this.replayEventsTable.setColumnExpandRatio("Flow Name", .18f);
		this.replayEventsTable.setColumnExpandRatio("Event Id / Payload Id", .33f);
		this.replayEventsTable.setColumnExpandRatio("Timestamp", .1f);
		this.replayEventsTable.setColumnExpandRatio("", .05f);
		
		this.replayEventsTable.addStyleName("wordwrap-table");
		this.replayEventsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		
		tableContainer = this.buildContainer();
		this.replayEventsTable.setContainerDataSource(tableContainer);
		
		this.replayEventsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
//			    	WiretapEvent<String> wiretapEvent = (WiretapEvent<String>)itemClickEvent.getItemId();
//			    	WiretapPayloadViewWindow wiretapPayloadViewWindow = new WiretapPayloadViewWindow(wiretapEvent);
//			    
//			    	UI.getCurrent().addWindow(wiretapPayloadViewWindow);
		    	}
		    }
		});
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setWidth("100%");
		layout.setMargin(true);
		
		layout.addComponent(formLayout);
		layout.addComponent(this.replayEventsTable);
		
		this.setContent(layout);
	}
	
	private List<String> getValidTargetServers()
	{
		String replayTargetServers = this.platformConfigurationService.getConfigurationValue("replayTargetServers");
		
		if(replayTargetServers != null && replayTargetServers.length() > 0)
		{
			return Arrays.asList(replayTargetServers.split(","));
		}
		else
		{
			return new ArrayList<String>();
		}
	}
	
	private void initialiseTargetServerCombo()
	{
		if(this.targetServerComboBox == null)
		{
			this.targetServerComboBox = new ComboBox();
		}
		
		this.targetServerComboBox.removeAllItems();
		
		List<String> targetServers = this.getValidTargetServers();
		
		for(String targetServer: targetServers)
		{
			this.targetServerComboBox.addItem(targetServer);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayListener#onReplay(java.lang.Object)
	 */
	@Override
	public void onReplay(ReplayEvent event) 
	{
		// TODO Auto-generated method stub
		
	}
}
