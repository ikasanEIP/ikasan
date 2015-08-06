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

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("dashboard")
@SuppressWarnings("serial")
//@Push(value=PushMode.AUTOMATIC, transport=Transport.STREAMING)
@PreserveOnRefresh
public class IkasanUI extends UI //implements Broadcaster.BroadcastListener
{   
	private Logger logger = Logger.getLogger(IkasanUI.class);
	
    private HashMap<String, IkasanUINavigator> views;
    private ViewComponentContainer viewComponentContainer;
    private UserService userService;
    private AuthenticationService authenticationService;
    private VisibilityGroup visibilityGroup;
    private EditableGroup editableGroup;
    private FunctionalGroup newMappingConfigurationFunctionalGroup;
    private FunctionalGroup existingMappingConfigurationFunctionalGroup;
    private VerticalLayout imagePanelLayout;
    private EventBus eventBus = new EventBus();
    private NavigationPanel navigationPanel;
    
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    
    private final Table table = new Table();
    private Container container = new IndexedContainer();
//    private FeederThread feederThread = new FeederThread();
    
    private ConnectorTracker tracker;
    
//    private CssLayout menu = new CssLayout();
    private final LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
    private CssLayout menuItemsLayout = new CssLayout();
    private MenuLayout menuLayout;
    private Component menuComponent;
    
    private HashMap<Component, String> menuComponents = new HashMap<Component, String>();

    private ThemeResource bannerImage;
    
    private Menu menu;
    
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
	        ViewComponentContainer viewComponentContainer, UserService userService,
	        AuthenticationService authenticationService, VisibilityGroup visibilityGroup, EditableGroup editableGroup,
            FunctionalGroup newMappingConfigurationFunctionalGroup, FunctionalGroup existingMappingConfigurationFunctionalGroup,
            EventBus eventBus, VerticalLayout imagePanelLayout, NavigationPanel navigationPanel, MenuLayout menuLayout,
            ThemeResource bannerImage, Menu menu)
	{
	    this.views = views;
	    this.userService = userService;
	    this.authenticationService = authenticationService;
	    this.visibilityGroup = visibilityGroup;
	    this.viewComponentContainer = viewComponentContainer;
	    this.editableGroup = editableGroup;
	    this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
	    this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
	    this.eventBus = eventBus;
	    this.imagePanelLayout = imagePanelLayout;
	    this.navigationPanel = navigationPanel;
	    this.menuLayout = menuLayout;
	    this.bannerImage = bannerImage;
	    this.menu = menu;
//	    Broadcaster.register(this);
	}

    @Override
    protected void init(VaadinRequest request)
    {
//    	Responsive.ma	keResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);
        
        final GridLayout layout = new GridLayout(1, 4);	
        layout.setSizeFull();   
        this.setContent(layout);

        imagePanelLayout.removeAllComponents();
        imagePanelLayout.setHeight("70px");

        layout.addComponent(imagePanelLayout, 0, 0);

        imagePanelLayout.setStyleName("v-header");

        Image image = new Image("", bannerImage);
        imagePanelLayout.addComponent(image);
        image.setHeight("150%");
        imagePanelLayout.setExpandRatio(image, 0.5f);
        Label label = new Label("Enterprise Integration Platform");
        label.setStyleName("ikasan-maroon");
        label.setHeight("100%");
        imagePanelLayout.addComponent(label);
        imagePanelLayout.setExpandRatio(label, 0.5f);
        imagePanelLayout.setComponentAlignment(label, Alignment.BOTTOM_LEFT);

        
        layout.addComponent(navigationPanel, 0, 1);
        
        loadTopLevelNavigator();
        menuComponent = buildContent();
        menuLayout.addMenu(menuComponent);
        layout.addComponent(menuLayout, 0, 2);
        
        layout.setRowExpandRatio(2, 1);
     
        this.navigationPanel.resetCurrentView();
        this.navigationPanel.setToggleButton(buildToggleButton());
        
        for(Component component: menu.getMenuComponents().keySet())
        {
        	logger.info("Setting visible false: " + component);
        	component.setVisible(false);
        }
        
        this.navigationPanel.setMenuComponents(menu.getMenuComponents());
        
        UI.getCurrent().getNavigator().navigateTo("landingView");  
	       	navigationPanel.setVisible(true);
    }
    
    private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
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
    	 final Button showMenu = new Button("Menu", new ClickListener() 
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
    	 
         showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
         showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
         showMenu.setIcon(FontAwesome.LIST);
         showMenu.setPrimaryStyleName("valo-menu-item");
         menu.setStyleName("valo-menu-visible");
         
         return showMenu;
    }
    
    public void loadTopLevelNavigator()
	{
		Navigator navigator = new Navigator(UI.getCurrent(), this.menuLayout.getContentContainer());

		for (IkasanUIView view : this.views.get("topLevel").getIkasanViews())
		{
			navigator.addView(view.getPath(), view.getView());
		}
	}

//    @Override
//	public void receiveBroadcast(final String message)
//	{
//		access(new Runnable() {
//            @Override
//            public void run() {
//            	eventBus.post(new AlertEvent("Alert:" + message, "Module:" + message));
//            	eventBus.post(new HealthEvent("Health Alert:" + message, "Module:" + message));
//            }
//        });	
//	}
	
	// Must also unregister when the UI expires    
    @Override
    public void detach() 
    {    	
    	logger.info("detaching UI");
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
              super.registerConnector(connector);
            } 
            catch (RuntimeException e) 
            {
              logger.info("Failed connector: " + connector.getClass().getSimpleName());
              throw e;
            }
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
