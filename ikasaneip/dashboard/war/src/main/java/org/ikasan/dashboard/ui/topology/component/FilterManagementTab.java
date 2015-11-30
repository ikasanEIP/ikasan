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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.FilterWindow;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.FilterComponentKey;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.RoleFilter;
import org.ikasan.topology.model.RoleFilterKey;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class FilterManagementTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(FilterManagementTab.class);
	
	private FilterTable filterTable;
	
	private TopologyService topologyService;
	
	private Table modules = new Table("Modules");
	private Table flows = new Table("Flows");
	private Table components = new Table("Components");

	private Container tableContainer;
	
	private Label resultsLabel = new Label();
	
	private ComboBox rolesCombo;
	private SecurityService securityService;
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private TextField nameTextField = new TextField();
	private TextArea descriptionTextArea = new TextArea();
	
	private Filter filter;
	private RoleFilter roleFilter;
	
	public FilterManagementTab(TopologyService topologyService, SecurityService securityService)
	{
		this.topologyService = topologyService;
		this.securityService = securityService;
		
		tableContainer = this.buildContainer();
	}
	
	protected Container buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Name", String.class,  null);
		cont.addContainerProperty("Description", String.class,  null);
		cont.addContainerProperty("Created By", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", CheckBox.class,  null);
		
        return cont;
    }
	
	public Layout createFilterManagementLayout()
	{	
		this.filterTable = new FilterTable();
		this.filterTable.setFilterBarVisible(true);
		this.filterTable.setSizeFull();
		this.filterTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.filterTable.addStyleName("ikasan");
		
		this.filterTable.setColumnExpandRatio("Module Name", .14f);
		this.filterTable.setColumnExpandRatio("Flow Name", .18f);
		this.filterTable.setColumnExpandRatio("Component Name", .2f);
		this.filterTable.setColumnExpandRatio("Event Id / Payload Id", .33f);
		this.filterTable.setColumnExpandRatio("Timestamp", .1f);
		this.filterTable.setColumnExpandRatio("", .05f);
		this.filterTable.setContainerDataSource(tableContainer);
		
		this.filterTable.addStyleName("wordwrap-table");
		this.filterTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		
		this.filterTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
			    	filter = (Filter)itemClickEvent.getItemId();
			    	modules.removeAllItems();
	            	flows.removeAllItems();
	            	components.removeAllItems();
	            	
	            	nameTextField.setValue(filter.getName());
                	descriptionTextArea.setValue(filter.getDescription());
                	
                	RoleFilter roleFilter = topologyService.getRoleFilterByFilterId(filter.getId());
                	
                	if(roleFilter != null)
                	{
                		for(Role role: (Collection<Role>)rolesCombo.getItemIds())
                		{
                			if(role.getId().equals(roleFilter.getId().getRoleId()))
                			{
                				rolesCombo.select(role);
                			}
                		}
                	}
                	
	            	for(FilterComponent filterComponent: filter.getComponents())
	            	{
	            		addComponent(filterComponent.getComponent());
	            		addFlow(filterComponent.getComponent().getFlow());
	            		addModule(filterComponent.getComponent().getFlow().getModule());
	            	}
		    	}
		    }
		});
	   				
		final Button saveButton = new Button("Save");
		saveButton.setStyleName(ValoTheme.BUTTON_SMALL);
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {   
            	if(filter != null)
            	{
	            	HashSet<Component> componentSet = new HashSet<Component>();
	            	
	            	if(components.getItemIds().size() > 0)
	            	{
	            		componentSet.addAll((Collection<? extends Component>)components.getItemIds());
	            	}
	            	
	            	Set<FilterComponent> filterComponents = new HashSet<FilterComponent>();
	        		
	        		for(Component component: componentSet)
	        		{
	        			FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
	        			fc.setComponent(component);
	        			
	        			filterComponents.add(fc);
	        		}
	        		
	        		filter.setName(nameTextField.getValue());
	        		filter.setDescription(descriptionTextArea.getValue());
	        		filter.setComponents(filterComponents);
	        		
	        		if(rolesCombo.getValue() != null)
	        		{
	        			Role role = (Role)rolesCombo.getValue();
	        			roleFilter = topologyService.getRoleFilterByFilterId(filter.getId());
	        			
	        			if(roleFilter != null && !role.getId().equals(roleFilter.getId().getRoleId()))
	        			{
	        				topologyService.deleteRoleFilter(roleFilter);
	        				roleFilter = new RoleFilter(new RoleFilterKey(filter.getId(), role.getId()));
	        				roleFilter.setFilter(filter);
	        			}
	        			else if(roleFilter == null)
	        			{
	        				roleFilter = new RoleFilter(new RoleFilterKey(filter.getId(), role.getId()));
	        				roleFilter.setFilter(filter);
	        			}
	        			
	        			topologyService.saveRoleFilter(roleFilter);
	        		}
	        		else
	        		{
	        			if(roleFilter != null)
	        			{
	        				topologyService.deleteRoleFilter(roleFilter);
	        				rolesCombo.setValue(null);
	        				roleFilter = null;
	        			}
	        		}
	        			
	        		
	        		topologyService.saveFilter(filter);
	        		
	        		Notification.show("Filter saved!", Type.HUMANIZED_MESSAGE);
            	}
            	else
            	{
            		Notification.show("No filter to save! Either create a new one or select one from the table below.", Type.WARNING_MESSAGE);
            	}
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	modules.removeAllItems();
            	flows.removeAllItems();
            	components.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(false);
//		layout.setHeight(270 , Unit.PIXELS);
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		
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
		
		listSelectLayout.addComponent(modules, 0, 0);
		
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

		listSelectLayout.addComponent(flows, 1, 0);
		
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
		listSelectLayout.addComponent(this.components, 2, 0);
		
		final VerticalSplitPanel vSplitPanel = new VerticalSplitPanel();
		vSplitPanel.setHeight("95%");
		
		GridLayout buttonLayout = new GridLayout(2, 1);
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(saveButton, 0, 0);
		buttonLayout.addComponent(clearButton, 1, 0);

		final HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(150 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
				
		final HorizontalLayout hButtonLayout = new HorizontalLayout();
		hButtonLayout.setHeight(30 , Unit.PIXELS);
		hButtonLayout.setWidth("100%");
		hButtonLayout.addComponent(buttonLayout);
		hButtonLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
				
		Label filterHintLabel = new Label();
		filterHintLabel.setCaptionAsHtml(true);
		filterHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag items from the topology tree to the tables below in order to narrow your search.");
		filterHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		filterHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		GridLayout controlsLayout = new GridLayout(2, 4);
		controlsLayout.setColumnExpandRatio(0, .15f);
		controlsLayout.setColumnExpandRatio(1, .85f);
		
		controlsLayout.setWidth("100%");
		controlsLayout.setSpacing(true);
		
		Label newBusinessStreamLabel = new Label("New Filter:");
		newBusinessStreamLabel.setSizeUndefined();		
		controlsLayout.addComponent(newBusinessStreamLabel, 0, 0);
		controlsLayout.setComponentAlignment(newBusinessStreamLabel, Alignment.MIDDLE_RIGHT);
		
		Button newButton = new Button();
		newButton.setIcon(VaadinIcons.PLUS);
		newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);   
		newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		newButton.setDescription("Create a new business stream.");
    	newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	HashSet<Component> componentSet = new HashSet<Component>();
            	
            	final FilterWindow filterWindow = new FilterWindow(topologyService, componentSet);
            	
            	filterWindow.addCloseListener(new Window.CloseListener() 
            	{
                    // inline close-listener
                    public void windowClose(CloseEvent e)
                    {                        
                        filter = filterWindow.getFilter();
                        
                        if(filter != null)
                        {
                        	SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
                    	    String timestamp = format.format(filter.getCreatedDateTime());
                    	    
                        	Item item = tableContainer.addItem(filter);			            	    
                    	    
                    	    item.getItemProperty("Name").setValue(filter.getName());
                			item.getItemProperty("Description").setValue(filter.getDescription());
                			item.getItemProperty("Created By").setValue(filter.getCreatedBy());
                			item.getItemProperty("Timestamp").setValue(timestamp);
                			
                        	nameTextField.setValue(filter.getName());
                        	descriptionTextArea.setValue(filter.getDescription());
                        	
                        	modules.removeAllItems();
                        	flows.removeAllItems();
                        	components.removeAllItems();
                        }
                    }
                });
            	
            	UI.getCurrent().addWindow(filterWindow);
            }
        });
    	
    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null 
    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					&& !authentication.hasGrantedAuthority(SecurityConstants.CREATE_BUSINESS_STREAM_AUTHORITY)))
    	{
			newButton.setVisible(false);
    	}
		
		controlsLayout.addComponent(newButton, 1, 0);
		
		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();		
		controlsLayout.addComponent(nameLabel, 0, 1);
		controlsLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		
		nameTextField.setWidth("80%");
		controlsLayout.addComponent(nameTextField, 1, 1);
		
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();		
		controlsLayout.addComponent(descriptionLabel, 0, 2);
		controlsLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		
		descriptionTextArea.setRows(3);
		descriptionTextArea.setWidth("80%");
		controlsLayout.addComponent(descriptionTextArea, 1, 2);
		
		Label roleLabel = new Label("Role:");
		roleLabel.setSizeUndefined();		
		controlsLayout.addComponent(roleLabel, 0, 3);
		controlsLayout.setComponentAlignment(roleLabel, Alignment.MIDDLE_RIGHT);
		
		
		this.rolesCombo = new ComboBox();
		this.rolesCombo.setWidth("80%");
		controlsLayout.addComponent(this.rolesCombo, 1, 3);
		
//		layout.addComponent(filterHintLabel);
		layout.addComponent(controlsLayout);
		layout.addComponent(hListSelectLayout);
		layout.addComponent(hButtonLayout);
		layout.setSizeFull();
		
		Panel filterPanel = new Panel();
		filterPanel.setHeight(500, Unit.PIXELS);
		filterPanel.setWidth("100%");
		filterPanel.setContent(layout);
		filterPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		vSplitPanel.setFirstComponent(filterPanel);
		
		GridLayout hErrorTable = new GridLayout();
		hErrorTable.setWidth("100%");
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		
		GridLayout gl = new GridLayout(2, 1);
		gl.setWidth("100%");
		
		searchResultsSizeLayout.setWidth("100%");
		searchResultsSizeLayout.addComponent(this.resultsLabel);
		searchResultsSizeLayout.setComponentAlignment(this.resultsLabel, Alignment.MIDDLE_LEFT);
		
		gl.addComponent(searchResultsSizeLayout);
		gl.addComponent(hl);
		
		hErrorTable.addComponent(gl);
		hErrorTable.addComponent(this.filterTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(210, Unit.PIXELS);
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setSizeFull();
		wrapper.addComponent(vSplitPanel);
		
		return wrapper;
	}
	
	private void addModule(final Module module)
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
	
	private void addFlow(final Flow flow)
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
	
	private void addComponent(final Component component)
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


	public void refresh()
	{
		tableContainer.removeAllItems();
		
		List<Filter> filters = this.topologyService.getAllFilters();
		
    	for(final Filter filter: filters)
    	{
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(filter.getCreatedDateTime());
    	    
    	    Item item = tableContainer.addItem(filter);			            	    
    	    
    	    item.getItemProperty("Name").setValue(filter.getName());
			item.getItemProperty("Description").setValue(filter.getDescription());
			item.getItemProperty("Created By").setValue(filter.getCreatedBy());
			item.getItemProperty("Timestamp").setValue(timestamp);
    	}
    	
    	List<Role> roles = this.securityService.getAllRoles();
		
		this.rolesCombo.removeAllItems();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
		
		if(roleFilter != null)
    	{
    		for(Role role: (Collection<Role>)rolesCombo.getItemIds())
    		{
    			if(role.getId().equals(roleFilter.getId().getRoleId()))
    			{
    				rolesCombo.select(role);
    			}
    		}
    	}
	}

}
