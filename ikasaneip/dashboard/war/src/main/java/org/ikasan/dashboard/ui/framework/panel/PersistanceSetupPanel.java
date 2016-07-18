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
import org.ikasan.configurationService.model.PlatformConfiguration;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.dashboard.ui.framework.window.AdminPasswordDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialogLite;
import org.ikasan.dashboard.ui.framework.window.PersistenceStatusWindow;
import org.ikasan.security.model.User;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceException;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.vaadin.teemu.VaadinIcons;

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
import com.vaadin.ui.Window;
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

    private static final String INSTALL = "INSTALL";
    private static final String UPGRADE = "UPGRADE";
    private static final String FILE_TRANSFER = "FILE_TRANSFER";
    
    private Logger logger = Logger.getLogger(PersistanceSetupPanel.class);
    private PersistenceService persistenceService;
    private ComboBox persistanceStoreTypeCombo = new ComboBox("Select action");
    private UserService userService;
    private AuthenticationService authenticationService;
	private Button fullInstallStatusButton = new Button();
	private Button upgradeInstallStatusButton = new Button();
	private Button fileTransferStatusButton = new Button();
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	private IkasanAuthentication ikasanAuthentication;

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public PersistanceSetupPanel(PersistenceService persistenceService,
    		UserService userService, AuthenticationService authenticationService,
    		ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
        super();
        this.persistenceService = persistenceService;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.configurationManagement = configurationManagement;
        init();
    }

    protected void init()
    {       
    	initPersistanceStoreTypeCombo();

        this.createOptionsView();
    }
    
    protected void createOptionsView()
    {
    	GridLayout layout = new GridLayout(2, 9);
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);

		Button returnToLoginScreenButton = new Button("Return to login");
		returnToLoginScreenButton.addStyleName(ValoTheme.BUTTON_LINK);
		returnToLoginScreenButton.addClickListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				UI.getCurrent().getNavigator().navigateTo("loginPanel");
			}
		});

        Label ikasanWelcomeLabel1 = new Label("Welcome to Ikasan!");
        ikasanWelcomeLabel1.addStyleName(ValoTheme.LABEL_HUGE);
        ikasanWelcomeLabel1.setWidth("100%");
        
        Label ikasanWelcomeLabel2 = new Label("Welcome to Ikasan setup. There are a number of options available to you.");
        
        ikasanWelcomeLabel2.setStyleName("large");
        ikasanWelcomeLabel2.setWidth("60%");
        ikasanWelcomeLabel2.setHeight("50px");
        
        Label ikasanWelcomeLabel3 = new Label("Full Installation. You are installing Ikasan for the first time. " +
        		"If this is the case please select the full install option.");
        
        ikasanWelcomeLabel3.setWidth("90%");

        
        fullInstallStatusButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        fullInstallStatusButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        fullInstallStatusButton.setImmediate(true);
        fullInstallStatusButton.setDescription("Click to see status details.");
        
        
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
        
        Label ikasanWelcomeLabel4 = new Label("Upgrade. You are upgrading from a previous version of Ikasan. " +
        		"If this is the case please select the upgrade option.");
        
        ikasanWelcomeLabel4.setWidth("90%");
        
        upgradeInstallStatusButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        upgradeInstallStatusButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        upgradeInstallStatusButton.setImmediate(true);
        upgradeInstallStatusButton.setDescription("Click to see status details.");
        
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
        
        Label ikasanWelcomeLabel5 = new Label("Provision File Transfer. You wish to provision Ikasan to provide file transfer funtionality. " +
        		"If this is the case please select the install file transfer option.");
        
        ikasanWelcomeLabel5.setWidth("90%");
        
        fileTransferStatusButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        fileTransferStatusButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        fileTransferStatusButton.setImmediate(true);
        fileTransferStatusButton.setDescription("Click to see status details.");
        
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
       

		this.updateIcons();

		layout.addComponent(returnToLoginScreenButton, 0, 0, 1, 0);
        layout.addComponent(ikasanWelcomeLabel1, 0, 1, 1, 1);
        layout.addComponent(ikasanWelcomeLabel2, 0, 2, 1, 2);
        layout.addComponent(ikasanWelcomeLabel3, 0, 3);
        layout.addComponent(fullInstallStatusButton, 1, 3);
        layout.addComponent(ikasanWelcomeLabel4, 0, 4);
        layout.addComponent(upgradeInstallStatusButton, 1, 4);
        layout.addComponent(ikasanWelcomeLabel5, 0, 5);
        layout.addComponent(fileTransferStatusButton, 1, 5);
        
        layout.addComponent(persistanceStoreTypeCombo);
        persistanceStoreTypeCombo.setHeight("30px");
        persistanceStoreTypeCombo.setWidth("50%");
        
        Button button = new Button("Create");
        button.setStyleName(ValoTheme.BUTTON_SMALL);
        button.setHeight("30px");

        button.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	User user = null;
        		
        		try
        		{
        			user = userService.loadUserByUsername("admin");
        		}
        		catch(Exception e)
        		{
        			// ignore this as the database may not have been set up yet.
        		}
        		

        		if(persistanceStoreTypeCombo.getValue() == null)
            	{                		
        			Notification.show("Please select an action."
    		                , Notification.Type.ERROR_MESSAGE);
            	}
        		else if(persistanceStoreTypeCombo.getValue().equals("Full Installation"))
            	{                		
            		if(user != null)
            		{
            			if(!baselinePersistenceChangesRequired())
            			{
            				Notification.show("Your database is already upgraded to the latest version! No changes required."
            		                , Notification.Type.ERROR_MESSAGE);
            				
            				return;
            			}

            			final LoginDialogLite dialog = new LoginDialogLite(authenticationService);

                		UI.getCurrent().addWindow(dialog);
                		
                		dialog.addCloseListener(new Window.CloseListener() 
                		{
                            public void windowClose(CloseEvent e) 
                            {
                            	ikasanAuthentication = dialog.getIkasanAuthentication();
                            	
                            	if(ikasanAuthentication != null)
                            	{
                            		createFull();
                            	}
                            }
                        });
            		}
            		else
            		{
            			createFull();
            		}
            	}
            	else if(persistanceStoreTypeCombo.getValue().equals("Upgrade"))
            	{
            		if(!postBaselinePersistenceChangesRequired())
        			{
            			 Notification.show("Your database is already upgraded to the latest version! No changes required."
         		                , Notification.Type.ERROR_MESSAGE);
         		            
            			 return;        			
        			}
            		
            		final LoginDialogLite dialog = new LoginDialogLite(authenticationService);

            		UI.getCurrent().addWindow(dialog);
            		
            		dialog.addCloseListener(new Window.CloseListener() 
            		{
                        public void windowClose(CloseEvent e) 
                        {
                        	ikasanAuthentication = dialog.getIkasanAuthentication();
                        	
                        	if(ikasanAuthentication != null)
                        	{
                        		upgrade();
                        	}
                        }
                    });
            	}
            	else if(persistanceStoreTypeCombo.getValue().equals("Provision File Transfer"))
            	{
            		if(!fileTransferPersistenceChangesRequired())
        			{
            			Notification.show("Your database is already provisioned to support file transfer! No changes required."
        		                , Notification.Type.ERROR_MESSAGE);
        		            
            			return;      			
        			}
            		
            		final LoginDialogLite dialog = new LoginDialogLite(authenticationService);

            		UI.getCurrent().addWindow(dialog);
            		
            		dialog.addCloseListener(new Window.CloseListener() 
            		{
                        public void windowClose(CloseEvent e) 
                        {
                        	ikasanAuthentication = dialog.getIkasanAuthentication();
                        	
                        	if(ikasanAuthentication != null)
                        	{
                        		installFileTransfer();
                        	}
                        }
                    });
            	}
            	
            	ikasanAuthentication = null;
            }
        });

        layout.addComponent(button, 0, 7, 1, 7);
        layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);

        this.setContent(layout);
    }
    
    protected void createFull()
    {    	
    	if(ikasanAuthentication == null)
    	{
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
		        		
		        		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
		        		
		        		PlatformConfiguration platformConfiguration = new PlatformConfiguration();
		        		platformConfiguration.setWebServiceUserAccount("admin");
		        		platformConfiguration.setWebServiceUserPassword(password);
		        		
		        		platformConfigurationConfiguredResource.setConfiguration(platformConfiguration);
		        		
		        		Configuration configuration = configurationManagement.createConfiguration(platformConfigurationConfiguredResource);
		        		
		        		configurationManagement.saveConfiguration(configuration);
		        			
		        		updateIcons();
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
    	else
    	{
    		try
        	{
        		persistenceService.createBaselinePersistence();
        		
        		this.updateIcons();
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
    }
    
    protected void upgrade()
    {
    	try
    	{	 
    		persistenceService.createPostBaselinePersistence();
    		
    		this.updateIcons();
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
    		persistenceService.createFileTransferPersistence();
    		
    		this.updateIcons();
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
    	
    	persistanceStoreTypeCombo.addItem("Full Installation");
    	persistanceStoreTypeCombo.addItem("Upgrade");
    	persistanceStoreTypeCombo.addItem("Provision File Transfer");
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        this.ikasanAuthentication = null;
    }


	public void updateIcons()
	{
		if(this.baselinePersistenceChangesRequired())
		{
			fullInstallStatusButton.setIcon(VaadinIcons.UPLOAD);
		}
		else
		{
			fullInstallStatusButton.setIcon(VaadinIcons.CHECK);
		}
		
		if(this.postBaselinePersistenceChangesRequired())
		{
			upgradeInstallStatusButton.setIcon(VaadinIcons.UPLOAD);
		}
		else
		{
			upgradeInstallStatusButton.setIcon(VaadinIcons.CHECK);
		}
		
		if(this.fileTransferPersistenceChangesRequired())
		{
			fileTransferStatusButton.setIcon(VaadinIcons.UPLOAD);
		}
		else
		{
			fileTransferStatusButton.setIcon(VaadinIcons.CHECK);
		}
	}
	
	private boolean baselinePersistenceChangesRequired()
	{
		try
		{
			return persistenceService.baselinePersistenceChangesRequired();
		} 
        catch (PersistenceServiceException e)
		{
			logger.error("Unable to determine if baseline changes required!", e);
		}
		
		return false;
	}
	
	private boolean postBaselinePersistenceChangesRequired()
	{
		try
		{
			return persistenceService.postBaselinePersistenceChangesRequired();
		} 
        catch (PersistenceServiceException e)
		{
			logger.error("Unable to determine if post baseline changes required!", e);
		}
		
		return false;
	}
	
	private boolean fileTransferPersistenceChangesRequired()
	{
		try
		{
			return persistenceService.fileTransferPersistenceChangesRequired();
		} 
        catch (PersistenceServiceException e)
		{
			logger.error("Unable to determine if file transfer changes required!", e);
		}
		
		return false;
	}
}
