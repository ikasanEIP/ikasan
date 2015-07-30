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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.NewBusinessStreamWindow;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.BusinessStreamFlowKey;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class BusinessStreamTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(BusinessStreamTab.class);
	
	private Table businessStreamTable;

	private TopologyService topologyService;	
	
	private ComboBox businessStreamCombo;
	
	public BusinessStreamTab(TopologyService topologyService, ComboBox businessStreamCombo)
	{
		this.topologyService = topologyService;
		this.businessStreamCombo = businessStreamCombo;
	}
	
	public Layout createBusinessStreamLayout()
	{		
		this.businessStreamTable = new Table();
		this.businessStreamTable.addContainerProperty("Server Name", String.class,  null);
		this.businessStreamTable.addContainerProperty("Module Name", String.class,  null);
		this.businessStreamTable.addContainerProperty("Flow Name", String.class,  null);
		this.businessStreamTable.addContainerProperty("", Button.class,  null);
		this.businessStreamTable.setWidth("100%");
		this.businessStreamTable.setHeight(600, Unit.PIXELS);
		this.businessStreamTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.businessStreamTable.setDragMode(TableDragMode.ROW);
		this.businessStreamTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{		
				final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
			        	.getAttribute(DashboardSessionValueConstants.USER);
				
				if(authentication != null 
		    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
		    					&& !authentication.hasGrantedAuthority(SecurityConstants.MODIFY_BUSINESS_STREAM_AUTHORITY)))
		    	{
					Notification.show("You do not have the privilege to modify a business stream.");
					return;
		    	}

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();

				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
					
					final BusinessStream businessStream = (BusinessStream)businessStreamCombo.getValue();
					BusinessStreamFlowKey key = new BusinessStreamFlowKey();
					key.setBusinessStreamId(businessStream.getId());
					key.setFlowId(flow.getId());
					final BusinessStreamFlow businessStreamFlow = new BusinessStreamFlow(key);
					businessStreamFlow.setFlow(flow);
					businessStreamFlow.setOrder(businessStreamTable.getItemIds().size());
					
					if(!businessStream.getFlows().contains(businessStreamFlow))
					{
						businessStream.getFlows().add(businessStreamFlow);
						
						topologyService.saveBusinessStream(businessStream);
						
						Button deleteButton = new Button();
    					Resource deleteIcon = VaadinIcons.TRASH;
    					deleteButton.setIcon(deleteIcon);
    					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);   
    					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);   
    					deleteButton.setDescription("Delete the flow from the business stream.");

    					deleteButton.setData(businessStreamFlow);
    					
    					// Add the delete functionality to each role that is added
    					deleteButton.addClickListener(new Button.ClickListener() 
    			        {
    			            public void buttonClick(ClickEvent event) 
    			            {	
    			            	businessStream.getFlows().remove(businessStreamFlow);
    			            	    			            	
    			            	topologyService.deleteBusinessStreamFlow(businessStreamFlow);
    			            	topologyService.saveBusinessStream(businessStream);
    			            	
    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
    			            }
    			        });
						
						businessStreamTable.addItem(new Object[]{flow.getModule().getServer().getName()
								, flow.getModule().getName(), flow.getName(), deleteButton}, businessStreamFlow);
					}
				}
				else if(t.getItemId() instanceof Module)
				{
					final Module sourceContainer = (Module) t
							.getItemId();
					
					for(Flow flow: sourceContainer.getFlows())
					{
						
						final BusinessStream businessStream = (BusinessStream)businessStreamCombo.getValue();
						BusinessStreamFlowKey key = new BusinessStreamFlowKey();
						key.setBusinessStreamId(businessStream.getId());
						key.setFlowId(flow.getId());
						final BusinessStreamFlow businessStreamFlow = new BusinessStreamFlow(key);
						businessStreamFlow.setFlow(flow);
						businessStreamFlow.setOrder(businessStreamTable.getItemIds().size());
						
						if(!businessStream.getFlows().contains(businessStreamFlow))
						{
							businessStream.getFlows().add(businessStreamFlow);
							
							topologyService.saveBusinessStream(businessStream);
							    					
							Button deleteButton = new Button();
							Resource deleteIcon = VaadinIcons.TRASH;
							
	    					deleteButton.setIcon(deleteIcon);
	    					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);   
	    					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
	    					deleteButton.setDescription("Delete the flow from the business stream.");
	    					deleteButton.setData(businessStreamFlow);
	    					
	    					// Add the delete functionality to each role that is added
	    					deleteButton.addClickListener(new Button.ClickListener() 
	    			        {
	    			            public void buttonClick(ClickEvent event) 
	    			            {
	    			            	businessStream.getFlows().remove(businessStreamFlow);
	    			            	
	    			            	topologyService.deleteBusinessStreamFlow(businessStreamFlow);
	    			            	topologyService.saveBusinessStream(businessStream);
	    			            	
	    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
	    			            }
	    			        });
							
	    					businessStreamTable.addItem(new Object[]{flow.getModule().getServer().getName()
									, flow.getModule().getName(), flow.getName(), deleteButton}, businessStreamFlow);
						}
					}
				}
				else
				{
					Notification.show("Only modules or flows can be dragged to this table.");
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		Label tableDropHintLabel = new Label();
		tableDropHintLabel.setCaptionAsHtml(true);
		tableDropHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag modules or flows from the topology tree to the table below to build a business stream.");
		tableDropHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		tableDropHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);

		layout.addComponent(tableDropHintLabel);
		
		GridLayout controlsLayout = new GridLayout(3, 3);
		controlsLayout.setColumnExpandRatio(0, .05f);
		controlsLayout.setColumnExpandRatio(1, .65f);
		controlsLayout.setColumnExpandRatio(2, .3f);
		
		controlsLayout.setWidth("100%");
		controlsLayout.setSpacing(true);
		
		Label newBusinessStreamLabel = new Label("New Business Stream:");
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
            	final NewBusinessStreamWindow newBusinessStreamWindow = new NewBusinessStreamWindow();
            	UI.getCurrent().addWindow(newBusinessStreamWindow);
            	
            	newBusinessStreamWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	topologyService.saveBusinessStream(newBusinessStreamWindow.getBusinessStream());
                    	
                    	businessStreamCombo.addItem(newBusinessStreamWindow.getBusinessStream());
                    	businessStreamCombo.setItemCaption(newBusinessStreamWindow.getBusinessStream(), 
                    			newBusinessStreamWindow.getBusinessStream().getName());
                    	
                    	businessStreamCombo.select(newBusinessStreamWindow.getBusinessStream());
                    	
                    	businessStreamTable.removeAllItems();
                    }
                });
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
		
		Label businessStreamLabel = new Label("Business Stream:");
		businessStreamLabel.setSizeUndefined();
		
		final TextArea descriptionTextArea = new TextArea();
		descriptionTextArea.setReadOnly(true);
		
		this.businessStreamCombo.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                	final BusinessStream businessStream  = (BusinessStream)event.getProperty().getValue();
                	
                	descriptionTextArea.setReadOnly(false);
                	descriptionTextArea.setValue(businessStream.getDescription());
                	descriptionTextArea.setReadOnly(true);
                	
                	businessStreamTable.removeAllItems();

                	for(final BusinessStreamFlow businessStreamFlow: businessStream.getFlows())
                	{
                		logger.info("Adding flow: " + businessStreamFlow);
                		Button deleteButton = new Button();
                    	deleteButton.setIcon(VaadinIcons.TRASH);
                    	deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);   
                		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                		deleteButton.setDescription("Delete the flow from the business stream.");
    					
    					// Add the delete functionality to each role that is added
    					deleteButton.addClickListener(new Button.ClickListener() 
    			        {
    			            public void buttonClick(ClickEvent event) 
    			            {		
    			            	businessStream.getFlows().remove(businessStreamFlow);
    			            	
    			            	topologyService.deleteBusinessStreamFlow(businessStreamFlow);
    			            	topologyService.saveBusinessStream(businessStream);
    			            	
    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
    			            }
    			        });
    					
    					
    					final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
    				        	.getAttribute(DashboardSessionValueConstants.USER);
    					
    					if(authentication != null 
    			    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    			    					&& !authentication.hasGrantedAuthority(SecurityConstants.MODIFY_BUSINESS_STREAM_AUTHORITY)))
    			    	{
    						deleteButton.setVisible(false);
    			    	}
    					
    					businessStreamTable.addItem(new Object[]{businessStreamFlow.getFlow().getModule().getServer().getName()
								, businessStreamFlow.getFlow().getName(), businessStreamFlow.getFlow().getName(), deleteButton}, businessStreamFlow);
                	}
                }
            }
        });
		businessStreamCombo.setWidth("100%");
		
		controlsLayout.addComponent(businessStreamLabel, 0, 1);
		controlsLayout.setComponentAlignment(businessStreamLabel, Alignment.MIDDLE_RIGHT);
		controlsLayout.addComponent(businessStreamCombo, 1, 1);
    	
    	Button deleteButton = new Button();
    	deleteButton.setIcon(VaadinIcons.TRASH);
    	deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);   
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteButton.setDescription("Delete the selected business stream.");
    	deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	Collection<BusinessStreamFlow> businessStreamFlows 
            		= (Collection<BusinessStreamFlow>)businessStreamTable.getItemIds();
            	
            	for(BusinessStreamFlow businessStreamFlow: businessStreamFlows)
            	{
            		topologyService.deleteBusinessStreamFlow(businessStreamFlow);
            	}
            	
            	BusinessStream businessStream = (BusinessStream)businessStreamCombo.getValue();
            	
            	topologyService.deleteBusinessStream(businessStream);
            	
            	businessStreamTable.removeAllItems();
            	
            	List<BusinessStream> businessStreams = topologyService.getAllBusinessStreams();
            	
            	businessStreamCombo.removeItem(businessStream);
            	
            	descriptionTextArea.setValue("");
            }
        });
    	
    	if(authentication != null 
    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					&& !authentication.hasGrantedAuthority(SecurityConstants.DELETE_BUSINESS_STREAM_AUTHORITY)))
    	{
    		deleteButton.setVisible(false);
    	}

    	controlsLayout.addComponent(deleteButton, 2, 1);
    	
    	Label descriptionLabel = new Label("Description:");
    	descriptionLabel.setSizeUndefined();
		
    	
    	descriptionTextArea.setRows(4);
    	descriptionTextArea.setWidth("100%");    	
    	controlsLayout.addComponent(descriptionLabel, 0, 2);
		controlsLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		controlsLayout.addComponent(descriptionTextArea, 1, 2);
    	
    	layout.addComponent(controlsLayout);
		layout.addComponent(this.businessStreamTable);
		
		return layout;
	}

}
