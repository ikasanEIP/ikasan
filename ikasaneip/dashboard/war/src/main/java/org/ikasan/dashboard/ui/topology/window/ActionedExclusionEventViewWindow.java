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
package org.ikasan.dashboard.ui.topology.window;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ActionedExclusionEventViewWindow extends Window
{
	private static Logger logger = Logger.getLogger(ActionedExclusionEventViewWindow.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private ErrorOccurrence errorOccurrence;
	private SerialiserFactory serialiserFactory;
	private ExclusionEventAction action;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	private TopologyService topologyService;

	/**
	 * @param policy
	 */
	public ActionedExclusionEventViewWindow(ErrorOccurrence errorOccurrence, SerialiserFactory serialiserFactory, ExclusionEventAction action,
			HospitalManagementService<ExclusionEventAction> hospitalManagementService, TopologyService topologyService)
	{
		super();
		this.errorOccurrence = errorOccurrence;
		this.serialiserFactory = serialiserFactory;
		this.action = action;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		this.setHeight("90%");
		this.setWidth("90%");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		
		layout.addComponent(createExclusionEventDetailsPanel());
			
		this.setContent(layout);
	}

	protected Panel createExclusionEventDetailsPanel()
	{
		Panel exclusionEventDetailsPanel = new Panel();
		exclusionEventDetailsPanel.setSizeFull();
		exclusionEventDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(4, 7);
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, .10f);
		layout.setColumnExpandRatio(1, .30f);
		layout.setColumnExpandRatio(2, .05f);
		layout.setColumnExpandRatio(3, .30f);
		
		layout.setWidth("100%");
		
		Label exclusionEvenDetailsLabel = new Label("Actioned Exclusion Event Details");
		exclusionEvenDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(exclusionEvenDetailsLabel, 0, 0, 3, 0);
		
		Label label = new Label("Module Name:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.action.getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.action.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Event Id:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.errorOccurrence.getEventLifeIdentifier());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf4 = new TextField();
		tf4.setValue(new Date(this.action.getTimestamp()).toString());
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		label = new Label("Error URI:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 5);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf5 = new TextField();
		tf5.setValue(this.action.getErrorUri());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 5);
		
		label = new Label("Action:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf6 = new TextField();
		if(this.action != null)
		{
			tf6.setValue(action.getAction());
		}
		tf6.setReadOnly(true);
		tf6.setWidth("80%");
		layout.addComponent(tf6, 3, 1);
		
		label = new Label("Actioned By:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf7 = new TextField();
		if(this.action != null)
		{
			tf7.setValue(action.getActionedBy());
		}
		tf7.setReadOnly(true);
		tf7.setWidth("80%");
		layout.addComponent(tf7, 3, 2);
		
		label = new Label("Actioned Time:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf8 = new TextField();
		if(this.action != null)
		{   	    
			tf8.setValue(new Date(action.getTimestamp()).toString());
		}
		tf8.setReadOnly(true);
		tf8.setWidth("80%");
		layout.addComponent(tf8, 3, 3);
		
		
		final AceEditor eventEditor = new AceEditor();
		eventEditor.setCaption("Event Payload");

		Object event = this.serialiserFactory.getDefaultSerialiser().deserialise(this.action.getEvent());
		eventEditor.setValue(event.toString());
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setWidth("100%");
		eventEditor.setHeight(600, Unit.PIXELS);
		
		CheckBox wrapTextCheckBox = new CheckBox("Wrap text");
		wrapTextCheckBox.addValueChangeListener(new Property.ValueChangeListener() 
		{
            @Override
            public void valueChange(ValueChangeEvent event)
            {
                Object value = event.getProperty().getValue();
                boolean isCheck = (null == value) ? false : (Boolean) value;
               
                eventEditor.setWordWrap(isCheck);
            }
        });
		

		
		AceEditor errorEditor = new AceEditor();
		errorEditor.setCaption("Error Details");
		errorEditor.setValue(this.errorOccurrence.getErrorDetail());
		errorEditor.setReadOnly(true);
		errorEditor.setMode(AceMode.xml);
		errorEditor.setTheme(AceTheme.eclipse);
		errorEditor.setWidth("100%");
		errorEditor.setHeight(600, Unit.PIXELS);
		
		
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		VerticalLayout h1 = new VerticalLayout();
		h1.setSizeFull();
		h1.setMargin(true);
		h1.addComponent(wrapTextCheckBox);
		h1.addComponent(eventEditor);
		
		HorizontalLayout h2 = new HorizontalLayout();
		h2.setSizeFull();
		h2.setMargin(true);
		h2.addComponent(errorEditor);
		
		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.setWidth("100%");
		formLayout.setHeight(240, Unit.PIXELS);
		formLayout.addComponent(layout);
		

		tabsheet.addTab(h1, "Event Payload");
		tabsheet.addTab(h2, "Error Details");
		
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setMargin(true);
		wrapperLayout.setWidth("100%");
		wrapperLayout.addComponent(formLayout);
		wrapperLayout.addComponent(tabsheet);

		exclusionEventDetailsPanel.setContent(wrapperLayout);
		return exclusionEventDetailsPanel;
	}
}
