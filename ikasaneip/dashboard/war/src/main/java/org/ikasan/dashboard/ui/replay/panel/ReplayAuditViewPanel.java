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
package org.ikasan.dashboard.ui.replay.panel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.*;
import org.ikasan.dashboard.ui.ReplayEventViewPopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.replay.ReplayManagementService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayAuditViewPanel extends Panel
{
	private ReplayAudit replayAudit;
	
	private IndexedContainer tableContainer;
	
	private FilterTable replayAuditTable;
	
	private TextArea comments;
	
	private ReplayManagementService<ReplayEvent, ReplayAudit, ReplayAuditEvent>  replayManagementService;
	
	public ReplayAuditViewPanel(ReplayAudit replayAudit, ReplayManagementService<ReplayEvent, ReplayAudit, ReplayAuditEvent>  replayManagementService) 
	{
		super();
		
		this.replayAudit = replayAudit;
		if(this.replayAudit == null)
		{
			throw new IllegalArgumentException("replayAudit cannot be null!");
		}
		this.replayManagementService = replayManagementService;
		if(this.replayManagementService == null)
		{
			throw new IllegalArgumentException("replayManagementService cannot be null!");
		}
		
		init();
	}
	
	protected IndexedContainer buildContainer() 
	{			
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Success", Label.class,  null);
		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Event Id / Payload Id", String.class,  null);
		cont.addContainerProperty("Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		
        return cont;
    }

	public void init()
	{
		this.setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);

		layout.addComponent( createPanel());

		this.setContent(layout);
	}

	public Panel createPanel()
	{
		this.setSizeFull();
		
		GridLayout formLayout = new GridLayout(2, 6);
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setColumnExpandRatio(0, 0.2f);
		formLayout.setColumnExpandRatio(1, 0.8f);
		
		Label replayAuditLabel = new Label("Replay Audit");
		replayAuditLabel.setStyleName(ValoTheme.LABEL_HUGE);
		formLayout.addComponent(replayAuditLabel);
		
		
		Label moduleCountLabel = new Label("Number of events replayed:");
		moduleCountLabel.setSizeUndefined();
		
		formLayout.addComponent(moduleCountLabel, 0, 1);
		formLayout.setComponentAlignment(moduleCountLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleCount = new TextField();
		
		if(this.replayAudit != null)
		{
			moduleCount.setValue(this.replayManagementService.getNumberReplayAuditEventsByAuditId(replayAudit.getId()).toString());
		}
		else
		{
			moduleCount.setValue("0");
		}
		
		moduleCount.setReadOnly(true);
		moduleCount.setWidth("80%");
		formLayout.addComponent(moduleCount, 1, 1);
		
		
		Label userLabel = new Label("User:");
		userLabel.setSizeUndefined();
		
		formLayout.addComponent(userLabel, 0, 2);
		formLayout.setComponentAlignment(userLabel, Alignment.MIDDLE_RIGHT);
		
		TextField userTf = new TextField();
		
		userTf.setWidth("80%");
		formLayout.addComponent(userTf, 1, 2);
		userTf.setValue(this.replayAudit.getUser());
		userTf.setReadOnly(true);
		
		Label targetServerLabel = new Label("Target Server:");
		targetServerLabel.setSizeUndefined();
		
		formLayout.addComponent(targetServerLabel, 0, 3);
		formLayout.setComponentAlignment(targetServerLabel, Alignment.MIDDLE_RIGHT);
		
		TextField targetServerTf = new TextField();
		
		targetServerTf.setWidth("80%");
		formLayout.addComponent(targetServerTf, 1, 3);
		targetServerTf.setValue(this.replayAudit.getTargetServer());
		targetServerTf.setReadOnly(true);
		
		Label commentLabel = new Label("Comment:");
		commentLabel.setSizeUndefined();
		
		formLayout.addComponent(commentLabel, 0, 4);
		formLayout.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);
		
		comments = new TextArea();
		comments.setWidth("80%");
		comments.setRows(4);
		comments.setValue(this.replayAudit.getReplayReason());
		comments.setReadOnly(true);
		
		formLayout.addComponent(comments, 1, 4);
			
		this.replayAuditTable = new FilterTable();
		this.replayAuditTable.setFilterBarVisible(true);
		this.replayAuditTable.setSizeFull();
		this.replayAuditTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.replayAuditTable.addStyleName("ikasan");
		
		this.replayAuditTable.setColumnExpandRatio("Success", .05f);
		this.replayAuditTable.setColumnExpandRatio("Module Name", .14f);
		this.replayAuditTable.setColumnExpandRatio("Flow Name", .18f);
		this.replayAuditTable.setColumnExpandRatio("Event Id / Payload Id", .14f);
		this.replayAuditTable.setColumnExpandRatio("Message", .4f);
		this.replayAuditTable.setColumnExpandRatio("Timestamp", .1f);
		
		this.replayAuditTable.addStyleName("wordwrap-table");
		this.replayAuditTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		
		tableContainer = this.buildContainer();
		this.replayAuditTable.setContainerDataSource(tableContainer);
		
		this.replayAuditTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{

		    	}
		    }
		});
		
		List<ReplayAuditEvent> auditEvents = this.replayManagementService.getReplayAuditEventsByAuditId(replayAudit.getId());
		
		for(final ReplayAuditEvent replayEvent: auditEvents)
    	{
    		Date date = new Date(replayEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    Item item = tableContainer.addItem(replayEvent);	
    	    
    	    if(replayEvent.isSuccess())
    	    {
    	    	item.getItemProperty("Success").setValue(new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML));
    	    }
    	    else
    	    {
    	    	item.getItemProperty("Success").setValue(new Label(VaadinIcons.BAN.getHtml(), ContentMode.HTML));
    	    }
    	    
    	    item.getItemProperty("Module Name").setValue(replayEvent.getReplayEvent().getModuleName());
			item.getItemProperty("Flow Name").setValue(replayEvent.getReplayEvent().getFlowName());
			item.getItemProperty("Event Id / Payload Id").setValue(replayEvent.getReplayEvent().getEventId());
			item.getItemProperty("Message").setValue(replayEvent.getResultMessage());
			item.getItemProperty("Timestamp").setValue(timestamp);
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);
			
			BrowserWindowOpener popupOpener = new BrowserWindowOpener(ReplayEventViewPopup.class);
			popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
//	            	 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("replayEvent", (ReplayEvent)replayEvent);
	            }
	        });
	        
//	        item.getItemProperty("").setValue(popupButton);
    	}
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setWidth("100%");
		layout.setMargin(true);
		
		layout.addComponent(formLayout);
		layout.addComponent(this.replayAuditTable);

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setStyleName("dashboard");

		panel.setContent(layout);

		return panel;
	}
}
