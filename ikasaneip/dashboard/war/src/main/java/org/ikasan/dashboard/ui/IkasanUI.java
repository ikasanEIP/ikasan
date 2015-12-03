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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.systemevent.service.SystemEventService;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("dashboard")
@SuppressWarnings("serial")
@Push(value=PushMode.AUTOMATIC, transport=Transport.LONG_POLLING)
@PreserveOnRefresh
public class IkasanUI extends UI implements Broadcaster.BroadcastListener
{   
	private Logger logger = Logger.getLogger(IkasanUI.class);
	
    private HashMap<String, IkasanUINavigator> views;
    private VerticalLayout imagePanelLayout;
    private EventBus eventBus;
    private NavigationPanel navigationPanel;
    
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    
    private ConnectorTracker tracker;

    private final LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
    
    private MenuLayout menuLayout;
    
    private HashMap<Component, String> menuComponents = new HashMap<Component, String>();

    private Image bannerImage;
    
    private Menu menu;
    
    private TopologyStateCache topologyStateCache;
    
    private Label bannerLabel;
    
    private GridLayout mainLayout;
    private CssLayout menuContent;
    private Button showMenuButton;
    private MenuItem settingsItem;
    
    private SystemEventService systemEventService;
    private ErrorReportingManagementService errorReportingManagementService;
    private ErrorReportingService errorReportingService;
    
    /**
     * Constructor 
     * 
     * @param views
     * @param viewComponentContainer
     * @param userService
     * @param authenticationService
     * @param visibilityGroup
     * @param editableGroup
     * @param newMappingConfigurationFunctionalGroup
     * @param existingMappingConfigurationFunctionalGroup
     * @param eventBus
     * @param persistenceServiceFactory
     * @param persistenceProvider
     * @param imagePanelLayout
     * @param navigationPanel
     */
	public IkasanUI(HashMap<String, IkasanUINavigator> views,
	        ViewComponentContainer viewComponentContainer, EventBus eventBus, VerticalLayout imagePanelLayout, 
	        NavigationPanel navigationPanel, MenuLayout menuLayout,
            Image bannerImage, Menu menu, TopologyStateCache topologyStateCache, Label bannerLabel, GridLayout mainLayout,
            CssLayout menuContent, Button showMenuButton, SystemEventService systemEventService,  ErrorReportingManagementService errorReportingManagementService,
        	ErrorReportingService errorReportingService)
	{
	    this.views = views;
	    this.eventBus = eventBus;
	    this.imagePanelLayout = imagePanelLayout;
	    this.navigationPanel = navigationPanel;
	    this.menuLayout = menuLayout;
	    this.bannerImage = bannerImage;
	    this.menu = menu;
	    this.topologyStateCache = topologyStateCache;
	    this.bannerLabel = bannerLabel;
	    this.mainLayout = mainLayout;
	    this.menuContent = menuContent;
	    this.showMenuButton = showMenuButton;
	    this.systemEventService = systemEventService;
	    this.errorReportingManagementService = errorReportingManagementService;
	    this.errorReportingService = errorReportingService;
	    Broadcaster.register(this);
	}

    @Override
    protected void init(VaadinRequest request)
    {    	
    	VaadinSession.getCurrent().setAttribute
    		(DashboardSessionValueConstants.TOPOLOGY_STATE_CACHE, this.topologyStateCache);
    	    	
        addStyleName(ValoTheme.UI_WITH_MENU);
        
        this.mainLayout.setSizeFull();   
        this.setContent(this.mainLayout);

        this.imagePanelLayout.removeAllComponents();
        this.imagePanelLayout.setHeight("70px");

        this.mainLayout.removeAllComponents();
        this.mainLayout.addComponent(imagePanelLayout, 0, 0);

        this.imagePanelLayout.setStyleName("v-header");

        this.imagePanelLayout.addComponent(this.bannerImage);
        this.bannerImage.setHeight("150%");
        this.imagePanelLayout.setExpandRatio(this.bannerImage, 0.5f);
        
        this.bannerLabel.setStyleName("ikasan-maroon");
        this.bannerLabel.setHeight("100%");
        this.imagePanelLayout.addComponent(this.bannerLabel);
        this.imagePanelLayout.setExpandRatio(this.bannerLabel, 0.5f);
        this.imagePanelLayout.setComponentAlignment(this.bannerLabel, Alignment.BOTTOM_LEFT);

        
        this.mainLayout.addComponent(navigationPanel, 0, 1);
        
        loadTopLevelNavigator();
        buildContent();
        this.menuLayout.addMenu(this.menuContent);
        this.mainLayout.addComponent(this.menuLayout, 0, 2);
        
        this.mainLayout.setRowExpandRatio(2, 1);
     
        this.navigationPanel.resetCurrentView();
        this.navigationPanel.setToggleButton(buildToggleButton());
        
        for(Component component: menu.getMenuComponents().keySet())
        {
        	component.setVisible(false);
        }
        
        this.navigationPanel.setMenuComponents(menu.getMenuComponents());

        if(getPage().getUriFragment() == null || (getPage().getUriFragment() != null && !getPage().getUriFragment().equals("!error-occurrence")))
    	{
        	UI.getCurrent().getNavigator().navigateTo("landingView"); 
    	}

        this.navigationPanel.setVisible(true);
        this.navigationPanel.setMenu(menu);
    }
    
    private Component buildContent() 
    {
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(this.menu);

        return menuContent;
    }    
    
    private Component buildToggleButton() 
    {
    	 showMenuButton.addClickListener(new ClickListener() 
         {
             @Override
             public void buttonClick(final ClickEvent event) 
             {
                 if(menu.getStyleName().contains("valo-menu-visible")) 
                 {
 	                menu.setVisible(false);	
                    menu.removeStyleName("valo-menu-visible");
                 } 
                 else 	
                 {
                 	menu.setVisible(true);	
                 	menu.addStyleName("valo-menu-visible");
                 }
             }
         });
    	 
         showMenuButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
         showMenuButton.addStyleName(ValoTheme.BUTTON_SMALL);
         showMenuButton.setIcon(FontAwesome.LIST);
         showMenuButton.setPrimaryStyleName("valo-menu-item");
         menu.setStyleName("valo-menu-visible");
         
         return showMenuButton;
    }
    
    public void loadTopLevelNavigator()
	{
		Navigator navigator = new Navigator(UI.getCurrent(), this.menuLayout.getContentContainer());

		for (IkasanUIView view : this.views.get("topLevel").getIkasanViews())
		{
			navigator.addView(view.getPath(), view.getView());
		}
	}

    @Override
	public void receiveBroadcast(final Object message)
	{
		access(new Runnable() 
		{
            @Override
            public void run() 
            {
            	logger.debug("Broadcasting new FlowStateEvent");
            	eventBus.post(new FlowStateEvent((ConcurrentHashMap<String, String>)message));
            }
        });	
	}
	
	// Must also unregister when the UI expires    
    @Override
    public void detach() 
    {    	   	    	
    	Broadcaster.unregister(this);
    	
    	VaadinSession vSession = VaadinSession.getCurrent();
        WrappedSession httpSession = vSession.getSession();
        
        this.navigationPanel.reset();
        
       //Invalidate HttpSession
        if(httpSession != null)
        {
        	 httpSession.invalidate();
        }
       
        vSession.close();
        
       //Redirect the user to the login/default Page
        Page.getCurrent().setLocation("/ikasan-dashboard");
    }
    
    @Override
    public ConnectorTracker getConnectorTracker() 
    {
      if (this.tracker == null) 
      {
        this.tracker =  new ConnectorTracker(this) 
        {

          @Override
          public void registerConnector(ClientConnector connector) 
          {
            try 
            {
              if(super.getConnector(connector.getConnectorId()) == null)
              {
            	  super.registerConnector(connector);
              }
              else
              {
            	  unregisterConnector(connector);
            	  super.registerConnector(connector);
              }
            } 
            catch (RuntimeException e) 
            {
              logger.info("Failed connector: " + connector.getClass().getSimpleName());
              throw e;
            }
          }

		/* (non-Javadoc)
		 * @see com.vaadin.ui.ConnectorTracker#unregisterConnector(com.vaadin.server.ClientConnector)
		 */
		@Override
		public void unregisterConnector(ClientConnector connector)
		{
			super.unregisterConnector(connector);
		}
        };
      }

      return tracker;
    }

	public EventBus getEventBus()
	{
		return eventBus;
	}
}
