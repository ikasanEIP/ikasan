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
import org.ikasan.dashboard.ui.framework.window.AdminPasswordDialog;
import org.ikasan.dashboard.ui.framework.window.PersistenceStatusWindow;
import org.ikasan.security.service.UserService;
import org.ikasan.setup.persistence.service.PersistenceService;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class PersistanceSetupPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(PersistanceSetupPanel.class);
    private PersistenceService persistenceService;
    private ComboBox persistanceStoreTypeCombo = new ComboBox("Select action");
    private boolean userTablesAlreadyExist;
    private UserService userService;

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public PersistanceSetupPanel(PersistenceService persistenceService,
    		UserService userService)
    {
        super();
        this.persistenceService = persistenceService;
        this.userService = userService;
        init();
    }

    protected void init()
    {       
    	initPersistanceStoreTypeCombo();

        this.createOptionsView();
    }
    
    protected void createOptionsView()
    {
    	GridLayout layout = new GridLayout(2, 8);
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);

        Label ikasanWelcomeLabel1 = new Label("Welcome to Ikasan!");
        ikasanWelcomeLabel1.addStyleName(ValoTheme.LABEL_HUGE);
        ikasanWelcomeLabel1.setWidth("100%");
        
        Label ikasanWelcomeLabel2 = new Label("Welcome to Ikasan setup. There are a number of options available to you.");
        
        ikasanWelcomeLabel2.setStyleName("large");
        ikasanWelcomeLabel2.setWidth("60%");
        ikasanWelcomeLabel2.setHeight("50px");
        
        Label ikasanWelcomeLabel3 = new Label("1. You are installing Ikasan for the first time. " +
        		"If this is the case please select the full install option.");
        
        ikasanWelcomeLabel3.setWidth("90%");

        
        Button fullInstallStatusButton = new Button("status");
        fullInstallStatusButton.addStyleName(ValoTheme.BUTTON_LINK);
        
        fullInstallStatusButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		String status = persistenceService.getBaselineStatus();
            		
            		PersistenceStatusWindow persistenceStatusWindow = new PersistenceStatusWindow(status);
            		
            		UI.getCurrent().addWindow(persistenceStatusWindow);
            	}
            	catch(Exception ex)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);

                    Notification.show("Error trying to determine if changes are required to the Ikasan database!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            		
            }
        });
        
        Label ikasanWelcomeLabel4 = new Label("2. You are upgrading from a previous version of Ikasan. " +
        		"If this is the case please select the upgrade option.");
        
        ikasanWelcomeLabel4.setWidth("90%");
        
        Button upgradeInstallStatusButton = new Button("status");
        upgradeInstallStatusButton.addStyleName(ValoTheme.BUTTON_LINK);
        
        upgradeInstallStatusButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		String status = persistenceService.getPostBaselineStatus();
            		
            		PersistenceStatusWindow persistenceStatusWindow = new PersistenceStatusWindow(status);
            		
            		UI.getCurrent().addWindow(persistenceStatusWindow);
            	}
            	catch(Exception ex)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);

                    Notification.show("Error trying to determine if changes are required to the Ikasan database!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            		
            }
        });
        
        Label ikasanWelcomeLabel5 = new Label("3. You wish to provision Ikasan to provide file transfer funtionality. " +
        		"If this is the case please select the install file transfer option.");
        
        ikasanWelcomeLabel5.setWidth("90%");
        
        Button fileTransferStatusButton = new Button("status");
        fileTransferStatusButton.addStyleName(ValoTheme.BUTTON_LINK);
        
        fileTransferStatusButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		String status = persistenceService.getFileTransferStatus();
            		
            		PersistenceStatusWindow persistenceStatusWindow = new PersistenceStatusWindow(status);
            		
            		UI.getCurrent().addWindow(persistenceStatusWindow);
            	}
            	catch(Exception ex)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);

                    Notification.show("Error trying to determine if changes are required to the Ikasan database!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            		
            }
        });
       

        layout.addComponent(ikasanWelcomeLabel1, 0, 0, 1, 0);
        layout.addComponent(ikasanWelcomeLabel2, 0, 1, 1, 1);
        layout.addComponent(ikasanWelcomeLabel3, 0, 2);
        layout.addComponent(fullInstallStatusButton, 1, 2);
        layout.addComponent(ikasanWelcomeLabel4, 0, 3);
        layout.addComponent(upgradeInstallStatusButton, 1, 3);
        layout.addComponent(ikasanWelcomeLabel5, 0, 4);
        layout.addComponent(fileTransferStatusButton, 1, 4);
        
        layout.addComponent(persistanceStoreTypeCombo);
        persistanceStoreTypeCombo.setHeight("30px");
        
        Button button = new Button("Create");
        button.setStyleName(ValoTheme.BUTTON_SMALL);
        button.setHeight("30px");

        button.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	if(persistanceStoreTypeCombo.getValue().equals("Full Install"))
            	{
            		createFull();
            	}
            	else if(persistanceStoreTypeCombo.getValue().equals("Upgrade"))
            	{
            		upgrade();
            	}
            	else if(persistanceStoreTypeCombo.getValue().equals("Provision File Transfer"))
            	{
            		installFileTransfer();
            	}
            		
            }
        });

        layout.addComponent(button, 0, 6, 1, 6);
        layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);

        this.setContent(layout);
    }
    
    protected void createFull()
    {
    	try
    	{
    		if(!persistenceService.baselinePersistenceChangesRequired())
    		{
    			 Notification.show("Your database is already upgraded to the latest version! No changes required."
    		                , Notification.Type.ERROR_MESSAGE);
    		            
    		     return;
    		}
    	}
    	catch(Exception ex)
    	{
    		StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Notification.show("Error trying to determine if changes are required to the Ikasan database!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
            
            return;
    	}
    	
    	final AdminPasswordDialog adminPasswordDialog 
			= new AdminPasswordDialog();
	
    	UI.getCurrent().addWindow(adminPasswordDialog);
	
	
	
		adminPasswordDialog.addCloseListener(new CloseListener() 
		{
	        // inline close-listener
	        public void windowClose(CloseEvent e) 
	        {
	        	String password = adminPasswordDialog.getPassword();
	        	
	        	String persistenceProvider = (String)PersistanceSetupPanel
	        			.this.persistanceStoreTypeCombo.getValue();
	        	
	        	if(persistenceProvider == null)
	        	{
	        		 Notification.show("Please select a database type!");
	        		 return;
	        	}
	
	        	try
	        	{
	        		persistenceService.createBaselinePersistence();
	        		
	        		userService.changeUsersPassword("admin", password, password);
	        	}
	        	catch(Exception ex)
	        	{
	        		StringWriter sw = new StringWriter();
	                PrintWriter pw = new PrintWriter(sw);
	                ex.printStackTrace(pw);
	
	                Notification.show("Error trying to create Ikasan database!", sw.toString()
	                    , Notification.Type.ERROR_MESSAGE);
	                
	                return;
	        	}
	
	            Notification.show("Database successfully created!");
	        }
	    });
    }
    
    protected void upgrade()
    {	
    	try
    	{
    		if(!persistenceService.postBaselinePersistenceChangesRequired())
    		{
    			 Notification.show("Your database is already upgraded to the latest version! No changes required."
    		                , Notification.Type.ERROR_MESSAGE);
    		            
    		     return;
    		}
    		
    		persistenceService.createPostBaselinePersistence();
    	}
    	catch(Exception ex)
    	{
    		StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Notification.show("Error trying to upgrade the Ikasan database!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
            
            return;
    	}
		
    	Notification.show("Database successfully upgraded!");
    }
    
    protected void installFileTransfer()
    {
    	try
    	{
    		if(!persistenceService.fileTransferPersistenceChangesRequired())
    		{
    			 Notification.show("Your database is already provisioned to support file transfer! No changes required."
    		                , Notification.Type.ERROR_MESSAGE);
    		            
    		     return;
    		}
    		
    		persistenceService.createFileTransferPersistence();
    	}
    	catch(Exception ex)
    	{
    		StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Notification.show("Error trying to provision the file transfer tables in the Ikasan database!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
            
            return;
    	}
    			
    	Notification.show("Database successfully provisioned to support transactional file transfer!");
    }
    
    protected void initPersistanceStoreTypeCombo()
    {
    	persistanceStoreTypeCombo.removeAllItems();
    	
    	persistanceStoreTypeCombo.addItem("Full Install");
    	persistanceStoreTypeCombo.addItem("Upgrade");
    	persistanceStoreTypeCombo.addItem("Provision File Transfer");
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
