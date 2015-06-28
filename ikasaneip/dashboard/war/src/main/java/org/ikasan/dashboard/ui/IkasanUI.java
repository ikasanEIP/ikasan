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
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
//@Push(value=PushMode.AUTOMATIC, transport=Transport.STREAMING)
//@PreserveOnRefresh
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
    
    private final Table table = new Table();
    private Container container = new IndexedContainer();
//    private FeederThread feederThread = new FeederThread();
    
    private ConnectorTracker tracker;

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
            EventBus eventBus, VerticalLayout imagePanelLayout, NavigationPanel navigationPanel)
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
	    
//	    Broadcaster.register(this);
	}

    @Override
    protected void init(VaadinRequest request)
    {
        final GridLayout layout = new GridLayout(1, 4);	
        layout.setSizeFull();   
        layout.setMargin(true);
        this.setContent(layout);

        imagePanelLayout.removeAllComponents();
        imagePanelLayout.setHeight("70px");

        layout.addComponent(imagePanelLayout, 0, 0);

        imagePanelLayout.setStyleName("v-header");

        ThemeResource resource = new ThemeResource("images/Ikasan_Logo_Transp.png");
        Image image = new Image("", resource);
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
        
        layout.addComponent(this.views.get("dashboard").getContainer(), 0, 2);
        layout.setRowExpandRatio(2, 1);

        boolean usersTablesExist = true;

        Navigator navigator = new Navigator(this, this.views.get("topLevel").getContainer());

        List<IkasanUIView> mappingViews = this.views.get("topLevel").getIkasanViews();
        
        for(IkasanUIView view: mappingViews)
        {
            navigator.addView(view.getPath(), view.getView());
        }
       
        this.navigationPanel.resetCurrentView();
        
        if(!usersTablesExist)
        {
        	navigator.navigateTo("persistanceSetupView");
        	navigationPanel.setVisible(false);
        }
        else
        {
	       	 navigator.navigateTo("landingView");  
	       	 navigationPanel.setVisible(true);
        }
    }
    
//    static class FeederThread extends Thread {
//        int count = 0;
//        
//        @Override
//        public void run() {
//	    	for(int i=0; i<100; i++)
//	    	{
//	    		Broadcaster.broadcast("" + System.currentTimeMillis());
//	    		System.out.println("Sending Alert!");
//    	    	try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    	}
//	    }
//    }

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
