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
package org.ikasan.dashboard.ui.framework.panel;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author CMI2 Development Team
 *
 */
public class PersistanceSetupPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(PersistanceSetupPanel.class);
    private PersistenceServiceFactory<String> persistenceServiceFactory;
    private ComboBox persistanceStoreTypeCombo = new ComboBox("Select database type");

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public PersistanceSetupPanel(PersistenceServiceFactory<String> persistenceServiceFactory)
    {
        super();
        this.persistenceServiceFactory = persistenceServiceFactory;
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("100%");
        verticalLayout.setMargin(true);
        
        initPersistanceStoreTypeCombo();

        Label ikasanWelcomeLabel1 = new Label("Welcome to Ikasan!");
        ikasanWelcomeLabel1.setStyleName("xlarge");
        ikasanWelcomeLabel1.setWidth("100%");
        
        Label ikasanWelcomeLabel2 = new Label("It appears that you are setting up Ikasan for the" +
        		" first time and we need to create some database tables. If this is not the first time accessing the " +
        		"Ikasan Console, it appears that there is an issue with the Ikasan database. If this is the case please " +
        		"contact your local database administrator.");
        ikasanWelcomeLabel2.setStyleName("large");
        ikasanWelcomeLabel2.setWidth("100%");
       

        verticalLayout.addComponent(ikasanWelcomeLabel1);
        verticalLayout.addComponent(ikasanWelcomeLabel2);
        
        verticalLayout.addComponent(persistanceStoreTypeCombo);
        
        Button button = new Button("Create");
        button.setStyleName(Reindeer.BUTTON_SMALL);

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) 
            {
            	String persistenceProvider = (String)PersistanceSetupPanel
            			.this.persistanceStoreTypeCombo.getValue();
            	
            	if(persistenceProvider == null)
            	{
            		 Notification.show("Please select a database type!");
            		 return;
            	}

            	PersistenceService persistanceService 
            		= persistenceServiceFactory.getPersistenceService(persistenceProvider);

            	try
            	{
            		persistanceService.createPersistence();
            		persistanceService.createAdminAccount();
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to create Ikasan database!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
            	}

                Notification.show("Database successfully created!");
            }
        });

        verticalLayout.addComponent(button);
        this.setContent(verticalLayout);
    }
    
    protected void initPersistanceStoreTypeCombo()
    {
    	persistanceStoreTypeCombo.addItem("Sybase12");
    	persistanceStoreTypeCombo.addItem("Sybase15");
    	persistanceStoreTypeCombo.addItem("MySQL");
    	persistanceStoreTypeCombo.addItem("SQLServer2008");
    	persistanceStoreTypeCombo.addItem("H2");
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
    }
}
