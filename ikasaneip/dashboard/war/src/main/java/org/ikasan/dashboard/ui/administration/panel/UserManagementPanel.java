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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.listener.AssociatedPrincipalItemClickListener;
import org.ikasan.dashboard.ui.administration.window.UserWindow;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.WiretapPayloadViewWindow;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.model.UserLite;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.themes.ValoTheme;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class UserManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(UserManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener;
	private SystemEventService systemEventService;

	private FilterTable userTable;

	private IndexedContainer tableContainer;

	public UserManagementPanel(UserService userService, SecurityService securityService,
			AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener,
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
		this.associatedPrincipalItemClickListener = associatedPrincipalItemClickListener;
		if (this.associatedPrincipalItemClickListener == null)
		{
			throw new IllegalArgumentException(
					"associatedPrincipalItemClickListener cannot be null!");
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

		cont.addContainerProperty("Username", String.class,  null);
		cont.addContainerProperty("First name", String.class,  null);
		cont.addContainerProperty("Last name", String.class,  null);
		cont.addContainerProperty("Email", String.class,  null);
		cont.addContainerProperty("Department", String.class,  null);
		cont.addContainerProperty("Previous Access", String.class,  null);

		return cont;
	}

	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");


		Panel securityAdministrationPanel = new Panel();
		securityAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		securityAdministrationPanel.setSizeFull();

		GridLayout gridLayout = new GridLayout();
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setWidth("100%");
	
		Label mappingConfigurationLabel = new Label("User Management");
 		mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		gridLayout.addComponent(mappingConfigurationLabel);
		gridLayout.setComponentAlignment(mappingConfigurationLabel, Alignment.MIDDLE_LEFT);


		this.tableContainer = this.buildContainer();
		
		this.userTable = new FilterTable();
		this.userTable.setWidth("100%");
		this.userTable.setHeight("900px");

		this.userTable.setFilterBarVisible(true);
		this.userTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.userTable.addStyleName("ikasan");

		this.userTable.setColumnExpandRatio("Username", .1f);
		this.userTable.setColumnExpandRatio("First name", .2f);
		this.userTable.setColumnExpandRatio("Last name", .2f);
		this.userTable.setColumnExpandRatio("Email", .33f);
		this.userTable.setColumnExpandRatio("Department", .2f);
		this.userTable.setColumnExpandRatio("Previous Access", .1f);

		this.userTable.addStyleName("wordwrap-table");
		this.userTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());

		this.userTable.setContainerDataSource(tableContainer);

		this.userTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
		{
			@Override
			public void itemClick(ItemClickEvent itemClickEvent)
			{
				if(itemClickEvent.isDoubleClick())
				{
					UserWindow window = new UserWindow(userService, securityService, systemEventService, (UserLite)itemClickEvent.getItemId());
					UI.getCurrent().addWindow(window);
				}
			}
		});

		gridLayout.addComponent(this.userTable);
		gridLayout.setComponentAlignment(this.userTable, Alignment.MIDDLE_CENTER);

		securityAdministrationPanel.setContent(gridLayout);

		this.setContent(securityAdministrationPanel);
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
		logger.info("Loading users");

		List<UserLite> users = this.userService.getUserLites();

		logger.info("Finished loading users. Number loaded: " + users.size());

		this.tableContainer.removeAllItems();
		
		for(UserLite user: users)
		{
			Date date = new Date(user.getPreviousAccessTimestamp());
			SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
			String timestamp = format.format(date);

			Item item = tableContainer.addItem(user);
			this.userTable.setColumnExpandRatio("Username", .1f);
			this.userTable.setColumnExpandRatio("First name", .2f);
			this.userTable.setColumnExpandRatio("Last name", .2f);
			this.userTable.setColumnExpandRatio("Email", .33f);
			this.userTable.setColumnExpandRatio("Department", .2f);
			this.userTable.setColumnExpandRatio("Previous Access", .1f);


			item.getItemProperty("Username").setValue(user.getUsername());
			item.getItemProperty("First name").setValue(user.getFirstName());
			item.getItemProperty("Last name").setValue(user.getSurname());
			item.getItemProperty("Email").setValue(user.getEmail());
			item.getItemProperty("Department").setValue(user.getDepartment());
			item.getItemProperty("Previous Access").setValue(timestamp);
		}
	}
}
