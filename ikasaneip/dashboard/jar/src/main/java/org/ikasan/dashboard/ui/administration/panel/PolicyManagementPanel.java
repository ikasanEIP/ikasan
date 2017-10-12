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
package org.ikasan.dashboard.ui.administration.panel;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.window.*;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.zybnet.autocomplete.server.AutocompleteField;

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
	private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;
	private PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow;
	private PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow;
	private PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow;

    private SystemEventService systemEventService;


	private FilterTable policyTable;

	private IndexedContainer tableContainer;

	private Button newButton;

    /**
     * Constructor
     *
     * @param userService
     * @param securityService
     * @param policyAssociationMappingSearchWindow
     * @param policyAssociationFlowSearchWindow
     * @param policyAssociationModuleSearchWindow
     * @param policyAssociationBusinessStreamSearchWindow
     */
	public PolicyManagementPanel(UserService userService, SecurityService securityService,
			PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow,
			PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow,
			PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow,
			PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow,
                                 SystemEventService systemEventService)
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
		if (this.policyAssociationMappingSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationMappingSearchWindow cannot be null!");
		}
		this.policyAssociationFlowSearchWindow = policyAssociationFlowSearchWindow;
		if (this.policyAssociationFlowSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationFlowSearchWindow cannot be null!");
		}
		this.policyAssociationModuleSearchWindow = policyAssociationModuleSearchWindow;
		if (this.policyAssociationModuleSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationModuleSearchWindow cannot be null!");
		}
		this.policyAssociationBusinessStreamSearchWindow = policyAssociationBusinessStreamSearchWindow;
		if (this.policyAssociationBusinessStreamSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationBusinessStreamSearchWindow cannot be null!");
		}
        this.systemEventService = systemEventService;
        if (this.systemEventService == null)
        {
            throw new IllegalArgumentException(
                    "systemEventService cannot be null!");
        }

		init();
	}

	protected IndexedContainer buildContainer()
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Name", String.class,  null);
		cont.addContainerProperty("Description", String.class,  null);
		cont.addContainerProperty("", Button.class,  null);

		return cont;
	}

	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");


		Panel securityAdministrationPanel = new Panel();
		securityAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		securityAdministrationPanel.setSizeFull();

		GridLayout gridLayout = new GridLayout(2, 1);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setWidth("100%");

		Label mappingConfigurationLabel = new Label("Policy Management");
		mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
		gridLayout.addComponent(mappingConfigurationLabel, 0, 0);
		gridLayout.setComponentAlignment(mappingConfigurationLabel, Alignment.MIDDLE_LEFT);

		newButton = new Button();
		newButton.setIcon(VaadinIcons.PLUS);
		newButton.setDescription("Create a New Role");
		newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		newButton.addClickListener(new Button.ClickListener()
		{
			public void buttonClick(Button.ClickEvent event)
			{
                final NewPolicyWindow newRoleWindow = new NewPolicyWindow(userService, securityService,
                        policyAssociationMappingSearchWindow, policyAssociationFlowSearchWindow,
                        policyAssociationModuleSearchWindow, policyAssociationBusinessStreamSearchWindow);
				UI.getCurrent().addWindow(newRoleWindow);

				newRoleWindow.addCloseListener(new Window.CloseListener()
				{
					// inline close-listener
					public void windowClose(Window.CloseEvent e)
					{
						Policy policy = newRoleWindow.getPolicy();

						if(policy != null)
						{
							addPolicyToTable(policy);
						}
					}
				});
			}
		});

		gridLayout.addComponent(newButton, 1, 0);
		gridLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);


		this.tableContainer = this.buildContainer();

		this.policyTable = new FilterTable();
		this.policyTable.setSizeFull();

		this.policyTable.setFilterBarVisible(true);
		this.policyTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.policyTable.addStyleName("ikasan");

		this.policyTable.setColumnExpandRatio("Name", .1f);
		this.policyTable.setColumnExpandRatio("Description", .2f);
		this.policyTable.setColumnExpandRatio("", .1f);

        this.policyTable.addStyleName("wordwrap-table");
        this.policyTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());

		this.policyTable.setContainerDataSource(tableContainer);

		this.policyTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
		{
			@Override
			public void itemClick(ItemClickEvent itemClickEvent)
			{

				if(itemClickEvent.isDoubleClick())
				{
					final PolicyWindow window = new PolicyWindow(userService, securityService, systemEventService, (Policy)itemClickEvent.getItemId());
					UI.getCurrent().addWindow(window);

					window.addCloseListener(new Window.CloseListener()
					{
						@Override
						public void windowClose(Window.CloseEvent closeEvent)
						{
							refresh();
						}
					});
				}
			}
		});

		VerticalSplitPanel vpanel = new VerticalSplitPanel(gridLayout
				, this.policyTable);
		vpanel.setSplitPosition(80, Unit.PIXELS);
		vpanel.setLocked(true);

		securityAdministrationPanel.setContent(vpanel);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
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
		this.refresh();
	}

	private void refresh()
	{
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);

		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
				authentication.hasGrantedAuthority(SecurityConstants.POLICY_ADMINISTRATION_ADMIN))
		{
			this.newButton.setVisible(true);
		}
		else
		{
			this.newButton.setVisible(false);
		}

		logger.info("Loading policies");

		List<Policy> policies = this.securityService.getAllPolicies();

		logger.info("Finished loading policies. Number loaded: " + policies.size());

		this.tableContainer.removeAllItems();

		for(final Policy policy: policies)
		{
			this.addPolicyToTable(policy);
		}
	}

	private void addPolicyToTable(final Policy policy)
	{
		Item item = tableContainer.addItem(policy);

		logger.info("Adding policy: " + policy.hashCode());
		logger.info("Adding policy: " + policy);

		item.getItemProperty("Name").setValue(policy.getName());
		item.getItemProperty("Description").setValue(policy.getDescription());

		Button deleteButton = new Button();
		deleteButton.setIcon(VaadinIcons.TRASH);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);


		deleteButton.addClickListener(new Button.ClickListener()
		{
			public void buttonClick(Button.ClickEvent event)
			{
				try
				{
					securityService.deletePolicy(policy);

					tableContainer.removeItem(policy);

					logger.info("Deleting role: " + policy.hashCode());
					logger.info("Deleting role: " + policy);

					Notification.show("Policy deleted!", Notification.Type.HUMANIZED_MESSAGE);
				}
				catch(Exception e)
				{
					e.printStackTrace();

					Notification.show("An error has occurred deleting this policy. A policy cannot be deleted if it is assigned to " +
							"any roles.", Notification.Type.ERROR_MESSAGE);
				}
			}
		});

		final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);

		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
				authentication.hasGrantedAuthority(SecurityConstants.POLICY_ADMINISTRATION_ADMIN))
		{
			deleteButton.setVisible(true);
		}
		else
		{
			deleteButton.setVisible(false);
		}

		item.getItemProperty("").setValue(deleteButton);
	}

}
