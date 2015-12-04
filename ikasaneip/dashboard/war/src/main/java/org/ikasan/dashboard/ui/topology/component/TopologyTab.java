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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.util.FilterMap;
import org.ikasan.dashboard.ui.topology.util.FilterUtil;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public abstract class TopologyTab extends VerticalLayout
{
	private Logger logger = Logger.getLogger(TopologyTab.class);
	
	protected Table modules = new Table("Modules");
	protected Table flows = new Table("Flows");
	protected Table components = new Table("Components");
	
	protected PopupDateField errorFromDate;
	protected PopupDateField errorToDate;
	
	public abstract void createLayout();
	
	public abstract void search();
	
	public void applyModuleFilter(Module module)
	{
		modules.removeAllItems();
		flows.removeAllItems();
		components.removeAllItems();
		
		errorFromDate.setValue(null);
		errorToDate.setValue(null);
		
		this.addModule(module);
		
		for(Flow flow: module.getFlows())
		{
			this.addFlow(flow);
			
			for(Component component: flow.getComponents())
			{
				this.addComponent(component);
			}
		}
	}
	
	public void resetSearchDates()
	{
		errorFromDate.setValue(this.getMidnightToday());
		errorToDate.setValue(this.getTwentyThreeFixtyNineToday());
	}
	
	public void applyFilter()
	{
		FilterMap filterMap = (FilterMap)VaadinService.getCurrentRequest().getWrappedSession()
	    		.getAttribute(DashboardSessionValueConstants.FILTERS);
		
		modules.removeAllItems();
		flows.removeAllItems();
		components.removeAllItems();
		
		if(filterMap != null)
		{
			for(FilterUtil filterUtil: filterMap.getFilters())
			{
				if(filterUtil.isSelected())
				{
					for(FilterComponent filterComponent: filterUtil.getFilter().getComponents())
					{
						this.addComponent(filterComponent.getComponent());
						this.addFlow(filterComponent.getComponent().getFlow());
						this.addModule(filterComponent.getComponent().getFlow().getModule());
					}
				}
			}
		}
	}
	
	protected void initialiseFilterTables()
	{
		modules.setIcon(VaadinIcons.ARCHIVE);
		modules.addContainerProperty("Module Name", String.class,  null);
		modules.addContainerProperty("", Button.class,  null);
		modules.setSizeFull();
		modules.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		modules.setDragMode(TableDragMode.ROW);
		modules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.debug("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ module.getName());
					
					addModule(module);

					for(final Flow flow: module.getFlows())
					{				
						addFlow(flow);
						
						for(final Component component: flow.getComponents())
						{							
							addComponent(component);
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
				
		flows.setIcon(VaadinIcons.AUTOMATION);
		flows.addContainerProperty("Flow Name", String.class,  null);
		flows.addContainerProperty("", Button.class,  null);
		flows.setSizeFull();
		flows.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		flows.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
										
					addFlow(flow);
						
					for(final Component component: flow.getComponents())
					{						
						addComponent(component);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		components.setIcon(VaadinIcons.COG);
		components.setSizeFull();
		components.addContainerProperty("Component Name", String.class,  null);
		components.addContainerProperty("", Button.class,  null);
		components.setCellStyleGenerator(new IkasanCellStyleGenerator());
		components.setSizeFull();
		components.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		components.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Component)
				{
					final Component component = (Component) t
							.getItemId();
					
					addComponent(component);		
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
	}
	
	protected void addModule(final Module module)
	{
		Button deleteButton = new Button();
		deleteButton.setIcon(VaadinIcons.TRASH);
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		
		// Add the delete functionality to each role that is added
		deleteButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {		
            	modules.removeItem(module);
            	
            	for(Flow flow: module.getFlows())
            	{
            		flows.removeItem(flow);
            		
            		for(Component component: flow.getComponents())
            		{
            			components.removeItem(component);
            		}
            	}
            }
        });
		
		modules.addItem(new Object[]{module.getName(), deleteButton}, module);
	}
	
	protected void addFlow(final Flow flow)
	{
		Button deleteButton = new Button();
		deleteButton.setIcon(VaadinIcons.TRASH);
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		// Add the delete functionality to each role that is added
		deleteButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {		
            	flows.removeItem(flow);
            	
            	for(Component component: flow.getComponents())
        		{
        			components.removeItem(component);
        		}
            }
        });
		
		flows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
	}
	
	protected void addComponent(final Component component)
	{
		Button deleteButton = new Button();
		deleteButton.setIcon(VaadinIcons.TRASH);
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		
		// Add the delete functionality to each role that is added
		deleteButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {		
            	components.removeItem(component);
            }
        });
		
		components.addItem(new Object[]{component.getName(), deleteButton}, component);
	}
	
	protected Date getMidnightToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
	
	protected Date getTwentyThreeFixtyNineToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
}
