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
package org.ikasan.dashboard.ui.dashboard.panel;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.Broadcaster;
import org.ikasan.dashboard.ui.IkasanUI;
import org.ikasan.dashboard.ui.framework.event.AlertEvent;
import org.ikasan.dashboard.ui.framework.event.HealthEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author CMI2 Development Team
 *
 */
public class DashboardPanel extends Panel implements View//, Broadcaster.BroadcastListener
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(DashboardPanel.class);
    private final Table table = new Table();
    private final Table healthTable = new Table();
    private IndexedContainer container = new IndexedContainer();
    private IndexedContainer healthContainer = new IndexedContainer();
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public DashboardPanel()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");

        GridLayout gridLayout = new GridLayout(3,2);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);

        VerticalLayout layout = new VerticalLayout();

        // Initialize the container as required by the container type
        container.addContainerProperty("Alert", String.class, null);
        container.addContainerProperty("Module",  String.class, null);
        container.addContainerProperty("Details",  PopupView.class, null);


        table.setContainerDataSource(container);
        table.setImmediate(true);
        table.addItemClickListener(new SearchResultTableItemClickListener());

        table.setHeight("100%");
        table.setWidth("100%");
        layout.setSizeFull();
        layout.addComponent(table);
        
        Panel p1 = new Panel("Alerts");
        p1.setStyleName("dashboard");
        p1.setWidth("90%");
        p1.setHeight("90%");
        p1.setContent(layout);

        
        
        gridLayout.addComponent(p1, 0, 0);
        
        Panel p2 = new Panel("Errors");
        p2.setStyleName("dashboard");
        p2.setWidth("90%");
        p2.setHeight("90%");
        
        gridLayout.addComponent(p2, 1, 0);
        
        VerticalLayout healthLayout = new VerticalLayout();

        // Initialize the container as required by the container type
        healthContainer.addContainerProperty("Health Alert", String.class, null);
        healthContainer.addContainerProperty("Module",  String.class, null);


        healthTable.setContainerDataSource(healthContainer);
        healthTable.setImmediate(true);

        healthTable.setHeight("100%");
        healthTable.setWidth("100%");
        healthLayout.addComponent(healthTable);
        
        Panel p3 = new Panel("Health");
        p3.setStyleName("dashboard");
        p3.setWidth("90%");
        p3.setHeight("90%");
        p3.setContent(healthLayout);
        
        gridLayout.addComponent(p3, 2, 0);
        
        Panel p4 = new Panel("Topology");
        p4.setStyleName("dashboard");
        p4.setWidth("90%");
        p4.setHeight("90%");
        
        gridLayout.addComponent(p4, 0, 1);
        
        Panel p5 = new Panel("Dashboard Item 5");
        p5.setStyleName("dashboard");
        p5.setWidth("90%");
        p5.setHeight("90%");
        
        gridLayout.addComponent(p5, 1, 1);
        
        Panel p6 = new Panel("Dashboard Item 6");
        p6.setStyleName("dashboard");
        p6.setWidth("90%");
        p6.setHeight("90%");
        
        gridLayout.addComponent(p6, 2, 1);

        
        this.setContent(gridLayout);
        
//        Broadcaster.register(this);
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

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
    	EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();
    	
    	eventBus.register(this);
    }

	@Subscribe
	public void receiveAlertEvent(final AlertEvent event)
	{
		UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
            	VaadinSession.getCurrent().getLockInstance().lock();
        		try {
        			Item item = container.addItemAt(0, event.getAlert() + this);
        			
        			Property<String> alertProperty =
        			        item.getItemProperty("Alert");

        			alertProperty.setValue(event.getAlert());
        			
        			final Property<String> moduleProperty =
        			        item.getItemProperty("Module");

        			moduleProperty.setValue(event.getModule());
        			
        			TextArea notesField = new TextArea(
                            "Notes");
                    notesField.setWidth("500px");
                    notesField.setRequired(false);
                    notesField.setHeight("150px");
                    notesField.setImmediate(true);
                    notesField.setStyleName("coursedetailtext");
                    // popup notes view
                    final PopupView notesPopupView = new PopupView("Details",
                            notesField);
                    notesPopupView.setCaption("Details");
                    notesPopupView.setHideOnMouseOut(false);
                    notesPopupView.setData(event.getAlert() + this);
                    
                    
                    final Property<PopupView> detailsProperty =
        			        item.getItemProperty("Details");

                    detailsProperty.setValue(notesPopupView);
        			
        			System.out.println("container = " + container);
        			System.out.println("Item = " + item);
        		} finally {
        			VaadinSession.getCurrent().getLockInstance().unlock();
        		}
            	
//            	UI.getCurrent().push();	
            }
        });	
	}
	
	@Subscribe
	public void receiveHealthAlert(final HealthEvent event)
	{
		UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
            	VaadinSession.getCurrent().getLockInstance().lock();
        		try {
        			Item item = healthContainer.addItemAt(0, event.getAlert() + this);
        			
        			Property<String> alertProperty =
        			        item.getItemProperty("Health Alert");

        			alertProperty.setValue(event.getAlert());
        			
        			final Property<String> moduleProperty =
        			        item.getItemProperty("Module");

        			moduleProperty.setValue(event.getModule());

        			System.out.println("container = " + container);
        			System.out.println("Item = " + item);
        		} finally {
        			VaadinSession.getCurrent().getLockInstance().unlock();
        		}
            	
//            	UI.getCurrent().push();	
            }
        });	
	}

	class SearchResultTableItemClickListener implements ItemClickListener
	{
	    /* (non-Javadoc)
	     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
	     */
	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
	    	Window subWindow = new Window("Sub-window");
	        VerticalLayout subContent = new VerticalLayout();
	        subContent.setMargin(true);
	        subWindow.setContent(subContent);
	        
	        Item item = event.getItem();
	        
	        final Property<String> moduleProperty =
			        item.getItemProperty("Module");
	        
	        // Put some components in it
	        subContent.addComponent(new Label(moduleProperty.getValue()));
	        subContent.addComponent(new Button("Awlright"));
	        
	        // Center it in the browser window
	        subWindow.center();
	        
	        // Open it in the UI
	        UI.getCurrent().addWindow(subWindow);
	    }
	}
}
