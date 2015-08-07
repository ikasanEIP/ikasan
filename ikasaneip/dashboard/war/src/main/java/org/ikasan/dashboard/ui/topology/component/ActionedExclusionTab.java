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
package org.ikasan.dashboard.ui.topology.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ActionedExclusionEventViewWindow;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ActionedExclusionTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ActionedExclusionTab.class);
	
	private Table actionedExclusionsTable;
	
	private Table errorOccurenceModules = new Table("Modules");
	private Table errorOccurenceFlows = new Table("Flows");
	private Table errorOccurenceComponents = new Table("Components");
	
	private PopupDateField fromDate;
	private PopupDateField toDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	private ErrorReportingService errorReportingService;
	private TopologyService topologyService;
	
	private SerialiserFactory serialiserFactory;
	
	public ActionedExclusionTab(ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			HospitalManagementService<ExclusionEventAction> hospitalManagementService,ErrorReportingService errorReportingService,
			TopologyService topologyService, SerialiserFactory serialiserFactory, ComboBox businessStreamCombo)
	{
		this.exclusionManagementService = exclusionManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.errorReportingService = errorReportingService;
		this.topologyService = topologyService;
		this.serialiserFactory = serialiserFactory;
		this.businessStreamCombo = businessStreamCombo;
	}
	
	public Layout createLayout()
	{
		this.actionedExclusionsTable = new Table();
		this.actionedExclusionsTable.setSizeFull();
		this.actionedExclusionsTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.actionedExclusionsTable.addContainerProperty("Module Name", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Flow Name", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Action", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Actioned By", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.actionedExclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	ExclusionEventAction exclusionEventAction = (ExclusionEventAction)itemClickEvent.getItemId();
		    	
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEventAction.getErrorUri());
		    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEventAction.getErrorUri());
		    	ActionedExclusionEventViewWindow actionExclusionEventViewWindow = new ActionedExclusionEventViewWindow(errorOccurrence, serialiserFactory
		    			, action, hospitalManagementService, topologyService);
		    
		    	UI.getCurrent().addWindow(actionExclusionEventViewWindow);
		    }
		});
				
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	actionedExclusionsTable.removeAllItems();

            	ArrayList<String> modulesNames = null;
            	
            	if(errorOccurenceModules.getItemIds().size() > 0)
            	{
	            	modulesNames = new ArrayList<String>();
	            	for(Object module: errorOccurenceModules.getItemIds())
	            	{
	            		modulesNames.add(((Module)module).getName());
	            	}
            	}
            	
            	ArrayList<String> flowNames = null;
            	
            	if(errorOccurenceFlows.getItemIds().size() > 0)
            	{
            		flowNames = new ArrayList<String>();
            		for(Object flow: errorOccurenceFlows.getItemIds())
                	{
                		flowNames.add(((Flow)flow).getName());
                	}
            	}
            	
            	ArrayList<String> componentNames = null;
            	
            	if(errorOccurenceComponents.getItemIds().size() > 0)
            	{
            		componentNames = new ArrayList<String>();
	            	for(Object component: errorOccurenceComponents.getItemIds())
	            	{
	            		componentNames.add(((Component)component).getName());
	            	}
            	}
            	
            	if(modulesNames == null && flowNames == null && componentNames == null
            			&& !((BusinessStream)businessStreamCombo.getValue()).getName().equals("All"))
            	{
            		BusinessStream businessStream = ((BusinessStream)businessStreamCombo.getValue());
            		
            		modulesNames = new ArrayList<String>();
            		
            		for(BusinessStreamFlow flow: businessStream.getFlows())
            		{
            			modulesNames.add(flow.getFlow().getModule().getName());
            		}
            	}
            	
         
            	List<ExclusionEventAction> exclusionEventActions = hospitalManagementService.getActionedExclusions
            			(modulesNames, flowNames, fromDate.getValue(), toDate.getValue());

            	for(ExclusionEventAction exclusionEventAction: exclusionEventActions)
            	{
            		Date date = new Date(exclusionEventAction.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(date);
            	    
            	    actionedExclusionsTable.addItem(new Object[]{exclusionEventAction.getModuleName(), exclusionEventAction.getFlowName()
            				, exclusionEventAction.getAction(), exclusionEventAction.getActionedBy(), timestamp}, exclusionEventAction);
            	}
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	errorOccurenceModules.removeAllItems();
            	errorOccurenceFlows.removeAllItems();
            	errorOccurenceComponents.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(false);
		layout.setHeight(270 , Unit.PIXELS);
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		
		errorOccurenceModules.setIcon(VaadinIcons.ARCHIVE);
		errorOccurenceModules.addContainerProperty("Module Name", String.class,  null);
		errorOccurenceModules.addContainerProperty("", Button.class,  null);
		errorOccurenceModules.setSizeFull();
		errorOccurenceModules.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		errorOccurenceModules.setDragMode(TableDragMode.ROW);
		errorOccurenceModules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ module.getName());
					
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceModules.removeItem(module);
			            }
			        });
					
					errorOccurenceModules.addItem(new Object[]{module.getName(), deleteButton}, module);

					for(final Flow flow: module.getFlows())
					{
						deleteButton = new Button();
						deleteButton.setIcon(VaadinIcons.TRASH);
						deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
						deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	errorOccurenceFlows.removeItem(flow);
				            }
				        });
						
						errorOccurenceFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
						for(final Component component: flow.getComponents())
						{
							deleteButton = new Button();
							deleteButton.setIcon(VaadinIcons.TRASH);
							deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
							deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
							
							// Add the delete functionality to each role that is added
							deleteButton.addClickListener(new Button.ClickListener() 
					        {
					            public void buttonClick(ClickEvent event) 
					            {		
					            	errorOccurenceComponents.removeItem(component);
					            }
					        });
							
							errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						}
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		listSelectLayout.addComponent(errorOccurenceModules, 0, 0);
		
		errorOccurenceFlows.setIcon(VaadinIcons.AUTOMATION);
		errorOccurenceFlows.addContainerProperty("Flow Name", String.class,  null);
		errorOccurenceFlows.addContainerProperty("", Button.class,  null);
		errorOccurenceFlows.setSizeFull();
		errorOccurenceFlows.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		errorOccurenceFlows.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ flow.getName());
					
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceFlows.removeItem(flow);
			            }
			        });
					
					errorOccurenceFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
					for(final Component component: flow.getComponents())
					{
						deleteButton = new Button();
						deleteButton.setIcon(VaadinIcons.TRASH);
						deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
						deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	errorOccurenceComponents.removeItem(component);
				            }
				        });
						
						errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		listSelectLayout.addComponent(errorOccurenceFlows, 1, 0);
		
		errorOccurenceComponents.setIcon(VaadinIcons.COG);
		errorOccurenceComponents.setSizeFull();
		errorOccurenceComponents.addContainerProperty("Component Name", String.class,  null);
		errorOccurenceComponents.addContainerProperty("", Button.class,  null);
		errorOccurenceComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		errorOccurenceComponents.setSizeFull();
		errorOccurenceComponents.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		errorOccurenceComponents.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Component)
				{
					final Component component = (Component) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ component.getName());
					
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceComponents.removeItem(component);
			            }
			        });
					
					errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		listSelectLayout.addComponent(this.errorOccurenceComponents, 2, 0);
		
		GridLayout dateSelectLayout = new GridLayout(2, 1);

		dateSelectLayout.setSizeFull();
		fromDate = new PopupDateField("From date");
		fromDate.setResolution(Resolution.MINUTE);
		fromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(fromDate, 0, 0);
		toDate = new PopupDateField("To date");
		toDate.setResolution(Resolution.MINUTE);
		toDate.setValue(this.getTwentyThreeFixtyNineToday());
		dateSelectLayout.addComponent(toDate, 1, 0);
				
		
		final VerticalSplitPanel vSplitPanel = new VerticalSplitPanel();
		vSplitPanel.setHeight("95%");
		
		GridLayout searchLayout = new GridLayout(2, 1);
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchButton, 0, 0);
		searchLayout.addComponent(clearButton, 1, 0);
		
		final Button hideFilterButton = new Button();
		hideFilterButton.setIcon(VaadinIcons.MINUS);
		hideFilterButton.setCaption("Hide Filter");
		hideFilterButton.setStyleName(ValoTheme.BUTTON_LINK);
		hideFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		final Button showFilterButton = new Button();
		showFilterButton.setIcon(VaadinIcons.PLUS);
		showFilterButton.setCaption("Show Filter");
		showFilterButton.addStyleName(ValoTheme.BUTTON_LINK);
		showFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		showFilterButton.setVisible(false);

		final HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(150 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
		
		final HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(40, Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		
		final HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		hSearchLayout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);
		
		hideFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(false);
            	showFilterButton.setVisible(true);
            	splitPosition = vSplitPanel.getSplitPosition();
            	splitUnit = vSplitPanel.getSplitPositionUnit();
            	vSplitPanel.setSplitPosition(0, Unit.PIXELS);
            }
        });

		
		showFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(true);
            	showFilterButton.setVisible(false);
            	vSplitPanel.setSplitPosition(splitPosition, splitUnit);
            }
        });
		
		GridLayout filterButtonLayout = new GridLayout(2, 1);
		filterButtonLayout.setHeight(25, Unit.PIXELS);
		filterButtonLayout.addComponent(hideFilterButton, 0, 0);
		filterButtonLayout.addComponent(showFilterButton, 1, 0);
		
		Label filterHintLabel = new Label();
		filterHintLabel.setCaptionAsHtml(true);
		filterHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag items from the topology tree to the tables below in order to narrow your search.");
		filterHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		filterHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		layout.addComponent(filterHintLabel);
		layout.addComponent(hListSelectLayout);
		layout.addComponent(hDateSelectLayout);
		layout.addComponent(hSearchLayout);
		layout.setSizeFull();
		
		Panel filterPanel = new Panel();
		filterPanel.setHeight(300, Unit.PIXELS);
		filterPanel.setWidth("100%");
		filterPanel.setContent(layout);
		filterPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		vSplitPanel.setFirstComponent(filterPanel);
		
		CssLayout hErrorTable = new CssLayout();
		hErrorTable.setSizeFull();
		hErrorTable.addComponent(this.actionedExclusionsTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		
		GridLayout wrapper = new GridLayout(1, 2);
		wrapper.setRowExpandRatio(0, .01f);
		wrapper.setRowExpandRatio(1, .99f);
		wrapper.setSizeFull();
		wrapper.addComponent(filterButtonLayout);
		wrapper.setComponentAlignment(filterButtonLayout, Alignment.MIDDLE_RIGHT);
		wrapper.addComponent(vSplitPanel);
		
		return wrapper;
	}

}
