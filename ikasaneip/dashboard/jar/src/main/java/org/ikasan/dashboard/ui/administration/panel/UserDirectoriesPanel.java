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
package org.ikasan.dashboard.ui.administration.panel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.springframework.transaction.UnexpectedRollbackException;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class UserDirectoriesPanel extends Panel implements View
{
	private Logger logger = LoggerFactory.getLogger(UserDirectoriesPanel.class);
	
	private SecurityService securityService;
    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private LdapService ldapService;
    private GridLayout mainLayout = new GridLayout(1, 4);
    private Table directoryTable;
	private Button newDirectoryButton;

	/**
	 * Constructor
	 *
	 * @param securityService
	 * @param authenticationProviderFactory
	 * @param ldapService
     */
    public UserDirectoriesPanel(SecurityService securityService,
    		AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory,
    		LdapService ldapService)
    {
        super();
        this.securityService = securityService;
        if(this.securityService == null)
        {
        	throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.authenticationProviderFactory = authenticationProviderFactory;
        if(this.authenticationProviderFactory == null)
        {
        	throw new IllegalArgumentException("authenticationProviderFactory cannot be null!");
        }
        this.ldapService = ldapService;
        if(this.ldapService == null)
        {
        	throw new IllegalArgumentException("ldapService cannot be null!");
        }
        init();        
    }

    protected void init()
    {     
    	Label userDirectories = new Label("User Directories");
    	userDirectories.setStyleName(ValoTheme.LABEL_HUGE);
 		
    	Label parapraphOne = new Label();
		parapraphOne.setCaptionAsHtml(true);
		parapraphOne.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The table below shows the user directories currently configured for Ikasan.");
		parapraphOne.addStyleName(ValoTheme.LABEL_TINY);
		parapraphOne.addStyleName(ValoTheme.LABEL_LIGHT);
		
        Label parapraphTwo = new Label();
        parapraphTwo.setCaptionAsHtml(true);
        parapraphTwo.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The order of the directory is the order in which it will be searched for users and groups." +
        		" It is recommended that each user exists in a single directory.");
        parapraphTwo.addStyleName(ValoTheme.LABEL_TINY);
        parapraphTwo.addStyleName(ValoTheme.LABEL_LIGHT);
        
        this.mainLayout.setWidth("100%");
        this.mainLayout.setSpacing(true);
        
        this.mainLayout.addComponent(userDirectories);
        this.mainLayout.addComponent(parapraphOne);
        this.mainLayout.addComponent(parapraphTwo);
        
        newDirectoryButton = new Button("Add Directory");
        newDirectoryButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	final UserDirectoryManagementPanel authMethodPanel = new UserDirectoryManagementPanel(new AuthenticationMethod(), 
    					securityService, authenticationProviderFactory, ldapService);
        		
        		Window window = new Window("Configure User Directory");
        		window.setModal(true);
        		window.setHeight("90%");
        		window.setWidth("90%");
        		
        		window.setContent(authMethodPanel);
        		
        		UI.getCurrent().addWindow(window);
        		
        		window.addCloseListener(new Window.CloseListener() 
        		{
					@Override
					public void windowClose(Window.CloseEvent e)
					{
						populateAll();
					}
                });
            }
        });
        
        this.mainLayout.addComponent(newDirectoryButton);
        
        this.setWidth("100%");
        this.setHeight("100%");
        
        this.directoryTable = new Table();
		this.directoryTable.setWidth("100%");
		this.directoryTable.setHeight("600px");
		this.directoryTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.directoryTable.addContainerProperty("Directory Name", String.class,  null);
		this.directoryTable.addContainerProperty("Type", String.class,  null);
		this.directoryTable.addContainerProperty("Order", Layout.class,  null);
		this.directoryTable.addContainerProperty("Operations", Layout.class,  null);

		this.directoryTable.setColumnExpandRatio("Directory Name", 25);
		this.directoryTable.setColumnExpandRatio("Type", 25);
		
		this.directoryTable.setColumnAlignment("Order",
                Align.CENTER);
		this.directoryTable.setColumnExpandRatio("Order", 10);
		this.directoryTable.setColumnAlignment("Operations",
                Align.CENTER);
		this.directoryTable.setColumnWidth("Operations", 300);
		
		this.mainLayout.addComponent(this.directoryTable);
        
        this.mainLayout.setMargin(true);
        
        Panel wrapperPanel = new Panel();
        wrapperPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        wrapperPanel.setSizeFull();
        wrapperPanel.setContent(this.mainLayout);
        
        HorizontalLayout wrapperLayout = new HorizontalLayout();
        wrapperLayout.setSizeFull();
        wrapperLayout.setMargin(true);
        wrapperLayout.addComponent(wrapperPanel);
        
        this.setContent(wrapperLayout);
    }

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{		
		this.populateAll();
	}
	
	protected void populateAll()
	{
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);

		List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();
		
		this.directoryTable.removeAllItems();
		
		for(final AuthenticationMethod authenticationMethod: authenticationMethods)
		{
			this.populateDirectoryTable(authenticationMethod);
		}	
	}
	
	protected void populateDirectoryTable(final AuthenticationMethod authenticationMethod)
	{
		Button test = new Button("Test");
		test.setStyleName(BaseTheme.BUTTON_LINK);
		test.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		authenticationProviderFactory.testAuthenticationConnection(authenticationMethod);
            	}
            	catch(RuntimeException e)
            	{
            		logger.error("An error occurred testing an LDAP connection", e);
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while testing connection!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	catch(Exception e)
            	{
            		logger.error("An error occurred testing an LDAP connection", e);
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while testing connection!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}

                Notification.show("Connection Successful!");
            }
        });
		
		final Button enableDisableButton = new Button();
		
		if(authenticationMethod.isEnabled())
		{
			enableDisableButton.setCaption("Disable");
		}
		else
		{
			enableDisableButton.setCaption("Enable");
		}
		enableDisableButton.setStyleName(BaseTheme.BUTTON_LINK);
		enableDisableButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		if(authenticationMethod.isEnabled())
            		{
            			authenticationMethod.setEnabled(false);
            		}
            		else
            		{
            			authenticationMethod.setEnabled(true);
            		}
            		
            		securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
            		
            		populateAll();
            	}
            	catch(RuntimeException e)
            	{
            		logger.error("An error occurred saving an authentication method", e);
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to enable/disable the authentication method!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	
            	if(authenticationMethod.isEnabled())
        		{
        			enableDisableButton.setCaption("Disable");
        			Notification.show("Enabled!");
        		}
        		else
        		{
        			enableDisableButton.setCaption("Enable");
        			Notification.show("Disabled!");
        		}
            }
        });
		
		Button delete = new Button("Delete");
		delete.setStyleName(BaseTheme.BUTTON_LINK);
		delete.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		securityService.deleteAuthenticationMethod(authenticationMethod);
            		
            		List<AuthenticationMethod> authenticationMethods = securityService.getAuthenticationMethods();
            		
            		directoryTable.removeAllItems();
            		
            		long order = 1;
            		
            		for(final AuthenticationMethod authenticationMethod: authenticationMethods)
            		{
            			authenticationMethod.setOrder(order++);
            			securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
            		}
            		
            		populateAll();
            	}
            	catch(RuntimeException e)
            	{
            		logger.error("An error occurred deleting an authentication method", e);
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to delete the authentication method!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}

                Notification.show("Deleted!");
            }
        });
		
		Button edit = new Button("Edit");
		edit.setStyleName(BaseTheme.BUTTON_LINK);
		edit.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {            	
        		UserDirectoryManagementPanel authMethodPanel = new UserDirectoryManagementPanel(authenticationMethod, 
    					securityService, authenticationProviderFactory, ldapService);
        		
        		Window window = new Window("Configure User Directory");
        		window.setModal(true);
        		window.setHeight("90%");
        		window.setWidth("90%");
        		
        		window.setContent(authMethodPanel);
        		
        		window.addCloseListener(new Window.CloseListener() 
        		{
                    // inline close-listener
                    public void windowClose(CloseEvent e) 
                    {
                        populateAll();
                    }
                });
        		
        		UI.getCurrent().addWindow(window);
            }
        });
		
		Button synchronise = new Button("Synchronise");
		synchronise.setStyleName(BaseTheme.BUTTON_LINK);
		
		synchronise.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		ldapService.synchronize(authenticationMethod);
            		
            		authenticationMethod.setLastSynchronised(new Date());
	            	securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
	            	
	            	populateAll();
            	}
            	catch(UnexpectedRollbackException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
                    logger.error("Most specific cause: " + e.getMostSpecificCause());
                    e.getMostSpecificCause().printStackTrace();
                    logger.error("Most specific cause: " + e.getRootCause());
                    e.getRootCause().printStackTrace();

                    Notification.show("Error occurred while synchronizing!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	catch(RuntimeException e)
            	{
            		logger.error("An error occurred synchronising an LDAP repository", e); 
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while synchronizing!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	catch(Exception e)
            	{
            		logger.error("An error occurred synchronising an LDAP repository", e); 
            		
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while synchronizing!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            
                Notification.show("Synchronized!");
            }
        });
		
		GridLayout operationsLayout = new GridLayout(9, 2);
		operationsLayout.setWidth("250px");
		operationsLayout.addComponent(enableDisableButton, 0, 0);
		operationsLayout.addComponent(new Label(" "), 1, 0);
		operationsLayout.addComponent(edit, 2, 0);
		operationsLayout.addComponent(new Label(" "), 3, 0);
		operationsLayout.addComponent(delete, 4, 0);
		operationsLayout.addComponent(new Label(" "), 5, 0);
		operationsLayout.addComponent(test, 6, 0);
		operationsLayout.addComponent(new Label(" "), 7, 0);
		operationsLayout.addComponent(synchronise, 8, 0);

		TextArea synchronisedTextArea = new TextArea();			
		synchronisedTextArea.setRows(3);
		synchronisedTextArea.setWordwrap(true);
		
		if(authenticationMethod.getLastSynchronised() != null)
		{
			synchronisedTextArea.setValue("This directory was last synchronised at " + authenticationMethod.getLastSynchronised());
		}
		else
		{
			synchronisedTextArea.setValue("This directory has not been synchronised");
		}
		
		synchronisedTextArea.setSizeFull();
		synchronisedTextArea.setReadOnly(true);
		
		operationsLayout.addComponent(synchronisedTextArea, 0, 1, 8, 1);
		
		HorizontalLayout orderLayout = new HorizontalLayout();
		orderLayout.setWidth("50%");

		Button upArrow = new Button(VaadinIcons.ARROW_UP);
		if(authenticationMethod.getOrder() != 1)
		{
			upArrow.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			upArrow.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			upArrow.addClickListener(new Button.ClickListener() 
	        {
	            public void buttonClick(ClickEvent event) 
	            {            	
	        		if(authenticationMethod.getOrder() != 1)
	        		{
	        			AuthenticationMethod upAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder() -1);
	        			
	        			upAuthMethod.setOrder(authenticationMethod.getOrder());
	        			authenticationMethod.setOrder(authenticationMethod.getOrder() - 1);
	        			
	        			securityService.saveOrUpdateAuthenticationMethod(upAuthMethod);
	        			securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
	        			
	        			populateAll();
	        		}
	            }
	        });
			
			orderLayout.addComponent(upArrow);
		}
		
		
		long numberOfAuthMethods = securityService.getNumberOfAuthenticationMethods();

		Button downArrow = new Button(VaadinIcons.ARROW_DOWN);
		if(authenticationMethod.getOrder() != numberOfAuthMethods)
		{
			downArrow.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			downArrow.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			downArrow.addClickListener(new Button.ClickListener() 
	        {
	            public void buttonClick(ClickEvent event) 
	            {        
	            	long numberOfAuthMethods = securityService.getNumberOfAuthenticationMethods();
	            	
	            	if(authenticationMethod.getOrder() != numberOfAuthMethods)
	        		{
	        			AuthenticationMethod downAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder()  + 1);
	        			
	        			downAuthMethod.setOrder(authenticationMethod.getOrder());
	        			authenticationMethod.setOrder(authenticationMethod.getOrder() + 1);
	        			
	        			securityService.saveOrUpdateAuthenticationMethod(downAuthMethod);
	        			securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
	        			
	        			populateAll();
	        		}
	            }
	        });
			
			orderLayout.addComponent(downArrow);
		}

		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);


		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
				authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_ADMIN))
		{
			test.setVisible(true);
			enableDisableButton.setVisible(true);
			delete.setVisible(true);
			edit.setVisible(true);
			synchronise.setVisible(true);
			upArrow.setVisible(true);
			downArrow.setVisible(true);
			newDirectoryButton.setVisible(true);
		}
		else if(authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_WRITE))
		{
			test.setVisible(true);
			enableDisableButton.setVisible(true);
			synchronise.setVisible(true);
			upArrow.setVisible(true);
			downArrow.setVisible(true);

			delete.setVisible(false);
			edit.setVisible(false);
			newDirectoryButton.setVisible(false);
		}
		else
		{
			test.setVisible(false);
			enableDisableButton.setVisible(false);
			delete.setVisible(false);
			edit.setVisible(false);
			synchronise.setVisible(false);
			upArrow.setVisible(false);
			downArrow.setVisible(false);
			newDirectoryButton.setVisible(false);
		}
		
		this.directoryTable.addItem(new Object[]{authenticationMethod.getName(), "Microsoft Active Directory"
				, orderLayout, operationsLayout}, authenticationMethod);
	}

	
}
