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

import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class CategorisedErrorOccurrenceViewWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private CategorisedErrorOccurrence categorisedErrorOccurrence;
	

	/**
	 * @param policy
	 */
	public CategorisedErrorOccurrenceViewWindow(CategorisedErrorOccurrence errorOccurrence)
	{
		super();
		this.categorisedErrorOccurrence = errorOccurrence;
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		this.setHeight("90%");
		this.setWidth("90%");
		
		GridLayout layout = new GridLayout(1, 1);
		layout.setWidth("100%");

		layout.addComponent(createErrorOccurrenceDetailsPanel(), 0, 0);
		
		this.setContent(layout);
	}

	protected Panel createErrorOccurrenceDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		
		GridLayout layout = new GridLayout(4, 7);
		layout.setWidth("100%");
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, .10f);
		layout.setColumnExpandRatio(1, .30f);
		layout.setColumnExpandRatio(2, .05f);
		layout.setColumnExpandRatio(3, .30f);
		
		Label errorOccurrenceDetailsLabel = new Label(" Categorised Error Occurence Details", ContentMode.HTML);
		Label errorCategoryLabel = new Label();
		
		if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.BLOCKER))
	    {
			errorOccurrenceDetailsLabel = new Label(VaadinIcons.BAN.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
			errorCategoryLabel = new Label(VaadinIcons.BAN.getHtml() + " Blocker", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.CRITICAL))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.EXCLAMATION.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.EXCLAMATION.getHtml() + " Critical", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.MAJOR))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.ARROW_UP.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.ARROW_UP.getHtml() + " Major", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.ARROW_DOWN.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.ARROW_DOWN.getHtml() + " Trivial", ContentMode.HTML);
	    }
		
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(errorOccurrenceDetailsLabel, 0, 0, 3, 0);
		
		Label label = new Label("Module Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Component Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getFlowElementName());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf4 = new TextField();
		tf4.setValue(new Date(this.categorisedErrorOccurrence.getErrorOccurrence().getTimestamp()).toString());
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setMargin(true);
		wrapperLayout.setWidth("100%");
		
		label = new Label("Error Category:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		layout.addComponent(errorCategoryLabel, 3, 1);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_LEFT);
		
		label = new Label("System Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField systemAction = new TextField();
		systemAction.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getAction());
		systemAction.setReadOnly(true);
		systemAction.setWidth("80%");
		layout.addComponent(systemAction, 3, 2);
		
		label = new Label("User Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField userAction = new TextField();
		userAction.setValue("");
		userAction.setReadOnly(true);
		userAction.setWidth("80%");
		layout.addComponent(userAction, 3, 3);
		
		label = new Label("User Action By:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField userActionBy = new TextField();
		userActionBy.setValue("");
		userActionBy.setReadOnly(true);
		userActionBy.setWidth("80%");
		layout.addComponent(userActionBy, 3, 4);
		
		
		AceEditor errorMessageEditor = new AceEditor();
		errorMessageEditor.setValue(this.categorisedErrorOccurrence.getErrorCategorisation().getErrorDescription()
				 + this.categorisedErrorOccurrence.getErrorOccurrence().getErrorMessage());
		errorMessageEditor.setReadOnly(true);
		errorMessageEditor.setMode(AceMode.xml);
		errorMessageEditor.setTheme(AceTheme.textmate);
		errorMessageEditor.setHeight(500, Unit.PIXELS);
		errorMessageEditor.setWidth("100%");
		errorMessageEditor.setWordWrap(true);
		
		AceEditor errorDetailEditor = new AceEditor();
		errorDetailEditor.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getErrorDetail());
		errorDetailEditor.setReadOnly(true);
		errorDetailEditor.setMode(AceMode.xml);
		errorDetailEditor.setTheme(AceTheme.eclipse);
		errorDetailEditor.setHeight(500, Unit.PIXELS);
		errorDetailEditor.setWidth("100%");

		
		final AceEditor eventEditor = new AceEditor();
		
		if(this.categorisedErrorOccurrence.getErrorOccurrence().getEvent() != null)
		{
			eventEditor.setValue(new String((byte[])this.categorisedErrorOccurrence.getErrorOccurrence().getEvent()));
		}
		
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setHeight(500, Unit.PIXELS);
		eventEditor.setWidth("100%");
		
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
		
		wrapTextCheckBox.setValue(true);

		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.setWidth("100%");
		formLayout.setHeight(200, Unit.PIXELS);
		formLayout.addComponent(layout);
		wrapperLayout.addComponent(formLayout, 0, 0);
				
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
		h2.addComponent(errorDetailEditor);
		
		HorizontalLayout h3 = new HorizontalLayout();
		h3.setSizeFull();
		h3.setMargin(true);
		h3.addComponent(errorMessageEditor);
		
		tabsheet.addTab(h3, "Error Message");
		tabsheet.addTab(h2, "Error Details");
		tabsheet.addTab(h1, "Message Data");
		
		wrapperLayout.addComponent(tabsheet, 0, 1);

		errorOccurrenceDetailsPanel.setContent(wrapperLayout);
		return errorOccurrenceDetailsPanel;
	}
}
