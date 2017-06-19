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

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.*;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceViewPanel extends Panel
{
	private static final long serialVersionUID = -3347325521531925322L;
	
	private ErrorOccurrence errorOccurrence;
	private ErrorReportingManagementService errorReportingManagementService;
	private PlatformConfigurationService platformConfigurationService;

	/**
	 * Constructor
	 *
	 * @param errorOccurrence
	 * @param errorReportingManagementService
	 * @param platformConfigurationService
     */
	public ErrorOccurrenceViewPanel(ErrorOccurrence errorOccurrence,
			ErrorReportingManagementService errorReportingManagementService,
			PlatformConfigurationService platformConfigurationService)
	{
		super();
		this.errorOccurrence = errorOccurrence;
		this.errorReportingManagementService = errorReportingManagementService;
		this.platformConfigurationService = platformConfigurationService;
		
		this.init();
	}

	public void init()
	{
		this.setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);

		layout.addComponent(createErrorOccurrenceDetailsPanel());


		this.setContent(layout);
	}

	protected Panel createErrorOccurrenceDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		errorOccurrenceDetailsPanel.setSizeFull();
		errorOccurrenceDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(4, 8);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, 0.10f);
		layout.setColumnExpandRatio(1, 0.40f);
		layout.setColumnExpandRatio(2, 0.10f);
		layout.setColumnExpandRatio(3, 0.40f);
		
		Label errorOccurrenceDetailsLabel = new Label("Error Details");
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(errorOccurrenceDetailsLabel);
		
		Label label = new Label("Module Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.errorOccurrence.getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.errorOccurrence.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Component Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.errorOccurrence.getFlowElementName());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		Date date = new Date(errorOccurrence.getTimestamp());
		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
	    String timestamp = format.format(date);
	    
		TextField tf4 = new TextField();
		tf4.setValue(timestamp);
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		label = new Label("URI:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
	    
		TextField uriTf = new TextField();
		uriTf.setValue(this.errorOccurrence.getUri());
		uriTf.setReadOnly(true);
		uriTf.setWidth("80%");
		layout.addComponent(uriTf, 3, 1);
		
		label = new Label("Event Life Identifier:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
	    
		TextField lidTf = new TextField();
		lidTf.setValue((this.errorOccurrence.getEventLifeIdentifier() == null ) ? "" : this.errorOccurrence.getEventLifeIdentifier());
		lidTf.setReadOnly(true);
		lidTf.setWidth("80%");
		layout.addComponent(lidTf, 3, 2);
		
		label = new Label("Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
	    
		TextField aTf = new TextField();
		aTf.setValue((this.errorOccurrence.getAction() == null ) ? "" : this.errorOccurrence.getAction());
		aTf.setReadOnly(true);
		aTf.setWidth("80%");
		layout.addComponent(aTf, 3, 3);
		
		label = new Label("Error Url:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 5);
		layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
		
		TextField ecrTf = new TextField();
		ecrTf.setValue(buildErrorUrl());
		ecrTf.setReadOnly(true);
		ecrTf.setWidth("95%");
		ecrTf.setNullRepresentation("");
		layout.addComponent(ecrTf, 1, 5, 3, 5);
		
		label = new Label("Exception Class:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 6);
		layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
		
		TextField ecTf = new TextField();
		ecTf.setValue(this.errorOccurrence.getExceptionClass());
		ecTf.setReadOnly(true);
		ecTf.setWidth("95%");
		ecTf.setNullRepresentation("");
		layout.addComponent(ecTf, 1, 6, 3, 6);
		
		label = new Label("Error Message:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 7);
		layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
		
		TextArea tf5 = new TextArea();
		tf5.setValue(this.errorOccurrence.getErrorMessage());
		tf5.setReadOnly(true);
		tf5.setWidth("95%");
		tf5.setRows(3);
		tf5.setNullRepresentation("");
		layout.addComponent(tf5, 1, 7, 3, 7);
		
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		final AceEditor editor = new AceEditor();
		editor.setValue(this.errorOccurrence.getErrorDetail());
		editor.setReadOnly(true);
		editor.setMode(AceMode.xml);
		editor.setTheme(AceTheme.eclipse);
		editor.setSizeFull();

		CheckBox wrapTextCheckBox = new CheckBox("Wrap text");
		wrapTextCheckBox.addValueChangeListener(new Property.ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				Object value = event.getProperty().getValue();
				boolean isCheck = (null == value) ? false : (Boolean) value;

				editor.setWordWrap(isCheck);
			}
		});


		
		final AceEditor eventEditor = new AceEditor();
		
		if(this.errorOccurrence.getEvent() != null)
		{
			if(this.errorOccurrence.getEventAsString() != null 
					&& this.errorOccurrence.getEventAsString().length() > 0)
			{
				eventEditor.setValue(errorOccurrence.getEventAsString());
			}
			else
			{
				eventEditor.setValue(new String((byte[])this.errorOccurrence.getEvent()));
			}
		}

		CheckBox eventWrapTextCheckBox = new CheckBox("Wrap text");
		eventWrapTextCheckBox.addValueChangeListener(new Property.ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				Object value = event.getProperty().getValue();
				boolean isCheck = (null == value) ? false : (Boolean) value;

				eventEditor.setWordWrap(isCheck);
			}
		});


		eventWrapTextCheckBox.setValue(true);
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setSizeFull();
		

		
		wrapTextCheckBox.setValue(true);

		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.setWidth("100%");
		formLayout.setHeight(320, Unit.PIXELS);
		formLayout.addComponent(layout);


		VerticalLayout checkBoxLayout = new VerticalLayout();
		checkBoxLayout.setSizeFull();
		checkBoxLayout.setSpacing(true);
		checkBoxLayout.addComponent(eventWrapTextCheckBox);
		checkBoxLayout.setComponentAlignment(eventWrapTextCheckBox, Alignment.MIDDLE_LEFT);

		VerticalSplitPanel eventVpanel = new VerticalSplitPanel(checkBoxLayout
				, eventEditor);
		eventVpanel.setSizeFull();
		eventVpanel.setSplitPosition(40, Unit.PIXELS);
		eventVpanel.setLocked(true);

		VerticalLayout checkBoxLayout2 = new VerticalLayout();
		checkBoxLayout2.setSizeFull();
		checkBoxLayout2.setSpacing(true);
		checkBoxLayout2.addComponent(wrapTextCheckBox);
		checkBoxLayout2.setComponentAlignment(wrapTextCheckBox, Alignment.MIDDLE_LEFT);

		VerticalSplitPanel errorVpanel = new VerticalSplitPanel(checkBoxLayout2
				, editor);
		errorVpanel.setSizeFull();
		errorVpanel.setSplitPosition(40, Unit.PIXELS);
		errorVpanel.setLocked(true);
		
		tabsheet.addTab(errorVpanel, "Error Details");
		tabsheet.addTab(eventVpanel, "Event Payload");
		tabsheet.addTab(createCommentsTabsheet(), "Notes / Links");

		VerticalSplitPanel wrapperVpanel = new VerticalSplitPanel(formLayout
				, tabsheet);
		wrapperVpanel.setSizeFull();
		wrapperVpanel.setSplitPosition(320, Unit.PIXELS);
		wrapperVpanel.setLocked(true);


		errorOccurrenceDetailsPanel.setContent(wrapperVpanel);
		return errorOccurrenceDetailsPanel;
	}
	
	protected String buildErrorUrl()
	{
		String dashboardBaseUrl = this.platformConfigurationService.getConfigurationValue("dashboardBaseUrl");
		
		if(dashboardBaseUrl == null || dashboardBaseUrl.length() == 0)
		{
			Notification.show("A value for dashboardBaseUrl has not been configured in the Platform Configuration. " +
					"Cannot display an error URL for this error!", Type.WARNING_MESSAGE);
			return "";
		}
		
		StringBuffer dashboardUrl = new StringBuffer(dashboardBaseUrl);
		
		dashboardUrl.append("/?errorUri=").append(errorOccurrence.getUri()).append("&ui=errorOccurrence");
		
		return dashboardUrl.toString();
	}
	
	protected Layout createCommentsTabsheet()
	{
		List<ErrorOccurrenceNote> notes = errorReportingManagementService.getErrorOccurrenceNotesByErrorUri(this.errorOccurrence.getUri());
		
		final GridLayout layout = new GridLayout();
		layout.setSpacing(true);
		layout.setWidth("100%");
		
		final Button commentButton = new Button("Comment");
		commentButton.addStyleName(ValoTheme.BUTTON_SMALL);
		commentButton.setImmediate(true);
		commentButton.setDescription("Comment on the error.");
		
		final HorizontalLayout commentButtonLayout = new HorizontalLayout();
		commentButtonLayout.setSpacing(true);
		commentButtonLayout.setMargin(true);
		commentButtonLayout.addComponent(commentButton);
		
		commentButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	final TextArea tf1 = new TextArea();
        		tf1.addValidator(new NonZeroLengthStringValidator("You must enter a comment!"));
        		tf1.setRows(5);
        		tf1.setReadOnly(false);
        		tf1.setWidth("100%");
        		tf1.setValidationVisible(false);
        		layout.removeAllComponents();
            	
        		final Button saveButton = new Button("Save");
        		saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        		saveButton.setImmediate(true);
        		saveButton.setDescription("Save the comment");
        		
        		saveButton.addClickListener(new Button.ClickListener() 
                {
                    public void buttonClick(ClickEvent event) 
                    {
                    	try
                    	{
                    		tf1.validate();
                    	}
                    	catch (InvalidValueException e)
                    	{
                    		tf1.setValidationVisible(true);
                    		return;
                    	}
                    	
                    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        			        	.getAttribute(DashboardSessionValueConstants.USER);
                    	
                    	ArrayList<String> uris = new ArrayList<String>();
        
                    	uris.add(errorOccurrence.getUri());
        
                    	errorReportingManagementService.update(uris, tf1.getValue(), authentication.getName());
                    	
                    	layout.removeAllComponents();                                        	
                    	updateNotes(layout);
                    	layout.addComponent(commentButton);
                    }
                });
        		
        		final Button cancelButton = new Button("Cancel");
        		cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        		cancelButton.setImmediate(true);
        		
        		cancelButton.addClickListener(new Button.ClickListener() 
                {
                    public void buttonClick(ClickEvent event) 
                    {
                    	layout.removeAllComponents();            
                    	
                    	updateNotes(layout);
                    	layout.addComponent(commentButtonLayout);
                    	layout.addComponent(commentButton);
                    }
                });
        		
        		HorizontalLayout buttonLayout = new HorizontalLayout();
        		buttonLayout.setSpacing(true);
        		
        		buttonLayout.addComponent(saveButton);
        		buttonLayout.addComponent(cancelButton);
        		
        		updateNotes(layout);
        		
        		layout.addComponent(tf1);
        		layout.addComponent(buttonLayout);
            }
        });
		
		this.updateNotes(layout);

		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);

		if(authentication.hasGrantedAuthority(SecurityConstants.ERROR_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.ERROR_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
		{
			commentButtonLayout.setVisible(true);
		}
		else
		{
			commentButtonLayout.setVisible(false);
		}

		layout.addComponent(commentButtonLayout);
		
		return layout;
	}
	
	protected Layout updateNotes(Layout layout)
	{
		List<ErrorOccurrenceNote> notes = errorReportingManagementService.getErrorOccurrenceNotesByErrorUri(this.errorOccurrence.getUri());		
		
		for(ErrorOccurrenceNote note: notes)
		{
			Label whoLabel = new Label(new Date(note.getNote().getTimestamp()) + ": " + note.getNote().getUser() + " wrote: ");
			whoLabel.setWidth("100%");
			whoLabel.setValue(new Date(note.getNote().getTimestamp()) + ": " + note.getNote().getUser() + " wrote: ");
			
			layout.addComponent(whoLabel);
			
			// Initialize Rendering components and allow getting instances
			EmbeddableComponentManager componentManager = new EmbeddableComponentManager();
			componentManager.initialize(this.getClass().getClassLoader());
			
			Converter converter;
			try
			{
				converter = componentManager.getInstance(Converter.class);
				
				// Convert input in XWiki Syntax 2.1 into XHTML. The result is stored in the printer.
				WikiPrinter printer = new DefaultWikiPrinter();
				converter.convert(new StringReader(note.getNote().getNote()), Syntax.XWIKI_2_1, Syntax.XHTML_1_0, printer);
				
				Label l = new Label(printer.toString(), ContentMode.HTML);
				l.setWidth("100%");
				
				layout.addComponent(l);
			} 
			catch (Exception e)
			{
				Notification.show("An error has occurred trying to render wiki test content: " + e.getMessage(), Type.ERROR_MESSAGE);
			} 
			
			layout.addComponent(new Label("<hr />",ContentMode.HTML));
		}
		
		
		return layout;
	}
}
