/*
 * $Id: EstateViewPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/EstateViewPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.administration.panel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.window.NewPolicyWindow;
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationMappingSearchWindow;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class PolicyManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(PolicyManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private ComboBox rolesCombo;
	private ComboBox linkTypeCombo  = new ComboBox();
	private Panel associatedRolesPanel;
	private Panel policyDropPanel;
	private Table roleTable;
	private Table policyDropTable;
	private Button linkButton = new Button("Link");
	private Button newButton = new Button("New");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;
	private Policy policy = new Policy();
	private AutocompleteField<Policy> policyNameField;
	private TextField descriptionField;
	private BeanItem<Policy> policyItem;
	private Long associatedEntityId;
	private TextArea linkedEntity;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PolicyManagementPanel(UserService userService, SecurityService securityService,
			PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow)
	{
		super();
		this.userService = userService;
		if (this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"securityService cannot be null!");
		}
		this.policyAssociationMappingSearchWindow = policyAssociationMappingSearchWindow;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationMappingSearchWindow cannot be null!");
		}

		init();
	}

	@SuppressWarnings({ "deprecation", "serial" })
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.createAssociatedRolesPanel();
		this.createPolicyDropPanel();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("Policy Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(3, 16);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();

		Label policyNameLabel = new Label("Policy Name");

		this.policyNameField = new AutocompleteField<Policy>();
		policyNameField.setWidth("80%");

		final DragAndDropWrapper policyNameFieldWrap = new DragAndDropWrapper(
				policyNameField);
		policyNameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);
		policyNameFieldWrap.setSizeUndefined();

		this.descriptionField = new TextField();
		descriptionField.setWidth("80%");

		policyNameField.setQueryListener(new AutocompleteQueryListener<Policy>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<Policy> field,
					String query)
			{
				for (Policy policy : securityService.getPolicyByNameLike(query))
				{
					field.addSuggestion(policy, policy.getName());
				}
			}
		});

		policyNameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<Policy>()
		{
			@Override
			public void onSuggestionPicked(final Policy pickedPolicy)
			{
				PolicyManagementPanel.this.policy = pickedPolicy;
				
				PolicyManagementPanel.this.policyItem = new BeanItem<Policy>(PolicyManagementPanel.this.policy);
				PolicyManagementPanel.this.policyNameField.setPropertyDataSource(policyItem.getItemProperty("name"));
				PolicyManagementPanel.this.descriptionField.setPropertyDataSource(policyItem.getItemProperty("description"));
		        
				if(PolicyManagementPanel.this.policy.getPolicyLink() != null)
				{
					PolicyManagementPanel.this.linkTypeCombo.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getPolicyLinkType());
					PolicyManagementPanel.this.linkedEntity.setValue(Long.toString(PolicyManagementPanel.this.policy.getPolicyLink().getTargetId()));
				}
				else
				{
					PolicyManagementPanel.this.linkTypeCombo.setValue(new String());
					PolicyManagementPanel.this.linkedEntity.setValue(new String());
				}
			}
		});
		
		this.newButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewPolicyWindow newPolicyWindow = new NewPolicyWindow();
                UI.getCurrent().addWindow(newPolicyWindow);
                
                newPolicyWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	PolicyManagementPanel.this.policy = newPolicyWindow.getPolicy();
                		
                    	PolicyManagementPanel.this.policyItem = new BeanItem<Policy>(PolicyManagementPanel.this.policy);
                		PolicyManagementPanel.this.policyNameField.setText(PolicyManagementPanel.this.policy.getName());
                		PolicyManagementPanel.this.policyNameField.setPropertyDataSource(policyItem.getItemProperty("name"));
                		PolicyManagementPanel.this.descriptionField.setPropertyDataSource(policyItem.getItemProperty("description"));
                    }
                });
            }
        });
    	
    	this.saveButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		PolicyManagementPanel.this.save();
            		
            		Notification.show("Saved");
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
            		Notification.show("Cauget exception trying to save a Policy!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
            	}
            }
        });
    	
    	this.deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		PolicyManagementPanel.this.securityService.deletePolicy(policy);
            		
            		Notification.show("Deleted");
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
            		Notification.show("Cauget exception trying to delete a Policy!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
            	}
            }
        });
    	
    	HorizontalLayout controlLayout =new HorizontalLayout();
    	controlLayout.setWidth("100%");
    	Label spacerLabel = new Label("");
    	controlLayout.addComponent(spacerLabel);
    	controlLayout.setExpandRatio(spacerLabel, 0.865f);
    	controlLayout.addComponent(newButton);
    	controlLayout.setExpandRatio(newButton, 0.045f);
    	controlLayout.addComponent(saveButton);
    	controlLayout.setExpandRatio(saveButton, 0.045f);
    	controlLayout.addComponent(deleteButton);
    	controlLayout.setExpandRatio(deleteButton, 0.045f);

    	gridLayout.addComponent(controlLayout, 0, 0, 1, 0);
    	
		gridLayout.addComponent(policyNameLabel, 0, 1);
		gridLayout.addComponent(policyNameFieldWrap, 1, 1);

		Label descriptionLabel = new Label("Description");
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.addComponent(descriptionField, 1, 2);
		
		Label linkTypeLabel = new Label("Policy Link Type");
		gridLayout.addComponent(linkTypeLabel, 0, 3);
		gridLayout.addComponent(this.linkTypeCombo, 1, 3);
		
		linkButton.setStyleName(Reindeer.BUTTON_LINK);
    	linkButton.setVisible(false);
    	linkButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	policyAssociationMappingSearchWindow.clear();
                UI.getCurrent().addWindow(policyAssociationMappingSearchWindow);
            }
        });
    	gridLayout.addComponent(this.linkButton, 1, 4);
    	
    	final Label linkedEntityLabel = new Label("Linked to");
		linkedEntity = new TextArea();
		linkedEntity.setWidth("80%");
		linkedEntity.setHeight("30px");
		
    	gridLayout.addComponent(linkedEntityLabel, 0, 5);
		gridLayout.addComponent(linkedEntity, 1, 5);
    	
    	this.policyAssociationMappingSearchWindow.addCloseListener(new Window.CloseListener() {
            // inline close-listener
            public void windowClose(CloseEvent e) {
            	linkedEntity.setValue(policyAssociationMappingSearchWindow.getMappingConfiguration().toString());
            	associatedEntityId = policyAssociationMappingSearchWindow.getMappingConfiguration().getId();
            }
        });
		
		this.linkTypeCombo.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final PolicyLinkType policyLinkType = (PolicyLinkType)event.getProperty().getValue();
		        
		        if(policyLinkType != null)
		        {		        	
		        	linkButton.setVisible(true);
		        	linkedEntityLabel.setVisible(true);
		        	linkedEntity.setVisible(true);
		        	
		        }
		        else
		        {
		        	linkButton.setVisible(false);
		        	linkedEntityLabel.setVisible(false);
		        	linkedEntity.setVisible(false);
		        }
		    }
		});
		
//		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 5, 1, 5);

		gridLayout.addComponent(this.associatedRolesPanel, 0, 6, 1, 6);
			
		gridLayout.addComponent(this.policyDropPanel, 2, 0, 2, 15);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		this.setContent(layout);
	}

	protected void createAssociatedRolesPanel()
	{
		this.associatedRolesPanel = new Panel("Role Associations");
		this.associatedRolesPanel.setStyleName("dashboard");
		this.associatedRolesPanel.setHeight("600px");
		this.associatedRolesPanel.setWidth("100%");
		
		this.roleTable = new Table();
		this.roleTable.addContainerProperty("Role", String.class, null);
		this.roleTable.addContainerProperty("", Button.class, null);
		this.roleTable.setHeight("400px");
		this.roleTable.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(this.roleTable);
		
		this.associatedRolesPanel.setContent(layout);
	}

	protected void createPolicyDropPanel()
	{
		this.policyDropPanel = new Panel();
		
		this.policyDropPanel.setStyleName("dashboard");
		this.policyDropPanel.setHeight("600px");
		this.policyDropPanel.setWidth("100%");
		
		this.rolesCombo = new ComboBox();
		this.rolesCombo.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();
		        
		        logger.info("Value changed got Role: " + role);
		        
		        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
				
				policyDropTable.removeAllItems();
				
				
				for(final IkasanPrincipal principal: principals)
				{
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            }
			        });
					
					
					policyDropTable.addItem(new Object[]
							{ principal.getName(), deleteButton }, principal.getName());
				}
		    }
		});
		
		this.policyDropTable = new Table();
		this.policyDropTable.addContainerProperty("Role Policies", String.class, null);
		this.policyDropTable.addContainerProperty("", Button.class, null);
		this.policyDropTable.setHeight("400px");
		this.policyDropTable.setWidth("300px");
		
		policyDropTable.setDragMode(TableDragMode.ROW);
		policyDropTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final WrapperTransferable t = (WrapperTransferable) dropEvent
						.getTransferable();

				final AutocompleteField sourceContainer = (AutocompleteField) t
						.getDraggedComponent();
				logger.info("sourceContainer.getText(): "
						+ sourceContainer.getText());

				Button deleteButton = new Button();
				ThemeResource deleteIcon = new ThemeResource(
						"images/remove-icon.png");
				deleteButton.setIcon(deleteIcon);
				deleteButton.setStyleName(Reindeer.BUTTON_LINK);
				
				final IkasanPrincipal principal = securityService.findPrincipalByName(sourceContainer.getText());
				final Role roleToRemove = (Role)rolesCombo.getValue();
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {
		            }
		        });
				
				policyDropTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());

				principal.getRoles().add((Role)rolesCombo.getValue());
				
				securityService.savePrincipal(principal);

				roleTable.removeAllItems();

				for (final Role role : principal.getRoles())
				{
					Button roleDeleteButton = new Button();
					roleDeleteButton.setIcon(deleteIcon);
					roleDeleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					roleDeleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	policyDropTable.removeItem(principal.getName());
			            }
			        }); 
					
					roleTable.addItem(new Object[]
					{ role.getName(), roleDeleteButton }, role);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(this.rolesCombo);
		layout.addComponent(this.policyDropTable);
		
		this.policyDropPanel.setContent(layout);
	}
	
	protected void save()
	{
		if(this.linkTypeCombo.getValue() != null)
		{
			PolicyLinkType policyLinkType = (PolicyLinkType)this.linkTypeCombo.getValue();
			PolicyLink policyLink = new PolicyLink(policyLinkType, this.associatedEntityId);
			
			this.securityService.savePolicyLink(policyLink);
			
			this.policy.setPolicyLink(policyLink);
			
			this.securityService.savePolicy(this.policy);
		}
		else
		{
			PolicyLink policyLink = this.policy.getPolicyLink();
			this.policy.setPolicyLink(null);
			
			this.securityService.savePolicy(this.policy);
			
			if(policyLink != null)
			{
				this.securityService.deletePolicyLink(policyLink);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		List<Role> roles = this.securityService.getAllRoles();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
		
		List<PolicyLinkType> policyLinkTypes = this.securityService.getAllPolicyLinkTypes();
		
		for(PolicyLinkType policyLinkType: policyLinkTypes)
		{
			this.linkTypeCombo.addItem(policyLinkType);
			this.linkTypeCombo.setItemCaption(policyLinkType, policyLinkType.getName());
		}
	}

}
