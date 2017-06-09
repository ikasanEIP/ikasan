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
package org.ikasan.dashboard.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class Menu extends CssLayout
{
	private CssLayout menuItemsLayout = new CssLayout();
	private HashMap<Component, List<String>> menuComponents = new HashMap<Component, List<String>>();
	private HashMap<String, IkasanUINavigator> views;
	private MenuLayout menuLayout;
	private Button userItem = new Button();
	private Label lastLoginTimeLabel = new Label();

    /**
     * Constructor
     *
     * @param views
     * @param menuLayout
     */
	public Menu(HashMap<String, IkasanUINavigator> views, MenuLayout menuLayout)
	{
		super();

		this.views = views;
		this.menuLayout = menuLayout;
		
		buildMenu();
	}
	
	protected void buildMenu() 
    {
        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menuItemsLayout.setResponsive(true);
        this.addComponent(menuItemsLayout);  
        
		lastLoginTimeLabel.setPrimaryStyleName("valo-menu-item");
		lastLoginTimeLabel.setVisible(false);
		
		menuItemsLayout.addComponent(lastLoginTimeLabel);
		
		
        Label label = null;
        
        label = new Label("General", ContentMode.HTML);
        label.setPrimaryStyleName("valo-menu-subtitle");
        label.addStyleName("h4");
        label.setSizeUndefined();
        menuItemsLayout.addComponent(label);
        
        userItem.setPrimaryStyleName("valo-menu-item");
		userItem.setIcon(VaadinIcons.USER);
		userItem.setVisible(false);
		userItem.setHtmlContentAllowed(true);
        menuItemsLayout.addComponent(userItem);
        
        userItem.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("profilePanel");
            }
        });
        
        
        final Button dashboardMenuItem = new Button("Dashboard", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("landingView");
            }
        });
                
        dashboardMenuItem.setHtmlContentAllowed(true);
        dashboardMenuItem.setPrimaryStyleName("valo-menu-item");
        dashboardMenuItem.setIcon(VaadinIcons.DASHBOARD);
        menuItemsLayout.addComponent(dashboardMenuItem);

        label = null;
        
        final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
        label = new Label("Services", ContentMode.HTML);
        label.setPrimaryStyleName("valo-menu-subtitle");
        label.addStyleName("h4");
        label.setSizeUndefined();
        menuItemsLayout.addComponent(label);
        
        this.menuComponents.put(label, SecurityConstants.SERVICE_VIEW_PERMISSIONS);
        
        final Button topologyMenuItem = new Button("Topology", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("topologyView");
            }
        });
        
        topologyMenuItem.setHtmlContentAllowed(true);
        topologyMenuItem.setPrimaryStyleName("valo-menu-item");
        topologyMenuItem.setIcon(VaadinIcons.CONNECT_O);
        menuItemsLayout.addComponent(topologyMenuItem);
        this.menuComponents.put(topologyMenuItem, SecurityConstants.SERVICE_VIEW_PERMISSIONS);
        
        final Button mappingMenuItem = new Button("Mapping", new ClickListener()
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("mappingView");
            	
            	loadNavigator("mapping");
            }
        });
        
        mappingMenuItem.setHtmlContentAllowed(true);
        mappingMenuItem.setPrimaryStyleName("valo-menu-item");
        mappingMenuItem.setIcon(VaadinIcons.COPY_O);
        menuItemsLayout.addComponent(mappingMenuItem);
        this.menuComponents.put(mappingMenuItem, SecurityConstants.MAPPING_VIEW_PERMISSIONS);
        
        final Button monitoringMenuItem = new Button("Monitoring", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("monitorView");
            }
        });
        
        monitoringMenuItem.setHtmlContentAllowed(true);
        monitoringMenuItem.setPrimaryStyleName("valo-menu-item");
        monitoringMenuItem.setIcon(VaadinIcons.DESKTOP);
        menuItemsLayout.addComponent(monitoringMenuItem);
        this.menuComponents.put(monitoringMenuItem, SecurityConstants.MONITORING_VIEW_PERMISSIONS);
        
        final Button replayMenuItem = new Button("Replay", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("replayView");
            }
        });
        
        replayMenuItem.setHtmlContentAllowed(true);
        replayMenuItem.setPrimaryStyleName("valo-menu-item");
        replayMenuItem.setIcon(VaadinIcons.RECYCLE);
        menuItemsLayout.addComponent(replayMenuItem);
        this.menuComponents.put(replayMenuItem, SecurityConstants.REPLAY_VIEW_PERMISSIONS);

        final Button houseKeepingMenuItem = new Button("Housekeeping", new ClickListener()
        {
            @Override
            public void buttonClick(final ClickEvent event)
            {
                loadTopLevelNavigator();
                UI.getCurrent().getNavigator().navigateTo("housekeepingView");
            }
        });

        houseKeepingMenuItem.setHtmlContentAllowed(true);
        houseKeepingMenuItem.setPrimaryStyleName("valo-menu-item");
        houseKeepingMenuItem.setIcon(VaadinIcons.TRASH);
        menuItemsLayout.addComponent(houseKeepingMenuItem);
        this.menuComponents.put(houseKeepingMenuItem, SecurityConstants.HOUSEKEEPING_VIEW_PERMISSIONS);
        
        label = new Label("Administration", ContentMode.HTML);
        label.setPrimaryStyleName("valo-menu-subtitle");
        label.addStyleName("h4");
        label.setSizeUndefined();
        menuItemsLayout.addComponent(label);
        
        this.menuComponents.put(label, SecurityConstants.ADMINISTRATION_VIEW_PERMISSIONS);
        
        final Button usersItem = new Button("Users", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("userPanel");
            }
        });
        
        usersItem.setHtmlContentAllowed(true);
        usersItem.setPrimaryStyleName("valo-menu-item");
        usersItem.setIcon(VaadinIcons.USERS);
        menuItemsLayout.addComponent(usersItem);
        
        this.menuComponents.put(usersItem, SecurityConstants.USER_ADMINISTRATION_VIEW_PERMISSIONS);
        
        final Button groupsItem = new Button("Groups", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("principalManagementPanel");
            }
        });
        
        groupsItem.setHtmlContentAllowed(true);
        groupsItem.setPrimaryStyleName("valo-menu-item");
        groupsItem.setIcon(VaadinIcons.GROUP);
        menuItemsLayout.addComponent(groupsItem);
        
        this.menuComponents.put(groupsItem, SecurityConstants.ALL_AUTHORITY);
        
        final Button rolesItem = new Button("Roles", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("roleManagementPanel");
            }
        });
        
        rolesItem.setHtmlContentAllowed(true);
        rolesItem.setPrimaryStyleName("valo-menu-item");
        rolesItem.setIcon(VaadinIcons.SPECIALIST);
        menuItemsLayout.addComponent(rolesItem);
        
        this.menuComponents.put(rolesItem, SecurityConstants.ALL_AUTHORITY);
        
        final Button policyItem = new Button("Policies", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("policyManagementPanel");
            }
        });
        
        policyItem.setHtmlContentAllowed(true);
        policyItem.setPrimaryStyleName("valo-menu-item");
        policyItem.setIcon(VaadinIcons.SAFE);
        menuItemsLayout.addComponent(policyItem);
        
        this.menuComponents.put(policyItem, SecurityConstants.ALL_AUTHORITY);
        
        final Button authItem = new Button("User Directories", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("authenticationMethodView");
            }
        });
        
        authItem.setHtmlContentAllowed(true);
        authItem.setPrimaryStyleName("valo-menu-item");
        authItem.setIcon(VaadinIcons.COG);
        menuItemsLayout.addComponent(authItem);
        
        this.menuComponents.put(authItem, SecurityConstants.ALL_AUTHORITY);
        
        final Button platformConfigItem = new Button("Platform Configuration", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("platformConfigurationView");
            }
        });
        
        platformConfigItem.setHtmlContentAllowed(true);
        platformConfigItem.setPrimaryStyleName("valo-menu-item");
        platformConfigItem.setIcon(VaadinIcons.LIST);
        menuItemsLayout.addComponent(platformConfigItem);
        
        this.menuComponents.put(platformConfigItem, SecurityConstants.ALL_AUTHORITY);
        
        final Button notificationItem = new Button("Notifications", new ClickListener() 
        {
            @Override
            public void buttonClick(final ClickEvent event) 
            {
            	loadTopLevelNavigator();
            	UI.getCurrent().getNavigator().navigateTo("notificationView");
            }
        });
        
        notificationItem.setHtmlContentAllowed(true);
        notificationItem.setPrimaryStyleName("valo-menu-item");
        notificationItem.setIcon(VaadinIcons.EXCLAMATION_CIRCLE_O);
        menuItemsLayout.addComponent(notificationItem);
        
        this.menuComponents.put(notificationItem, SecurityConstants.ALL_AUTHORITY);

    }
	
	public void loadTopLevelNavigator()
	{
		Navigator navigator = new Navigator(UI.getCurrent(), this.menuLayout.getContentContainer());

		for (IkasanUIView view : this.views.get("topLevel").getIkasanViews())
		{
			navigator.addView(view.getPath(), view.getView());
		}
	}

	public void loadNavigator(String name)
	{
    	IkasanUINavigator uiNavigator = this.views.get(name);
    	uiNavigator.setParentContainer(this.menuLayout.getContentContainer());
		Navigator navigator = new Navigator(UI.getCurrent(), uiNavigator.getContainer());

		for (IkasanUIView view : uiNavigator.getIkasanViews())
		{
			navigator.addView(view.getPath(), view.getView());
		}
	}

	/**
	 * @return the menuComponents
	 */
	public HashMap<Component, String> getMenuComponents()
	{
		return menuComponents;
	}

	/**
	 * @param menuComponents the menuComponents to set
	 */
	public void setMenuComponents(HashMap<Component, String> menuComponents)
	{
		this.menuComponents = menuComponents;
	}
	
	public void setLoggedIn()
	{
		IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
            	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(ikasanAuthentication != null)
        {
			String loginName = "User: " + ikasanAuthentication.getName();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			String loginTime = "Last login: " + dateFormat.format(new Date(ikasanAuthentication.getPreviousLoginTimestamp()));
			
			this.userItem.setCaption(loginName);
			this.userItem.setVisible(true);
			
			this.lastLoginTimeLabel.setCaption(loginTime);
			this.lastLoginTimeLabel.setVisible(true);
        }
	}
}
