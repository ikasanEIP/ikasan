package org.ikasan.dashboard.ui;

import java.util.HashMap;
import java.util.List;

import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.framework.event.AlertEvent;
import org.ikasan.dashboard.ui.framework.event.HealthEvent;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceFactory;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@Push(value=PushMode.AUTOMATIC, transport=Transport.STREAMING)
/**
 * 
 * @author CMI2 Development Team
 *
 */
public class IkasanUI extends UI implements Broadcaster.BroadcastListener
{   
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
    private PersistenceService persistenceService; 
    private NavigationPanel navigationPanel;
    
    private final Table table = new Table();
    private Container container = new IndexedContainer();
    private FeederThread feederThread = new FeederThread();

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
	        AuthenticationService authenticationService, VisibilityGroup visibilityGroup,
            EditableGroup editableGroup,
            FunctionalGroup newMappingConfigurationFunctionalGroup, FunctionalGroup existingMappingConfigurationFunctionalGroup,
            EventBus eventBus, PersistenceServiceFactory<String> persistenceServiceFactory, String persistenceProvider,
            VerticalLayout imagePanelLayout, NavigationPanel navigationPanel)
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
	    this.persistenceService = persistenceServiceFactory.getPersistenceService(persistenceProvider);
	    this.imagePanelLayout = imagePanelLayout;
	    this.navigationPanel = navigationPanel;
	    
	    Broadcaster.register(this);
	}

    @Override
    protected void init(VaadinRequest request) {
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

        boolean usersTablesExist = this.persistenceService.userTablesExist();

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
        
//        if(!this.navigationPanel.isCurrentViewNull())
//        {
//        	this.navigationPanel.navigateToCurrentView();
//        }
//        else
//        {
//        	 Navigator navigator = new Navigator(this, this.views.get("topLevel").getContainer());
//
//             List<IkasanUIView> mappingViews = this.views.get("topLevel").getIkasanViews();
//             
//             for(IkasanUIView view: mappingViews)
//             {
//                 navigator.addView(view.getPath(), view.getView());
//             }
//            
//             if(!usersTablesExist)
//             {
//             	navigator.navigateTo("persistanceSetupView");
//             	navigationPanel.setVisible(false);
//             }
//             else
//             {
//            	 navigator.navigateTo("landingView");  
//            	 navigationPanel.setVisible(true);
//             }
//        }

//        VerticalLayout vlayout = new VerticalLayout();
//
//        Button button = new Button("Start Alerts");
//
//        button.addClickListener(new Button.ClickListener() {
//            public void buttonClick(ClickEvent event) {
//            	feederThread.start();
//            }
//        });
//        
//        vlayout.addComponent(button);
//        layout.addComponent(vlayout);
    }
    
    static class FeederThread extends Thread {
        int count = 0;
        
        @Override
        public void run() {
	    	for(int i=0; i<100; i++)
	    	{
	    		Broadcaster.broadcast("" + System.currentTimeMillis());
	    		System.out.println("Sending Alert!");
    	    	try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
    }

    @Override
	public void receiveBroadcast(final String message)
	{
		access(new Runnable() {
            @Override
            public void run() {
            	eventBus.post(new AlertEvent("Alert:" + message, "Module:" + message));
            	eventBus.post(new HealthEvent("Health Alert:" + message, "Module:" + message));
            }
        });	
	}
	
	// Must also unregister when the UI expires    
    @Override
    public void detach() {
        Broadcaster.unregister(this);
        feederThread.stop();
        super.detach();
    }

	public EventBus getEventBus() {
		return eventBus;
	}
}
