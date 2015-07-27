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
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Ikasan Development Team
 *
 */
public class AuthenticationMethodPanel extends Panel
{
	private static final long serialVersionUID = 6005593259860222561L;
	
	private static final String APPLICATION_SECURITY_GROUP_ATTRIBUTE_NAME = "sAMAccountName";
	private static final String LDAP_USER_SEARCH_FILTER = "(sAMAccountName={0})";
	private static final String ACCOUNT_TYPE_ATTRIBUTE_NAME = "accountType";
	private static final String USER_ACCOUNT_NAME_ATTRIBUTE_NAME = "sAMAccountName";
	private static final String EMAIL_ATTRIBUTE_NAME = "mail";
	private static final String FIRST_NAME_ATTRIBUTE_NAME = "givenName";
	private static final String SURNAME_ATTRIBUTE_NAME = "sn";
	private static final String DEPARTMENT_ATTRIBUTE_NAME = "department";
	private static final String LDAP_USER_DESCRIPTION_ATTRIBUTE_NAME = "description";
	private static final String APPLICATION_SECURITY_DESCRIPTION_ATTRIBUTE_NAME = "description";
	private static final String MEMBER_OF_ATTRIBUTE_NAME = "memberOf";
	
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
    private TextField directoryName;
    private TextField ldapServerUrl;
    private TextField ldapBindUserDn;
    private PasswordField ldapBindUserPassword;
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

        Panel securityAdministrationPanel = new Panel();
        securityAdministrationPanel.setStyleName("dashboard");
        securityAdministrationPanel.setWidth("100%");
        securityAdministrationPanel.setHeight("100%");
        
        GridLayout gridLayout = new GridLayout(2, 25);
        gridLayout.setSpacing(true);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);
        gridLayout.setColumnExpandRatio(0, 0.3f);
        gridLayout.setColumnExpandRatio(1, 0.7f);

        authenticationMethodCombo.addItem(LOCAL_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LOCAL_AUTHENTICATION, LOCAL_AUTHENTICATION.getCaption());
        authenticationMethodCombo.addItem(LDAP_LOCAL_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LDAP_LOCAL_AUTHENTICATION, LDAP_LOCAL_AUTHENTICATION.getCaption());
        authenticationMethodCombo.addItem(LDAP_AUTHENTICATION);
        authenticationMethodCombo.setItemCaption(LDAP_AUTHENTICATION, LDAP_AUTHENTICATION.getCaption());
 
        authenticationMethodDropdownValuesMap.put(LOCAL_AUTHENTICATION.getValue(), LOCAL_AUTHENTICATION);
        authenticationMethodDropdownValuesMap.put(LDAP_LOCAL_AUTHENTICATION.getValue(), LDAP_LOCAL_AUTHENTICATION);
        authenticationMethodDropdownValuesMap.put(LDAP_AUTHENTICATION.getValue(), LDAP_AUTHENTICATION);
        
        final Label serverSettings = new Label("Server Settings");
        serverSettings.setStyleName("large-bold");
        
        gridLayout.addComponent(serverSettings, 0, 0);

        final Label directoryNameLabel = new Label("Directory Name:");
        directoryNameLabel.setSizeUndefined();
        this.directoryName = new TextField();
        this.directoryName.setWidth("400px");
        this.directoryName.setRequired(true);
        
        gridLayout.addComponent(directoryNameLabel, 0, 1);
        gridLayout.addComponent(this.directoryName, 1, 1);
        gridLayout.setComponentAlignment(directoryNameLabel, Alignment.MIDDLE_RIGHT);
        

        final Label ldapServerUrlLabel = new Label("LDAP Server URL:");
        ldapServerUrlLabel.setSizeUndefined();
        this.ldapServerUrl = new TextField();
        this.ldapServerUrl.setWidth("400px");
        
        gridLayout.addComponent(ldapServerUrlLabel, 0, 2);
        gridLayout.addComponent(this.ldapServerUrl, 1, 2);
        this.ldapServerUrl.setRequired(true);
        gridLayout.setComponentAlignment(ldapServerUrlLabel, Alignment.MIDDLE_RIGHT);
        
        Label hostnameExample = new Label("Hostname of server running LDAP. Example: ldap://ldap.example.com:389");
        gridLayout.addComponent(hostnameExample, 1, 3);
        
        final Label ldapBindUserDnLabel = new Label("Username:");
        ldapBindUserDnLabel.setSizeUndefined();
        this.ldapBindUserDn = new TextField();
        this.ldapBindUserDn.setWidth("400px");
        this.ldapBindUserDn.setRequired(true);
        
        gridLayout.addComponent(ldapBindUserDnLabel, 0, 4);
        gridLayout.addComponent(this.ldapBindUserDn, 1, 4);
        gridLayout.setComponentAlignment(ldapBindUserDnLabel, Alignment.MIDDLE_RIGHT);
        
        Label usernameExample = new Label("User to log into LDAP. Example: cn=user,DC=domain,DC=name");
        gridLayout.addComponent(usernameExample, 1, 5);
        
        final Label ldapBindUserPasswordLabel = new Label("Password:");
        ldapBindUserPasswordLabel.setSizeUndefined();
        this.ldapBindUserPassword = new PasswordField();
        this.ldapBindUserPassword.setWidth("100px");
        this.ldapBindUserPassword.setRequired(true);
        
        gridLayout.addComponent(ldapBindUserPasswordLabel, 0, 6);
        gridLayout.addComponent(this.ldapBindUserPassword, 1, 6);
        gridLayout.setComponentAlignment(ldapBindUserPasswordLabel, Alignment.MIDDLE_RIGHT);
        
        final Label ldapSchema = new Label("LDAP Schema");
        ldapSchema.setStyleName("large-bold");
        
        gridLayout.addComponent(ldapSchema, 0, 7);
        
        final Label ldapUserSearchDnLabel = new Label("User DN:");
        ldapUserSearchDnLabel.setSizeUndefined();
        this.ldapUserSearchDn = new TextField();
        this.ldapUserSearchDn.setRequired(true);
        this.ldapUserSearchDn.setWidth("400px");
        
        gridLayout.addComponent(ldapUserSearchDnLabel, 0, 8);
        gridLayout.setComponentAlignment(ldapUserSearchDnLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.ldapUserSearchDn, 1, 8);
        
        Label userDnExample = new Label("The base DN to use when searching for users.");
        gridLayout.addComponent(userDnExample, 1, 9);
        
        final Label applicationSecurityBaseDnLabel = new Label("Group DN:");
        applicationSecurityBaseDnLabel.setSizeUndefined();
        this.applicationSecurityBaseDn = new TextField();
        this.applicationSecurityBaseDn.setRequired(true);
        this.applicationSecurityBaseDn.setWidth("400px");
        gridLayout.addComponent(applicationSecurityBaseDnLabel, 0, 10);
        gridLayout.setComponentAlignment(applicationSecurityBaseDnLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.applicationSecurityBaseDn, 1, 10);
        
        Label groupDnExample = new Label("The base DN to use when searching for groups.");
        gridLayout.addComponent(groupDnExample, 1, 11);
        
        final Label ldapAttributes = new Label("LDAP Attributes");
        ldapAttributes.setStyleName("large-bold");
        
        CheckBox checkbox = new CheckBox("Populate default attributes");
        checkbox.setValue(false);
        
        checkbox.addValueChangeListener(new Property.ValueChangeListener() 
        {
            public void valueChange(ValueChangeEvent event)
            {
                boolean value = (Boolean) event.getProperty().getValue();
                
                if(value == true)
                {
                	ldapUserSearchFilter.setValue(LDAP_USER_SEARCH_FILTER);
                	emailAttributeName.setValue(EMAIL_ATTRIBUTE_NAME);
                	userAccountNameAttributeName.setValue(USER_ACCOUNT_NAME_ATTRIBUTE_NAME);
                	accountTypeAttributeName.setValue(ACCOUNT_TYPE_ATTRIBUTE_NAME);
                	firstNameAttributeName.setValue(FIRST_NAME_ATTRIBUTE_NAME);
                	surnameAttributeName.setValue(SURNAME_ATTRIBUTE_NAME);
                	departmentAttributeName.setValue(DEPARTMENT_ATTRIBUTE_NAME);
                	ldapUserDescriptionAttributeName.setValue(LDAP_USER_DESCRIPTION_ATTRIBUTE_NAME);
                	memberofAttributeName.setValue(MEMBER_OF_ATTRIBUTE_NAME);
                	applicationSecurityGroupAttributeName.setValue(APPLICATION_SECURITY_GROUP_ATTRIBUTE_NAME);
                	applicationSecurityDescriptionAttributeName.setValue(APPLICATION_SECURITY_DESCRIPTION_ATTRIBUTE_NAME);
                }
                else
                {
                	ldapUserSearchFilter.setValue("");
                	emailAttributeName.setValue("");
                	userAccountNameAttributeName.setValue("");
                	accountTypeAttributeName.setValue("");
                	firstNameAttributeName.setValue("");
                	surnameAttributeName.setValue("");
                	departmentAttributeName.setValue("");
                	ldapUserDescriptionAttributeName.setValue("");
                	memberofAttributeName.setValue("");
                	applicationSecurityGroupAttributeName.setValue("");
                	applicationSecurityDescriptionAttributeName.setValue("");
                }
            }
        });
        checkbox.setImmediate(true);
        
        gridLayout.addComponent(ldapAttributes, 0, 12);
        gridLayout.addComponent(checkbox, 1, 12);
        
        final Label userSearchFieldLabel = new Label("User Search Filter:");
        userSearchFieldLabel.setSizeUndefined();
        this.ldapUserSearchFilter = new TextField();
        this.ldapUserSearchFilter.setWidth("300px");
        this.ldapUserSearchFilter.setRequired(true);
        
        gridLayout.addComponent(userSearchFieldLabel, 0, 13);
        gridLayout.setComponentAlignment(userSearchFieldLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.ldapUserSearchFilter, 1, 13);

        final Label emailAttributeNameLabel = new Label("Email:");
        emailAttributeNameLabel.setSizeUndefined();
        this.emailAttributeName = new TextField();
        this.emailAttributeName.setWidth("300px");
        this.emailAttributeName.setRequired(true);
        
        gridLayout.addComponent(emailAttributeNameLabel, 0, 14);
        gridLayout.setComponentAlignment(emailAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.emailAttributeName, 1, 14);
        
        final Label userAccountNameAttributeNameLabel = new Label("Account Name:");
        userAccountNameAttributeNameLabel.setSizeUndefined();
        this.userAccountNameAttributeName = new TextField();
        this.userAccountNameAttributeName.setWidth("300px");
        this.userAccountNameAttributeName.setRequired(true);
        
        gridLayout.addComponent(userAccountNameAttributeNameLabel, 0, 15);
        gridLayout.setComponentAlignment(userAccountNameAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.userAccountNameAttributeName, 1, 15);

        final Label accountTypeAttributeNameLabel = new Label("Account Type:");
        accountTypeAttributeNameLabel.setSizeUndefined();
        this.accountTypeAttributeName = new TextField();
        this.accountTypeAttributeName.setWidth("300px");
        this.accountTypeAttributeName.setRequired(true);
        
        gridLayout.addComponent(accountTypeAttributeNameLabel, 0, 16);
        gridLayout.setComponentAlignment(accountTypeAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.accountTypeAttributeName, 1, 16);
        
        final Label firstNameAttributeNameLabel = new Label("First Name:");
        firstNameAttributeNameLabel.setSizeUndefined();
        this.firstNameAttributeName = new TextField();
        this.firstNameAttributeName.setWidth("300px");
        this.firstNameAttributeName.setRequired(true);
        
        gridLayout.addComponent(firstNameAttributeNameLabel, 0, 17);
        gridLayout.setComponentAlignment(firstNameAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.firstNameAttributeName, 1, 17);
        
        final Label surnameAttributeNameLabel = new Label("Surname:");
        surnameAttributeNameLabel.setSizeUndefined();
        this.surnameAttributeName = new TextField();
        this.surnameAttributeName.setWidth("300px");
        this.surnameAttributeName.setRequired(true);
        
        gridLayout.addComponent(surnameAttributeNameLabel, 0, 18);
        gridLayout.setComponentAlignment(surnameAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.surnameAttributeName, 1, 18);
       
        final Label departmentAttributeNameLabel = new Label("User Department:");
        departmentAttributeNameLabel.setSizeUndefined();
        this.departmentAttributeName = new TextField();
        this.departmentAttributeName.setWidth("300px");
        this.departmentAttributeName.setRequired(true);
        
        gridLayout.addComponent(departmentAttributeNameLabel, 0, 19);
        gridLayout.setComponentAlignment(departmentAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.departmentAttributeName, 1, 19);
       
        final Label ldapUserDescriptionAttributeNameLabel = new Label("User Description:");
        ldapUserDescriptionAttributeNameLabel.setSizeUndefined();
        this.ldapUserDescriptionAttributeName = new TextField();
        this.ldapUserDescriptionAttributeName.setWidth("300px");
        this.ldapUserDescriptionAttributeName.setRequired(true);
        
        gridLayout.addComponent(ldapUserDescriptionAttributeNameLabel, 0, 20);
        gridLayout.setComponentAlignment(ldapUserDescriptionAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.ldapUserDescriptionAttributeName, 1, 20);
        
        
        final Label memberOfAttributeNameLabel = new Label("Member Of:");
        memberOfAttributeNameLabel.setSizeUndefined();
        this.memberofAttributeName = new TextField();
        this.memberofAttributeName.setWidth("300px");
        this.memberofAttributeName.setRequired(true);
        
        gridLayout.addComponent(memberOfAttributeNameLabel, 0, 21);
        gridLayout.setComponentAlignment(memberOfAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.memberofAttributeName, 1, 21);
        
        final Label applicationSecurityGroupAttributeNameLabel = new Label("Group Name:");
        applicationSecurityGroupAttributeNameLabel.setSizeUndefined();
        this.applicationSecurityGroupAttributeName = new TextField();
        this.applicationSecurityGroupAttributeName.setWidth("300px");
        this.applicationSecurityGroupAttributeName.setRequired(true);
        
        gridLayout.addComponent(applicationSecurityGroupAttributeNameLabel, 0, 22);
        gridLayout.setComponentAlignment(applicationSecurityGroupAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.applicationSecurityGroupAttributeName, 1, 22);

        final Label applicationSecurityAttributeNameLabel = new Label("Group Description:");
        applicationSecurityAttributeNameLabel.setSizeUndefined();
        this.applicationSecurityDescriptionAttributeName = new TextField();
        this.applicationSecurityDescriptionAttributeName.setWidth("300px");
        this.applicationSecurityDescriptionAttributeName.setRequired(true);
        
        gridLayout.addComponent(applicationSecurityAttributeNameLabel, 0, 23);
        gridLayout.setComponentAlignment(applicationSecurityAttributeNameLabel, Alignment.MIDDLE_RIGHT);
        gridLayout.addComponent(this.applicationSecurityDescriptionAttributeName, 1, 23);

        
        final BeanItem<AuthenticationMethod> authenticationMethodItem = new BeanItem<AuthenticationMethod>(authenticationMethod);
		
        this.directoryName.setPropertyDataSource(authenticationMethodItem.getItemProperty("name"));
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
            		logger.info("saving auth method: " + authenticationMethod);
            		authenticationMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
            		
            		if(authenticationMethod.getOrder() == null)
            		{
            			authenticationMethod.setOrder(securityService.getNumberOfAuthenticationMethods() + 1);
            		}
            		
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
        
                
        GridLayout buttonLayout = new GridLayout(1, 1);
        buttonLayout.setWidth("200px");
        buttonLayout.setHeight("20px");
        buttonLayout.addComponent(saveButton);
        
        gridLayout.addComponent(buttonLayout, 0, 24, 1, 24);

        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.addComponent(gridLayout);
        wrapperLayout.setComponentAlignment(gridLayout, Alignment.TOP_CENTER);
        
        securityAdministrationPanel.setContent(wrapperLayout);        
        layout.addComponent(securityAdministrationPanel);
        this.setContent(layout);
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
