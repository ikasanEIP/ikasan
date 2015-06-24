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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.springframework.transaction.UnexpectedRollbackException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Ikasan Development Team
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
    
    private AuthenticationMethod authenticationMethod;
    private ComboBox authenticationMethodCombo = new ComboBox();
    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private LdapService ldapService;
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public AuthenticationMethodPanel(AuthenticationMethod authenticationMethod, SecurityService securityService,
    		AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory,
    		LdapService ldapService)
    {
        super();
       
        this.authenticationMethod = authenticationMethod;
        if(this.authenticationMethod == null)
        {
        	throw new IllegalArgumentException("authenticationMethod cannot be null!");
        }
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

        Panel securityAdministrationPanel = new Panel("Authentication Configuration");
        securityAdministrationPanel.setWidth("100%");
        securityAdministrationPanel.setHeight("100%");
        
        GridLayout gridLayout = new GridLayout(2, 18);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);
        gridLayout.setColumnExpandRatio(0, 0.3f);
        gridLayout.setColumnExpandRatio(1, 0.7f);

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

        final Label ldapServerUrlLabel = new Label("LDAP Server URL");
        this.ldapServerUrl = new TextField();
        this.ldapServerUrl.setWidth("70%");
        
        gridLayout.addComponent(ldapServerUrlLabel, 0, 1);
        gridLayout.addComponent(this.ldapServerUrl, 1, 1);
        
        final Label ldapBindUserDnLabel = new Label("LDAP Bind User DN");
        this.ldapBindUserDn = new TextField();
        this.ldapBindUserDn.setWidth("70%");
        
        gridLayout.addComponent(ldapBindUserDnLabel, 0, 2);
        gridLayout.addComponent(this.ldapBindUserDn, 1, 2);
        
        final Label ldapBindUserPasswordLabel = new Label("LDAP Bind User Password");
        this.ldapBindUserPassword = new TextField();
        this.ldapBindUserPassword.setWidth("70%");
        
        gridLayout.addComponent(ldapBindUserPasswordLabel, 0, 3);
        gridLayout.addComponent(this.ldapBindUserPassword, 1, 3);
        
        final Label ldapUserSearchDnLabel = new Label("LDAP User Search DN");
        this.ldapUserSearchDn = new TextField();
        this.ldapUserSearchDn.setWidth("70%");
        
        gridLayout.addComponent(ldapUserSearchDnLabel, 0, 4);
        gridLayout.addComponent(this.ldapUserSearchDn, 1, 4);
        
        final Label ldapUserSearchFilterLabel = new Label("LDAP User Search Filter");
        this.ldapUserSearchFilter = new TextField();
        this.ldapUserSearchFilter.setWidth("70%");

        gridLayout.addComponent(ldapUserSearchFilterLabel, 0, 5);
        gridLayout.addComponent(this.ldapUserSearchFilter, 1, 5);

        final Label emailAttributeNameLabel = new Label("LDAP User Email Attribute Name");
        this.emailAttributeName = new TextField();
        this.emailAttributeName.setWidth("70%");

        gridLayout.addComponent(emailAttributeNameLabel, 0, 6);
        gridLayout.addComponent(this.emailAttributeName, 1, 6);
        
        final Label userAccountNameAttributeNameLabel = new Label("LDAP User Account Name Attribute Name");
        this.userAccountNameAttributeName = new TextField();
        this.userAccountNameAttributeName.setWidth("70%");

        gridLayout.addComponent(userAccountNameAttributeNameLabel, 0, 7);
        gridLayout.addComponent(this.userAccountNameAttributeName, 1, 7);

        final Label accountTypeAttributeNameLabel = new Label("LDAP User Account Type Attribute Name");
        this.accountTypeAttributeName = new TextField();
        this.accountTypeAttributeName.setWidth("70%");

        gridLayout.addComponent(accountTypeAttributeNameLabel, 0, 8);
        gridLayout.addComponent(this.accountTypeAttributeName, 1, 8);
        
        final Label firstNameAttributeNameLabel = new Label("LDAP User First Name Attribute Name");
        this.firstNameAttributeName = new TextField();
        this.firstNameAttributeName.setWidth("70%");

        gridLayout.addComponent(firstNameAttributeNameLabel, 0, 9);
        gridLayout.addComponent(this.firstNameAttributeName, 1, 9);
        
        final Label surnameAttributeNameLabel = new Label("LDAP User Surname Attribute Name");
        this.surnameAttributeName = new TextField();
        this.surnameAttributeName.setWidth("70%");

        gridLayout.addComponent(surnameAttributeNameLabel, 0, 10);
        gridLayout.addComponent(this.surnameAttributeName, 1, 10);

        final Label departmentAttributeNameLabel = new Label("LDAP User Department Attribute Name");
        this.departmentAttributeName = new TextField();
        this.departmentAttributeName.setWidth("70%");
        
        gridLayout.addComponent(departmentAttributeNameLabel, 0, 11);
        gridLayout.addComponent(this.departmentAttributeName, 1, 11);
        
        final Label ldapUserDescriptionAttributeNameLabel = new Label("LDAP User Description Attribute Name");
        this.ldapUserDescriptionAttributeName = new TextField();
        this.ldapUserDescriptionAttributeName.setWidth("70%");

        gridLayout.addComponent(ldapUserDescriptionAttributeNameLabel, 0, 12);
        gridLayout.addComponent(this.ldapUserDescriptionAttributeName, 1, 12);
        
        final Label memberOfAttributeNameLabel = new Label("LDAP Member Of Attribute Name");
        this.memberofAttributeName = new TextField();
        this.memberofAttributeName.setWidth("70%");

        gridLayout.addComponent(memberOfAttributeNameLabel, 0, 13);
        gridLayout.addComponent(this.memberofAttributeName, 1, 13);

        final Label applicationSecurityBaseDnLabel = new Label("Application Security Base DN");
        this.applicationSecurityBaseDn = new TextField();
        this.applicationSecurityBaseDn.setWidth("70%");

        gridLayout.addComponent(applicationSecurityBaseDnLabel, 0, 14);
        gridLayout.addComponent(this.applicationSecurityBaseDn, 1, 14);
        
        final Label applicationSecurityGroupAttributeNameLabel = new Label("Application Security Group Attribute Name");
        this.applicationSecurityGroupAttributeName = new TextField();
        this.applicationSecurityGroupAttributeName.setWidth("70%");

        gridLayout.addComponent(applicationSecurityGroupAttributeNameLabel, 0, 15);
        gridLayout.addComponent(this.applicationSecurityGroupAttributeName, 1, 15);
        
        final Label applicationSecurityAttributeNameLabel = new Label("Application Security Description Attribute Name");
        this.applicationSecurityDescriptionAttributeName = new TextField();
        this.applicationSecurityDescriptionAttributeName.setWidth("70%");

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
        
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		securityService.deleteAuthenticationMethod(authenticationMethod);
            		
            		UI.getCurrent().getNavigator().navigateTo("authenticationMethodView");
            	}
            	catch(RuntimeException e)
            	{
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

        final Button testConnectionButton = new Button("Test Connection");
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
        
        final Button sychronizeButton = new Button("Synchronize");
        sychronizeButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		ldapService.synchronize(authenticationMethod);
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
        
        this.authenticationMethodCombo.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                   AuthenticationMethodDropdownValue value 
                   	= (AuthenticationMethodDropdownValue)event.getProperty().getValue();
                   if((value.getCaption()).equals(LOCAL_AUTHENTICATION.getCaption()))
                   {
                	   ldapServerUrlLabel.setVisible(false);
                       ldapServerUrl.setVisible(false);
                       
                       ldapBindUserDnLabel.setVisible(false);
                       ldapBindUserDn.setVisible(false);
                       
                       ldapBindUserPasswordLabel.setVisible(false);
                       ldapBindUserPassword.setVisible(false);
                       
                       ldapUserSearchDnLabel.setVisible(false);
                       ldapUserSearchDn.setVisible(false);
                       
                       ldapUserSearchFilterLabel.setVisible(false);
                       ldapUserSearchFilter.setVisible(false);

                       emailAttributeNameLabel.setVisible(false);
                       emailAttributeName.setVisible(false);

                       userAccountNameAttributeNameLabel.setVisible(false);
                       userAccountNameAttributeName.setVisible(false);
                       
                       accountTypeAttributeNameLabel.setVisible(false);
                       accountTypeAttributeName.setVisible(false);
                   
                       firstNameAttributeNameLabel.setVisible(false);
                       firstNameAttributeName.setVisible(false);

                       surnameAttributeNameLabel.setVisible(false);
                       surnameAttributeName.setVisible(false);

                       departmentAttributeNameLabel.setVisible(false);
                       departmentAttributeName.setVisible(false);
                       
                       ldapUserDescriptionAttributeNameLabel.setVisible(false);
                       ldapUserDescriptionAttributeName.setVisible(false);
                       
                       memberOfAttributeNameLabel.setVisible(false);
                       memberofAttributeName.setVisible(false);

                       applicationSecurityBaseDnLabel.setVisible(false);
                       applicationSecurityBaseDn.setVisible(false);
                       
                       applicationSecurityGroupAttributeNameLabel.setVisible(false);
                       applicationSecurityGroupAttributeName.setVisible(false);
                       
                       applicationSecurityAttributeNameLabel.setVisible(false);
                       applicationSecurityDescriptionAttributeName.setVisible(false);
                       
                       testConnectionButton.setVisible(false);
                       sychronizeButton.setVisible(false);
                   }
                   else
                   {
                	   ldapServerUrlLabel.setVisible(true);
                       ldapServerUrl.setVisible(true);
                       
                       ldapBindUserDnLabel.setVisible(true);
                       ldapBindUserDn.setVisible(true);
                       
                       ldapBindUserPasswordLabel.setVisible(true);
                       ldapBindUserPassword.setVisible(true);
                       
                       ldapUserSearchDnLabel.setVisible(true);
                       ldapUserSearchDn.setVisible(true);
                       
                       ldapUserSearchFilterLabel.setVisible(true);
                       ldapUserSearchFilter.setVisible(true);

                       emailAttributeNameLabel.setVisible(true);
                       emailAttributeName.setVisible(true);

                       userAccountNameAttributeNameLabel.setVisible(true);
                       userAccountNameAttributeName.setVisible(true);
                       
                       accountTypeAttributeNameLabel.setVisible(true);

                       firstNameAttributeNameLabel.setVisible(true);
                       firstNameAttributeName.setVisible(true);

                       surnameAttributeNameLabel.setVisible(true);
                       surnameAttributeName.setVisible(true);

                       departmentAttributeNameLabel.setVisible(true);
                       departmentAttributeName.setVisible(true);
                       
                       ldapUserDescriptionAttributeNameLabel.setVisible(true);
                       ldapUserDescriptionAttributeName.setVisible(true);
                       
                       memberOfAttributeNameLabel.setVisible(true);
                       memberofAttributeName.setVisible(true);

                       applicationSecurityBaseDnLabel.setVisible(true);
                       applicationSecurityBaseDn.setVisible(true);
                       
                       applicationSecurityGroupAttributeNameLabel.setVisible(true);
                       applicationSecurityGroupAttributeName.setVisible(true);
                       
                       applicationSecurityAttributeNameLabel.setVisible(true);
                       applicationSecurityDescriptionAttributeName.setVisible(true);
                       
                       testConnectionButton.setVisible(true);
                       sychronizeButton.setVisible(true);
                   }
                }
            }
        });

        GridLayout buttonLayout = new GridLayout(4, 1);
        buttonLayout.setWidth("200px");
        buttonLayout.setHeight("20px");
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(deleteButton);
        buttonLayout.addComponent(sychronizeButton);
        buttonLayout.addComponent(testConnectionButton);
        
        gridLayout.addComponent(buttonLayout, 0, 17, 1, 17);

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
    		AuthenticationMethod loadedAuthenticationMethod = null;
    		
    		if(this.authenticationMethod != null)
    		{
    			loadedAuthenticationMethod= securityService.getAuthenticationMethod(authenticationMethod.getId());
    		}
    		
    		List<AuthenticationMethod> loadedAuthenticationMethods = securityService.getAuthenticationMethods();
    		
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
