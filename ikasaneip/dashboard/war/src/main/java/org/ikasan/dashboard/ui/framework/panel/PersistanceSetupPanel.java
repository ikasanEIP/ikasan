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
 * @author Ikasan Development Team
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
