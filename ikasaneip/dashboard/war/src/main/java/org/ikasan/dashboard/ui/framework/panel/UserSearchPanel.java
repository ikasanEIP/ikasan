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
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.User;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.SecurityServiceException;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 *
 */
public class UserSearchPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(UserSearchPanel.class);
    
    private UserService userService;
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public UserSearchPanel(UserService userService)
    {
        super();
        this.userService = userService;
        if(this.userService == null)
        {
        	throw new IllegalArgumentException("userService cannot be null!");
        }
        
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");
        
        AutocompleteField<User> search = new AutocompleteField<User>();

        

        search.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>() {
          @Override
          public void onSuggestionPicked(User page) {
           
          }
        });
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        Panel securityAdministrationPanel = new Panel("Security Administration");
        securityAdministrationPanel.setStyleName("dashboard");
        securityAdministrationPanel.setHeight("500px");
        securityAdministrationPanel.setWidth("800px");
        
        GridLayout gridLayout = new GridLayout(2, 16);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);

        Label authMethodLabel = new Label("Authentication Method");

        
//        BeanItem<AuthenticationMethod> authenticationMethodItem = new BeanItem<AuthenticationMethod>(authenticationMethod);
//		
//        this.ldapServerUrl.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapServerUrl"));
//        this.ldapBindUserDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserDn"));
//        this.ldapBindUserPassword.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserPassword"));
//        this.ldapUserSearchDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchBaseDn"));
//        this.ldapUserSearchFilter.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchFilter"));        
//        this.emailAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("emailAttributeName"));
//        this.userAccountNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("userAccountNameAttributeName"));
//        this.accountTypeAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("accountTypeAttributeName"));
//        this.applicationSecurityBaseDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityBaseDn"));
//        this.applicationSecurityGroupAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityGroupAttributeName"));
//        this.departmentAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("departmentAttributeName"));
//        this.firstNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("firstNameAttributeName"));
//        this.surnameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("surnameAttributeName"));
        

        Button saveButton = new Button("Search");
        saveButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
//            	try
//            	{
//            		
//            	}
//            	catch(SecurityServiceException e)
//            	{
//            		StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    e.printStackTrace(pw);
//
//                    Notification.show("Error trying to save the authentication method!", sw.toString()
//                        , Notification.Type.ERROR_MESSAGE);
//                    
//                    return;
//            	}
//
//                Notification.show("Saved!");
            }
        });

        

        securityAdministrationPanel.setContent(gridLayout);        
        layout.addComponent(securityAdministrationPanel);
        this.setContent(layout);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
//    	try
//		{
//    		
//		}
//		catch (SecurityServiceException e)
//		{
//			logger.error("Error occurred trying to load authentication method: ", e);
//			
//			StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//
//            Notification.show("Error trying to load the authentication method. ", sw.toString()
//                , Notification.Type.ERROR_MESSAGE);
//		}
    }
 
}
