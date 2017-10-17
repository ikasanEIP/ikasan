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
import java.util.Date;
import java.util.List;

import com.vaadin.ui.*;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.spec.error.reporting.ErrorOccurrence;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ActionedErrorOccurrenceViewPanel extends Panel
{
	private static final long serialVersionUID = -3347325521531925322L;
	
	private ErrorOccurrence errorOccurrence;
	private ErrorReportingManagementService errorReportingManagementService;


	/**
	 * Constructor
	 *
	 * @param errorOccurrence
	 * @param errorReportingManagementService
     */
	public ActionedErrorOccurrenceViewPanel(ErrorOccurrence errorOccurrence,
			ErrorReportingManagementService errorReportingManagementService)
	{
		super();
		this.errorOccurrence = errorOccurrence;
		this.errorReportingManagementService = errorReportingManagementService;
		
		this.init();
	}

	/**
	 * Constructor
	 *
	 * @param errorUri
	 * @param errorReportingManagementService
	 * @param errorReportingService
     */
	public ActionedErrorOccurrenceViewPanel(String errorUri,
			ErrorReportingManagementService errorReportingManagementService,
			ErrorReportingService errorReportingService)
	{
		super();
		
		this.errorReportingManagementService = errorReportingManagementService;
		this.errorOccurrence = (ErrorOccurrence)errorReportingService.find(errorUri);
		
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
		
		GridLayout layout = new GridLayout(4, 6);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, 0.05f);
		layout.setColumnExpandRatio(1, 0.45f);
		layout.setColumnExpandRatio(2, 0.05f);
		layout.setColumnExpandRatio(3, 0.45f);
		
		Label errorOccurrenceDetailsLabel = new Label("Actioned Error Details");
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(errorOccurrenceDetailsLabel, 0, 0, 3, 0);
		
		Label label = new Label("Module Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.errorOccurrence.getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("100%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.errorOccurrence.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("100%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Component Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.errorOccurrence.getFlowElementName());
		tf3.setReadOnly(true);
		tf3.setWidth("100%");
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
		
		label = new Label("Error Message:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 5);
		layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
		
		TextArea tf5 = new TextArea();
		tf5.setValue(this.errorOccurrence.getErrorMessage());
		tf5.setReadOnly(true);
		tf5.setWidth("95%");
		tf5.setRows(4);
		tf5.setNullRepresentation("");
		layout.addComponent(tf5, 1, 5, 3, 5);
		
		label = new Label("Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField actionTf = new TextField();
		actionTf.setValue(this.errorOccurrence.getUserAction());
		actionTf.setReadOnly(true);
		actionTf.setWidth("80%");
		layout.addComponent(actionTf, 3, 1);
		
		label = new Label("Action Date/Time:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		date = new Date(errorOccurrence.getUserActionTimestamp());
		timestamp = format.format(date);
	    
		TextField actionTimeTf = new TextField();
		actionTimeTf.setValue(timestamp);
		actionTimeTf.setReadOnly(true);
		actionTimeTf.setWidth("80%");
		layout.addComponent(actionTimeTf, 3, 2);
		
		label = new Label("Action By:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField actionByTf = new TextField();
		actionByTf.setValue(this.errorOccurrence.getActionedBy());
		actionByTf.setReadOnly(true);
		actionByTf.setWidth("80%");
		layout.addComponent(actionByTf, 3, 3);
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setMargin(true);
		wrapperLayout.setWidth("100%");
		
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		AceEditor editor = new AceEditor();
		editor.setValue(this.errorOccurrence.getErrorDetail());
		editor.setReadOnly(true);
		editor.setMode(AceMode.xml);
		editor.setTheme(AceTheme.eclipse);
		editor.setSizeFull();
		
		final AceEditor eventEditor = new AceEditor();
		
		if(this.errorOccurrence.getEvent() != null)
		{
			eventEditor.setValue(new String((byte[])this.errorOccurrence.getEvent()));
		}
		
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setSizeFull();
		
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
		formLayout.setHeight(300, Unit.PIXELS);
		formLayout.addComponent(layout);

		VerticalLayout checkBoxLayout2 = new VerticalLayout();
		checkBoxLayout2.setSizeFull();
		checkBoxLayout2.setSpacing(true);
		checkBoxLayout2.addComponent(wrapTextCheckBox);
		checkBoxLayout2.setComponentAlignment(wrapTextCheckBox, Alignment.MIDDLE_LEFT);


		VerticalSplitPanel eventVpanel = new VerticalSplitPanel(checkBoxLayout2
				, eventEditor);
		eventVpanel.setSizeFull();
		eventVpanel.setSplitPosition(40, Unit.PIXELS);
		eventVpanel.setLocked(true);


		VerticalSplitPanel errorVpanel = new VerticalSplitPanel(new VerticalLayout()
				, editor);
		errorVpanel.setSizeFull();
		errorVpanel.setSplitPosition(0, Unit.PIXELS);
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
	
	protected Layout createCommentsTabsheet()
	{
		List<ErrorOccurrenceNote> notes = errorReportingManagementService.getErrorOccurrenceNotesByErrorUri(this.errorOccurrence.getUri());
		
		GridLayout layout = new GridLayout();
		layout.setWidth("100%");
		
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
