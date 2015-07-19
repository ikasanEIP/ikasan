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
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceViewWindow;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
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
public class CategorisedErrorTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(CategorisedErrorTab.class);
	
	private Table categorizedErrorOccurenceTable;
	
	private Table errorOccurenceModules = new Table("Modules");
	private Table errorOccurenceFlows = new Table("Flows");
	private Table errorOccurenceComponents = new Table("Components");
	
	private PopupDateField errorFromDate;
	private PopupDateField errorToDate;
	
	private ComboBox errorCategoryCombo;
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ErrorCategorisationService errorCategorisationService;
	
	public CategorisedErrorTab(ErrorCategorisationService errorCategorisationService,
			ComboBox businessStreamCombo)
	{
		this.errorCategorisationService = errorCategorisationService;
		this.businessStreamCombo = businessStreamCombo;
	}
	
	public Layout createCategorisedErrorLayout()
	{
		this.categorizedErrorOccurenceTable = new Table();
		this.categorizedErrorOccurenceTable.setSizeFull();
		this.categorizedErrorOccurenceTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.categorizedErrorOccurenceTable.addStyleName("ikasan");
		this.categorizedErrorOccurenceTable.addContainerProperty("Category", Label.class,  null);
		this.categorizedErrorOccurenceTable.addContainerProperty("Module Name", String.class,  null);
		this.categorizedErrorOccurenceTable.addContainerProperty("Flow Name", String.class,  null);
		this.categorizedErrorOccurenceTable.addContainerProperty("Component Name", String.class,  null);
		this.categorizedErrorOccurenceTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.categorizedErrorOccurenceTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) {
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)itemClickEvent.getItemId();
		    	ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence);
		    
		    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
		    }
		});
		
		this.categorizedErrorOccurenceTable.setCellStyleGenerator(new Table.CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				CategorisedErrorOccurrence categorisedErrorOccurrence = (CategorisedErrorOccurrence)itemId;
				
				if (propertyId == null) {
				// Styling for row			
					
					if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
					{
						return "ikasan-green";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.MAJOR))
					{
						return "ikasan-green";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
					{
						return "ikasan-orange";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
					{
						return "ikasan-red";
					}
				}
				
				if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
				{
					return "ikasan-green";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.MAJOR))
				{
					return "ikasan-green";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
				{
					return "ikasan-orange";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
				{
					return "ikasan-red";
				}
				
				return "ikasan";
			}
			});
				
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	categorizedErrorOccurenceTable.removeAllItems();

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
            	
            	String errorCategory = null;
            	
            	if(errorCategoryCombo != null && errorCategoryCombo.getValue() != null)
            	{
            		errorCategory = (String)errorCategoryCombo.getValue();
            	}
         
            	List<CategorisedErrorOccurrence> categorisedErrorOccurences = errorCategorisationService
            			.findCategorisedErrorOccurences(modulesNames, flowNames, componentNames, errorCategory,
            					errorFromDate.getValue(), errorToDate.getValue());

            	for(CategorisedErrorOccurrence categorisedErrorOccurrence: categorisedErrorOccurences)
            	{
            		ErrorOccurrence errorOccurrence = categorisedErrorOccurrence.getErrorOccurrence();
            		
            		Date date = new Date(errorOccurrence.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(date);
            	    
            	    Label blockerLabel = new Label(VaadinIcons.BAN.getHtml() + " Blocker", ContentMode.HTML);
            	    
            	    categorizedErrorOccurenceTable.addItem(new Object[]{blockerLabel, errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
            				, errorOccurrence.getFlowElementName(), timestamp}, categorisedErrorOccurrence);
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
					Resource deleteIcon = VaadinIcons.CLOSE_CIRCLE_O;
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(ValoTheme.BUTTON_LINK);

					
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
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(ValoTheme.BUTTON_LINK);
						
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
							deleteButton.setIcon(deleteIcon);
							deleteButton.setStyleName(ValoTheme.BUTTON_LINK);
							
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
					deleteButton.setCaptionAsHtml(true);
					deleteButton.setCaption(VaadinIcons.CLOSE_CIRCLE_O.getHtml());
					deleteButton.setStyleName(ValoTheme.BUTTON_LINK);

					
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
						deleteButton.setCaptionAsHtml(true);
						deleteButton.setCaption(VaadinIcons.CLOSE_CIRCLE_O.getHtml());
						deleteButton.setStyleName(ValoTheme.BUTTON_LINK);
						
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
					Resource deleteIcon = VaadinIcons.CLOSE_CIRCLE_O;
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(ValoTheme.BUTTON_LINK);

					
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

		errorCategoryCombo = new ComboBox("Error Category");
		errorCategoryCombo.setNullSelectionAllowed(true); 
		errorCategoryCombo.addItem(ErrorCategorisation.TRIVIAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.TRIVIAL, VaadinIcons.ARROW_DOWN);
		errorCategoryCombo.addItem(ErrorCategorisation.MAJOR);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.MAJOR, VaadinIcons.ARROW_UP);
		errorCategoryCombo.addItem(ErrorCategorisation.CRITICAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.CRITICAL, VaadinIcons.EXCLAMATION_CIRCLE_O);
		errorCategoryCombo.addItem(ErrorCategorisation.BLOCKER);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.BLOCKER, VaadinIcons.BAN);
		
		GridLayout dateSelectLayout = new GridLayout(3, 1);
		dateSelectLayout.addComponent(errorCategoryCombo, 2, 0);
		dateSelectLayout.setSizeFull();
		errorFromDate = new PopupDateField("From date");
		errorFromDate.setResolution(Resolution.MINUTE);
		errorFromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(errorFromDate, 0, 0);
		errorToDate = new PopupDateField("To date");
		errorToDate.setResolution(Resolution.MINUTE);
		errorToDate.setValue(this.getTwentyThreeFixtyNineToday());
		dateSelectLayout.addComponent(errorToDate, 1, 0);
				
		
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
		hErrorTable.addComponent(this.categorizedErrorOccurenceTable);
		
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
