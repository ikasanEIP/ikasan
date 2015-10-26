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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.dashboard.ui.framework.validator.UrlStringValidator;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class CategorisedErrorOccurrenceCloseWindow extends Window
{
	public static final String CLOSE = "close";
	public static final String CANCEL = "cancel";
	
	private static final long serialVersionUID = -3347325521531925322L;
	
	private Collection<CategorisedErrorOccurrence> errorOccurrences;
	
	private Table errorOccurenceTable;
	
	private ErrorReportingManagementService errorReportingManagementService;
	
	private String action;
	

	/**
	 * @param policy
	 */
	public CategorisedErrorOccurrenceCloseWindow(ErrorReportingManagementService errorReportingManagementService,
			Collection<CategorisedErrorOccurrence> errorOccurrences)
	{
		super();
		this.errorOccurrences = errorOccurrences;
		
		this.errorReportingManagementService = errorReportingManagementService;
		if(this.errorReportingManagementService == null)
		{
			throw new IllegalArgumentException("errorReportingManagementService cannot be null!");
		}
		
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

	protected Container buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Component Name", String.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);

        return cont;
    }

	protected Panel createErrorOccurrenceDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		
		final Container cont = buildContainer();
		this.errorOccurenceTable = new Table();

		this.errorOccurenceTable.setWidth("100%");
		this.errorOccurenceTable.setHeight("550px");
		this.errorOccurenceTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.errorOccurenceTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.errorOccurenceTable.addStyleName("ikasan");
		this.errorOccurenceTable.setContainerDataSource(cont);
		this.errorOccurenceTable.setColumnExpandRatio("Module Name", .14f);
		this.errorOccurenceTable.setColumnExpandRatio("Flow Name", .18f);
		this.errorOccurenceTable.setColumnExpandRatio("Component Name", .2f);
		this.errorOccurenceTable.setColumnExpandRatio("Error Message", .33f);
		this.errorOccurenceTable.setColumnExpandRatio("Timestamp", .1f);
		
		this.errorOccurenceTable.addStyleName("wordwrap-table");
		
		GridLayout layout = new GridLayout(2, 6);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setColumnExpandRatio(0, 0.25f);
		layout.setColumnExpandRatio(1, 0.75f);
		
		Label errorOccurrenceDetailsLabel = new Label("Close Errors");
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(errorOccurrenceDetailsLabel);
		
		
		Label errorCloseHintLabel = new Label();
		errorCloseHintLabel.setCaptionAsHtml(true);
		errorCloseHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Closing errors removes them from the errors view and they will no longer be reported. " +
				"It is mandatory to enter a comment.");
		
		errorCloseHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		errorCloseHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		layout.addComponent(errorCloseHintLabel, 0, 1, 1, 1);		
		
		Label label = new Label("Comments:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
		
		final TextArea tf1 = new TextArea();
		tf1.addValidator(new NonZeroLengthStringValidator("You must enter a comment!"));
		tf1.setRows(5);
		tf1.setReadOnly(false);
		tf1.setWidth("80%");
		tf1.setValidationVisible(false);
		layout.addComponent(tf1, 1, 2);
		
		label = new Label("Link:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf2 = new TextField();
		tf2.setReadOnly(false);
		tf2.setWidth("80%");
		tf2.addValidator(new UrlStringValidator("Link must be a valid URL!"));
		tf2.setValidationVisible(false);
		layout.addComponent(tf2, 1, 3);
		
		final Button closeButton = new Button("Close");
		closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
		closeButton.setImmediate(true);
		closeButton.setDescription("Close below errors.");
		
		closeButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	try
            	{
            		tf1.validate();
            		tf2.validate();
            	}
            	catch (InvalidValueException e)
            	{
            		tf1.setValidationVisible(true);
            		tf2.setValidationVisible(true);
            		return;
            	}
            	
            
            	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
			        	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	Collection<CategorisedErrorOccurrence> items = (Collection<CategorisedErrorOccurrence>)cont.getItemIds();
            	ArrayList<String> uris = new ArrayList<String>();
            	
            	for(CategorisedErrorOccurrence eo: items)
            	{
            		uris.add(eo.getErrorOccurrence().getUri());
            	}
            	
            	errorReportingManagementService.close(uris, tf1.getValue(),authentication.getName());
            	action = CLOSE;
            	close();
            }
        });
		
		final Button cancelButton = new Button("Cancel");
		cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
		cancelButton.setImmediate(true);
		cancelButton.setDescription("Close this window.");
		
		cancelButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	action = CANCEL;
            	close();
            }
        });
		
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(closeButton);
		buttonsLayout.addComponent(cancelButton);
		layout.addComponent(buttonsLayout, 0, 4, 1, 4);
		layout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER);
		
		for(CategorisedErrorOccurrence errorOccurrence: errorOccurrences)
    	{
    		Date date = new Date(errorOccurrence.getErrorOccurrence().getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT);
    	    String timestamp = format.format(date);
    	    
    	    Item item = cont.addItem(errorOccurrence);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(errorOccurrence.getErrorOccurrence().getModuleName());
			item.getItemProperty("Flow Name").setValue(errorOccurrence.getErrorOccurrence().getFlowName());
			item.getItemProperty("Component Name").setValue(errorOccurrence.getErrorOccurrence().getFlowElementName());
			item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorOccurrence().getErrorMessage());
			item.getItemProperty("Timestamp").setValue(timestamp);    	    
    	}
		
		layout.addComponent(errorOccurenceTable, 0, 5, 1, 5);

		errorOccurrenceDetailsPanel.setContent(layout);
		return errorOccurrenceDetailsPanel;
	}


	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}
}
