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

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PolicyManagementPanel(UserService userService, SecurityService securityService)
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

		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

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

		final AutocompleteField<Policy> policyNameField = new AutocompleteField<Policy>();
		policyNameField.setWidth("80%");

		final DragAndDropWrapper policyNameFieldWrap = new DragAndDropWrapper(
				policyNameField);
		policyNameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);
		policyNameFieldWrap.setSizeUndefined();

		final TextField description = new TextField();
		description.setWidth("80%");
		
		final Table roleTable = new Table();
		roleTable.addContainerProperty("Role", String.class, null);
		roleTable.addContainerProperty("", Button.class, null);
		roleTable.setHeight("400px");
		roleTable.setWidth("300px");
		
		final Table dropTable = new Table();
		dropTable.addContainerProperty("Members", String.class, null);
		dropTable.addContainerProperty("", Button.class, null);
		dropTable.setHeight("400px");
		dropTable.setWidth("300px");

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
			public void onSuggestionPicked(final Policy policy)
			{
				description.setValue(policy.getDescription());
			}
		});
		

		gridLayout.addComponent(policyNameLabel, 0, 0);
		gridLayout.addComponent(policyNameFieldWrap, 1, 0);

		Label descriptionLabel = new Label("Description");
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.addComponent(description, 1, 2);
		
		Label linkTypeLabel = new Label("Policy Link Type");
		gridLayout.addComponent(linkTypeLabel, 0, 3);
		gridLayout.addComponent(this.linkTypeCombo, 1, 3);
		
		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 5, 1, 5);

		dropTable.setDragMode(TableDragMode.ROW);
		dropTable.setDropHandler(new DropHandler()
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
		            	dropTable.removeItem(principal.getName());
		            	
		            	principal.getRoles().remove(roleToRemove);
		            	
		            	securityService.savePrincipal(principal);
		            	
		            	if(policyNameField.getText().equals(principal.getName()))
		            	{
		            		roleTable.removeItem(roleToRemove);
		            	}
		            }
		        });
				
				dropTable.addItem(new Object[]
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
			            	
			            	dropTable.removeItem(principal.getName());
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
		
		gridLayout.addComponent(roleTable, 0, 6, 1, 6);
					
		this.rolesCombo = new ComboBox("Groups");
		this.rolesCombo.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();
		        
		        logger.info("Value changed got Role: " + role);
		        
		        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
				
				dropTable.removeAllItems();
				
				
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
					
					
					dropTable.addItem(new Object[]
							{ principal.getName(), deleteButton }, principal.getName());
				}
		    }
		});
			
		gridLayout.addComponent(this.rolesCombo, 2, 0);
		gridLayout.addComponent(dropTable, 2, 1, 2, 15);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		this.setContent(layout);
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
