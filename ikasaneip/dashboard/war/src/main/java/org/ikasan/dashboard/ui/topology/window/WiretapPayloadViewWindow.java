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

import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapPayloadViewWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private WiretapEvent<String> wiretapEvent;
	

	/**
	 * @param policy
	 */
	public WiretapPayloadViewWindow(WiretapEvent<String> wiretapEvent)
	{
		super();
		this.wiretapEvent = wiretapEvent;
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		this.setHeight("90%");
		this.setWidth("90%");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		
		layout.addComponent( createWiretapDetailsPanel());
		
		
		this.setContent(layout);
	}

	protected Panel createWiretapDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		errorOccurrenceDetailsPanel.setSizeFull();
		errorOccurrenceDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(2, 6);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, 0.2f);
		layout.setColumnExpandRatio(1, 0.8f);
		
		Label wiretapDetailsLabel = new Label("Wiretap Details");
		wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(wiretapDetailsLabel);
		
		
		Label moduleNameLabel = new Label("Module Name:");
		moduleNameLabel.setSizeUndefined();
		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleName = new TextField();
		moduleName.setValue(this.wiretapEvent.getModuleName());
		moduleName.setReadOnly(true);
		moduleName.setWidth("80%");
		layout.addComponent(moduleName, 1, 1);
		
		Label flowNameLabel = new Label("Flow Name:");
		flowNameLabel.setSizeUndefined();
		
		layout.addComponent(flowNameLabel, 0, 2);
		layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.wiretapEvent.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		Label componentNameLabel = new Label("Component Name:");
		componentNameLabel.setSizeUndefined();
		
		layout.addComponent(componentNameLabel, 0, 3);
		layout.setComponentAlignment(componentNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.wiretapEvent.getComponentName());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		Label dateTimeLabel = new Label("Date/Time:");
		dateTimeLabel.setSizeUndefined();
		
		layout.addComponent(dateTimeLabel, 0, 4);
		layout.setComponentAlignment(dateTimeLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf4 = new TextField();
		tf4.setValue(new Date(this.wiretapEvent.getTimestamp()).toString());
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		
		Label eventIdLabel = new Label("Event Id:");
		eventIdLabel.setSizeUndefined();
		
		layout.addComponent(eventIdLabel, 0, 5);
		layout.setComponentAlignment(eventIdLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf5 = new TextField();
		tf5.setValue(((WiretapFlowEvent)wiretapEvent).getEventId());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 5);
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setWidth("100%");
		
		AceEditor editor = new AceEditor();
		editor.setCaption("Event");
		editor.setValue(this.wiretapEvent.getEvent());
		editor.setReadOnly(true);
		editor.setMode(AceMode.xml);
		editor.setWordWrap(true);
		editor.setTheme(AceTheme.eclipse);
		editor.setWidth("100%");
		editor.setHeight(550, Unit.PIXELS);
		

		wrapperLayout.addComponent(layout, 0, 0);
		wrapperLayout.addComponent(editor, 0, 2);
		wrapperLayout.setComponentAlignment(editor, Alignment.TOP_LEFT);

		errorOccurrenceDetailsPanel.setContent(wrapperLayout);
		return errorOccurrenceDetailsPanel;
	}
}
