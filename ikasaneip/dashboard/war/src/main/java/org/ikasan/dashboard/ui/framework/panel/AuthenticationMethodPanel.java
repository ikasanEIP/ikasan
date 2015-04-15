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
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.ikasan.security.dao.SecurityDaoException;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.SecurityServiceException;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author CMI2 Development Team
 *
 */
public class AuthenticationMethodPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(AuthenticationMethodPanel.class);

    private HashMap<String, AuthenticationMethodDropdownValue> authenticationMethodDropdownValuesMap 
    	= new HashMap<String, AuthenticationMethodDropdownValue>();
    
    private final AuthenticationMethodDropdownValue LOCAL_AUTHENTICATION 
		= new AuthenticationMethodDropdownValue("Local Authentication", SecurityConstants.AUTH_METHOD_LOCAL);
	private final AuthenticationMethodDropdownValue LDAP_LOCAL_AUTHENTICATION 
		= new AuthenticationMethodDropdownValue("LDAP + Local Authentication", SecurityConstants.AUTH_METHOD_LDAP_LOCAL);
	private final AuthenticationMethodDropdownValue LDAP_AUTHENTICATION 
		= new AuthenticationMethodDropdownValue("LDAP Authentication", SecurityConstants.AUTH_METHOD_LDAP);
    
    private SecurityService securityService;
    private TextField ldapServerUrl;
    private TextField ldapBindUserDn;
    private TextField ldapBindUserPassword;
    private TextField ldapUserSearchDn;
    private TextField ldapUserSearchFilter;
	private TextField accountTypeAttributeName;
	private TextField userAccountNameAttributeName;
	private TextField emailAttributeName;
	private TextField firstNameAttributeName;
	private TextField surnameAttributeName;
	private TextField departmentAttributeName;
	private TextField ldapUserDescriptionAttributeName;
	private TextField applicationSecurityBaseDn;
	private TextField applicationSecurityGroupAttributeName;
	private TextField applicationSecurityDescriptionAttributeName;
	private TextField memberofAttributeName;
    
    private AuthenticationMethod authenticationMethod = new AuthenticationMethod();
    private ComboBox authenticationMethodCombo = new ComboBox();
    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private LdapService ldapService;
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public AuthenticationMethodPanel(SecurityService securityService,
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
        this.setWidth("100%");
        this.setHeight("100%");
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        Panel securityAdministrationPanel = new Panel("Security Administration");
        securityAdministrationPanel.setStyleName("dashboard");
        securityAdministrationPanel.setHeight("500px");
        securityAdministrationPanel.setWidth("800px");
        
        GridLayout gridLayout = new GridLayout(2, 19);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);

        Label authMethodLabel = new Label("Authentication Method");

        authenticationMethodCombo.addItem(LOCAL_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LOCAL_AUTHENTICATION, LOCAL_AUTHENTICATION.getCaption());
        authenticationMethodCombo.addItem(LDAP_LOCAL_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LDAP_LOCAL_AUTHENTICATION, LDAP_LOCAL_AUTHENTICATION.getCaption());
        authenticationMethodCombo.addItem(LDAP_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LDAP_AUTHENTICATION, LDAP_AUTHENTICATION.getCaption());
 
        authenticationMethodDropdownValuesMap.put(LOCAL_AUTHENTICATION.getValue(), LOCAL_AUTHENTICATION);
        authenticationMethodDropdownValuesMap.put(LDAP_LOCAL_AUTHENTICATION.getValue(), LDAP_LOCAL_AUTHENTICATION);
        authenticationMethodDropdownValuesMap.put(LDAP_AUTHENTICATION.getValue(), LDAP_AUTHENTICATION);
        
        gridLayout.addComponent(authMethodLabel, 0, 0);
        gridLayout.addComponent(authenticationMethodCombo, 1, 0);

        Label ldapServerUrlLabel = new Label("LDAP Server URL");
        this.ldapServerUrl = new TextField();
        this.ldapServerUrl.setWidth("100%");
        
        gridLayout.addComponent(ldapServerUrlLabel, 0, 1);
        gridLayout.addComponent(this.ldapServerUrl, 1, 1);
        
        Label ldapBindUserDnLabel = new Label("LDAP Bind User DN");
        this.ldapBindUserDn = new TextField();
        this.ldapBindUserDn.setWidth("100%");
        
        gridLayout.addComponent(ldapBindUserDnLabel, 0, 2);
        gridLayout.addComponent(this.ldapBindUserDn, 1, 2);
        
        Label ldapBindUserPasswordLabel = new Label("LDAP Bind User Password");
        this.ldapBindUserPassword = new TextField();
        this.ldapBindUserPassword.setWidth("100%");
        
        gridLayout.addComponent(ldapBindUserPasswordLabel, 0, 3);
        gridLayout.addComponent(this.ldapBindUserPassword, 1, 3);
        
        Label ldapUserSearchDnLabel = new Label("LDAP User Search DN");
        this.ldapUserSearchDn = new TextField();
        this.ldapUserSearchDn.setWidth("100%");
        
        gridLayout.addComponent(ldapUserSearchDnLabel, 0, 4);
        gridLayout.addComponent(this.ldapUserSearchDn, 1, 4);
        
        Label ldapUserSearchFilterLabel = new Label("LDAP User Search Filter");
        this.ldapUserSearchFilter = new TextField();
        this.ldapUserSearchFilter.setWidth("100%");

        gridLayout.addComponent(ldapUserSearchFilterLabel, 0, 5);
        gridLayout.addComponent(this.ldapUserSearchFilter, 1, 5);

        Label emailAttributeNameLabel = new Label("LDAP User Email Attribute Name");
        this.emailAttributeName = new TextField();
        this.emailAttributeName.setWidth("100%");

        gridLayout.addComponent(emailAttributeNameLabel, 0, 6);
        gridLayout.addComponent(this.emailAttributeName, 1, 6);
        
        Label userAccountNameAttributeNameLabel = new Label("LDAP User Account Name Attribute Name");
        this.userAccountNameAttributeName = new TextField();
        this.userAccountNameAttributeName.setWidth("100%");

        gridLayout.addComponent(userAccountNameAttributeNameLabel, 0, 7);
        gridLayout.addComponent(this.userAccountNameAttributeName, 1, 7);

        Label accountTypeAttributeNameLabel = new Label("LDAP User Account Type Attribute Name");
        this.accountTypeAttributeName = new TextField();
        this.accountTypeAttributeName.setWidth("100%");

        gridLayout.addComponent(accountTypeAttributeNameLabel, 0, 8);
        gridLayout.addComponent(this.accountTypeAttributeName, 1, 8);
        
        Label firstNameAttributeNameLabel = new Label("LDAP User First Name Attribute Name");
        this.firstNameAttributeName = new TextField();
        this.firstNameAttributeName.setWidth("100%");

        gridLayout.addComponent(firstNameAttributeNameLabel, 0, 9);
        gridLayout.addComponent(this.firstNameAttributeName, 1, 9);
        
        Label surnameAttributeNameLabel = new Label("LDAP User Surname Attribute Name");
        this.surnameAttributeName = new TextField();
        this.surnameAttributeName.setWidth("100%");

        gridLayout.addComponent(surnameAttributeNameLabel, 0, 10);
        gridLayout.addComponent(this.surnameAttributeName, 1, 10);

        Label departmentAttributeNameLabel = new Label("LDAP User Department Attribute Name");
        this.departmentAttributeName = new TextField();
        this.departmentAttributeName.setWidth("100%");
        
        gridLayout.addComponent(departmentAttributeNameLabel, 0, 11);
        gridLayout.addComponent(this.departmentAttributeName, 1, 11);
        
        Label ldapUserDescriptionAttributeNameLabel = new Label("LDAP User Description Attribute Name");
        this.ldapUserDescriptionAttributeName = new TextField();
        this.ldapUserDescriptionAttributeName.setWidth("100%");

        gridLayout.addComponent(ldapUserDescriptionAttributeNameLabel, 0, 12);
        gridLayout.addComponent(this.ldapUserDescriptionAttributeName, 1, 12);
        
        Label memberOfAttributeNameLabel = new Label("LDAP Member Of Attribute Name");
        this.memberofAttributeName = new TextField();
        this.memberofAttributeName.setWidth("100%");

        gridLayout.addComponent(memberOfAttributeNameLabel, 0, 13);
        gridLayout.addComponent(this.memberofAttributeName, 1, 13);

        Label applicationSecurityBaseDnLabel = new Label("Application Security Base DN");
        this.applicationSecurityBaseDn = new TextField();
        this.applicationSecurityBaseDn.setWidth("100%");

        gridLayout.addComponent(applicationSecurityBaseDnLabel, 0, 14);
        gridLayout.addComponent(this.applicationSecurityBaseDn, 1, 14);
        
        Label applicationSecurityGroupAttributeNameLabel = new Label("Application Security Group Attribute Name");
        this.applicationSecurityGroupAttributeName = new TextField();
        this.applicationSecurityGroupAttributeName.setWidth("100%");

        gridLayout.addComponent(applicationSecurityGroupAttributeNameLabel, 0, 15);
        gridLayout.addComponent(this.applicationSecurityGroupAttributeName, 1, 15);
        
        Label applicationSecurityAttributeNameLabel = new Label("Application Security Description Attribute Name");
        this.applicationSecurityDescriptionAttributeName = new TextField();
        this.applicationSecurityDescriptionAttributeName.setWidth("100%");

        gridLayout.addComponent(applicationSecurityAttributeNameLabel, 0, 16);
        gridLayout.addComponent(this.applicationSecurityDescriptionAttributeName, 1, 16);

        
        BeanItem<AuthenticationMethod> authenticationMethodItem = new BeanItem<AuthenticationMethod>(authenticationMethod);
		
        this.ldapServerUrl.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapServerUrl"));
        this.ldapBindUserDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserDn"));
        this.ldapBindUserPassword.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserPassword"));
        this.ldapUserSearchDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchBaseDn"));
        this.ldapUserSearchFilter.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchFilter"));        
        this.emailAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("emailAttributeName"));
        this.userAccountNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("userAccountNameAttributeName"));
        this.accountTypeAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("accountTypeAttributeName"));
        this.applicationSecurityBaseDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityBaseDn"));
        this.applicationSecurityGroupAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityGroupAttributeName"));
        this.departmentAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("departmentAttributeName"));
        this.firstNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("firstNameAttributeName"));
        this.surnameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("surnameAttributeName"));
        this.ldapUserDescriptionAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserDescriptionAttributeName"));
        this.applicationSecurityDescriptionAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityDescriptionAttributeName"));
        this.memberofAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("memberofAttributeName"));
        

        Button saveButton = new Button("Save");
        saveButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		logger.info("method combo value: " + authenticationMethodCombo.getValue());
            		logger.info("authenticationMethod: " + authenticationMethod);
            		logger.info("method combo value: " + ((AuthenticationMethodDropdownValue)authenticationMethodCombo.getValue()).getValue());
            		authenticationMethod.setMethod(((AuthenticationMethodDropdownValue)authenticationMethodCombo.getValue()).getValue());
            		securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to save the authentication method!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}

                Notification.show("Saved!");
            }
        });

        Button testConnectionButton = new Button("Test Connection");
        testConnectionButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		authenticationMethod.setMethod(((AuthenticationMethodDropdownValue)authenticationMethodCombo.getValue()).getValue());

            		authenticationProviderFactory.testAuthenticationConnection(authenticationMethod);
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while testing connection!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	catch(Exception e)
            	{
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
        
        gridLayout.addComponent(saveButton, 0, 17);
        gridLayout.addComponent(testConnectionButton, 1, 17);
        
        Button sychronizeButton = new Button("Synchronize");
        sychronizeButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		ldapService.synchronize();
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while synchronizing!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    return;
            	}
            	catch(Exception e)
            	{
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
        gridLayout.addComponent(sychronizeButton, 0, 18);

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
    	try
		{
    		AuthenticationMethod loadedAuthenticationMethod = securityService.getAuthenticationMethod();

			if(loadedAuthenticationMethod != null)
			{
				this.authenticationMethod = loadedAuthenticationMethod;

				BeanItem<AuthenticationMethod> authenticationMethodItem = new BeanItem<AuthenticationMethod>(authenticationMethod);

				this.authenticationMethodCombo.setValue(this.authenticationMethodDropdownValuesMap.get(loadedAuthenticationMethod.getMethod()));

		    	this.ldapServerUrl.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapServerUrl"));
		    	this.ldapBindUserDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserDn"));
		    	this.ldapBindUserPassword.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapBindUserPassword"));
		    	this.ldapUserSearchDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchBaseDn"));
		    	this.ldapUserSearchFilter.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserSearchFilter"));
		    	this.emailAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("emailAttributeName"));
		        this.userAccountNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("userAccountNameAttributeName"));
		        this.accountTypeAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("accountTypeAttributeName"));
		        this.applicationSecurityBaseDn.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityBaseDn"));
		        this.applicationSecurityGroupAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityGroupAttributeName"));
		        this.departmentAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("departmentAttributeName"));
		        this.firstNameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("firstNameAttributeName"));
		        this.surnameAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("surnameAttributeName"));
		        this.ldapUserDescriptionAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("ldapUserDescriptionAttributeName"));
		        this.applicationSecurityDescriptionAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("applicationSecurityDescriptionAttributeName"));
		        this.memberofAttributeName.setPropertyDataSource(authenticationMethodItem.getItemProperty("memberofAttributeName"));
			}
		}
		catch (RuntimeException e)
		{
			logger.error("Error occurred trying to load authentication method: ", e);
			
			StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("Error trying to load the authentication method. ", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
		}
    }
 
    private class AuthenticationMethodDropdownValue
    {
    	private String caption;
    	private String value;
		/**
		 * @param caption
		 * @param value
		 */
		public AuthenticationMethodDropdownValue(String caption, String value)
		{
			super();
			this.caption = caption;
			this.value = value;
		}
		/**
		 * @return the caption
		 */
		public String getCaption()
		{
			return caption;
		}
		/**
		 * @return the value
		 */
		public String getValue()
		{
			return value;
		}
    }
}
