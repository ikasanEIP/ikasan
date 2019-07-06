package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.ikasan.dashboard.security.SecurityConfiguration;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

@ContextConfiguration(classes = {SecurityConfiguration.class})
public class UserDirectoryDialog extends Dialog
{
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
    private static final String USER_FILTER = "(objectclass=user)";
    private static final String GROUP_FILTER = "(objectclass=group)";

    private Logger logger = LoggerFactory.getLogger(UserDirectoryDialog.class);

    @Resource
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
    private TextField userSynchronisationFilter;
    private TextField groupSynchronisationFilter;

    private AuthenticationMethod authenticationMethod;


    /**
     * Constructor
     *
     * @param authenticationMethod
     */
    public UserDirectoryDialog(SecurityService securityService, AuthenticationMethod authenticationMethod)
    {
        super();

        this.securityService = securityService;
        if(this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.authenticationMethod = authenticationMethod;
        if(this.authenticationMethod == null)
        {
            throw new IllegalArgumentException("authenticationMethod cannot be null!");
        }

        init();
    }

    /**
     * Constructor
     */
    public UserDirectoryDialog()
    {
        super();

        init();
    }

    protected void init()
    {

        final H2 configureUserDirectories = new H2("Configure User Directories");
        this.add(configureUserDirectories);


        FormLayout formLayout = new FormLayout();

        final H3 serverSettings = new H3("Server Settings");

        formLayout.add(serverSettings);
        formLayout.add(new Div());

        this.directoryName = new TextField();
        this.directoryName.setWidth("600px");
        this.directoryName.setRequired(true);
        this.directoryName.setLabel("Directory Name");

        Div directoryNameDiv = new Div();
        directoryNameDiv.add(this.directoryName);

        formLayout.add(directoryNameDiv, new Div());

        this.ldapServerUrl = new TextField();
        this.ldapServerUrl.setWidth("600px");
        this.ldapServerUrl.setRequired(true);
        this.ldapServerUrl.setLabel("LDAP Server URL");
        H6 hostnameExample = new H6("Hostname of server running LDAP. Example: ldap://ldap.example.com:389");

        Div ldapServerUrlDiv = new Div();
        ldapServerUrlDiv.add(ldapServerUrl, hostnameExample);
        formLayout.add(ldapServerUrlDiv, new Div());

        this.ldapBindUserDn = new TextField();
        this.ldapBindUserDn.setWidth("600px");
        this.ldapBindUserDn.setRequired(true);
        this.ldapBindUserDn.setLabel("Username");

        H6 usernameExample = new H6("User to log into LDAP. Example: cn=user,DC=domain,DC=name");

        Div ldapBindUserDnDiv = new Div();
        ldapBindUserDnDiv.add(this.ldapBindUserDn, usernameExample);

        formLayout.add(ldapBindUserDnDiv, new Div());

        this.ldapBindUserPassword = new PasswordField();
        this.ldapBindUserPassword.setWidth("300px");
        this.ldapBindUserPassword.setRequired(true);
        this.ldapBindUserPassword.setLabel("Password");

        formLayout.add(new Div(this.ldapBindUserPassword));
        formLayout.add(new Div());

        final H3 ldapSchema = new H3("LDAP Schema");
        formLayout.add(ldapSchema, new Div());

        this.ldapUserSearchDn = new TextField();
        this.ldapUserSearchDn.setRequired(true);
        this.ldapUserSearchDn.setWidth("600px");
        this.ldapUserSearchDn.setLabel("User DN");

        H6 userDnExample = new H6("The base DN to use when searching for users.");

        Div ldapUserSearchDnDiv = new Div();
        ldapUserSearchDnDiv.add(this.ldapUserSearchDn, userDnExample);

        formLayout.add(ldapUserSearchDnDiv, new Div());

        this.applicationSecurityBaseDn = new TextField();
        this.applicationSecurityBaseDn.setRequired(true);
        this.applicationSecurityBaseDn.setWidth("600px");
        this.applicationSecurityBaseDn.setLabel("Group DN");

        H6 groupDnExample = new H6("The base DN to use when searching for groups.");

        Div applicationSecurityBaseDnDiv = new Div();
        applicationSecurityBaseDnDiv.add(this.applicationSecurityBaseDn, groupDnExample);

        formLayout.add(applicationSecurityBaseDnDiv, new Div());

        final H3 ldapAttributes = new H3("LDAP Attributes");
        formLayout.add(ldapAttributes, new Div());

        Checkbox checkbox = new Checkbox("Populate default attributes");
        checkbox.setValue(false);

        formLayout.add(checkbox, new Div());

        checkbox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>) checkboxBooleanComponentValueChangeEvent ->
        {
            boolean value = checkboxBooleanComponentValueChangeEvent.getValue();

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
                userSynchronisationFilter.setValue(USER_FILTER);
                groupSynchronisationFilter.setValue(GROUP_FILTER);
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
                userSynchronisationFilter.setValue("");
                groupSynchronisationFilter.setValue("");
            }
        });

        this.userSynchronisationFilter = new TextField();
        this.userSynchronisationFilter.setWidth("300px");
        this.userSynchronisationFilter.setRequired(true);
        this.userSynchronisationFilter.setLabel("User Synchronisation Filter");
        formLayout.add(new Div(this.userSynchronisationFilter));

        this.groupSynchronisationFilter = new TextField();
        this.groupSynchronisationFilter.setWidth("300px");
        this.groupSynchronisationFilter.setRequired(true);
        this.groupSynchronisationFilter.setLabel("Group Synchronisation Filter");
        formLayout.add(new Div(this.groupSynchronisationFilter));

        this.ldapUserSearchFilter = new TextField();
        this.ldapUserSearchFilter.setWidth("300px");
        this.ldapUserSearchFilter.setRequired(true);
        this.ldapUserSearchFilter.setLabel("Group Synchronisation Filter");
        formLayout.add(new Div(this.ldapUserSearchFilter));

        this.emailAttributeName = new TextField();
        this.emailAttributeName.setWidth("300px");
        this.emailAttributeName.setRequired(true);
        this.emailAttributeName.setLabel("Email");
        formLayout.add(new Div(this.emailAttributeName));

        this.userAccountNameAttributeName = new TextField();
        this.userAccountNameAttributeName.setWidth("300px");
        this.userAccountNameAttributeName.setRequired(true);
        this.userAccountNameAttributeName.setLabel("Account Name");
        formLayout.add(new Div(this.userAccountNameAttributeName));

        this.accountTypeAttributeName = new TextField();
        this.accountTypeAttributeName.setWidth("300px");
        this.accountTypeAttributeName.setRequired(true);
        this.accountTypeAttributeName.setLabel("Account Type");
        formLayout.add(new Div(this.accountTypeAttributeName));

        this.firstNameAttributeName = new TextField();
        this.firstNameAttributeName.setWidth("300px");
        this.firstNameAttributeName.setLabel("First Name");
        this.firstNameAttributeName.setRequired(true);
        formLayout.add(new Div(this.firstNameAttributeName));

        this.surnameAttributeName = new TextField();
        this.surnameAttributeName.setWidth("300px");
        this.surnameAttributeName.setRequired(true);
        this.surnameAttributeName.setLabel("Surname");
        formLayout.add(new Div(this.surnameAttributeName));

        this.departmentAttributeName = new TextField();
        this.departmentAttributeName.setWidth("300px");
        this.departmentAttributeName.setRequired(true);
        this.departmentAttributeName.setLabel("User Department");
        formLayout.add(new Div(this.departmentAttributeName));

        this.ldapUserDescriptionAttributeName = new TextField();
        this.ldapUserDescriptionAttributeName.setWidth("300px");
        this.ldapUserDescriptionAttributeName.setRequired(true);
        this.ldapUserDescriptionAttributeName.setLabel("User Description");
        formLayout.add(new Div(this.ldapUserDescriptionAttributeName));

        this.memberofAttributeName = new TextField();
        this.memberofAttributeName.setWidth("300px");
        this.memberofAttributeName.setRequired(true);
        this.memberofAttributeName.setLabel("Member Of");
        formLayout.add(new Div(this.memberofAttributeName));

        this.applicationSecurityGroupAttributeName = new TextField();
        this.applicationSecurityGroupAttributeName.setWidth("300px");
        this.applicationSecurityGroupAttributeName.setRequired(true);
        this.applicationSecurityGroupAttributeName.setLabel("Group Name");
        formLayout.add(new Div(this.applicationSecurityGroupAttributeName));

        this.applicationSecurityDescriptionAttributeName = new TextField();
        this.applicationSecurityDescriptionAttributeName.setWidth("300px");
        this.applicationSecurityDescriptionAttributeName.setRequired(true);
        this.applicationSecurityDescriptionAttributeName.setLabel("Group Description");
        formLayout.add(new Div(this.applicationSecurityDescriptionAttributeName), new Div());


        Binder<AuthenticationMethod> binder = this.setupBinderAndValidation();

        Button save = new Button("Save");
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            this.authenticationMethod = new AuthenticationMethod();

            if(binder.writeBeanIfValid(this.authenticationMethod))
            {
                try
                {
                    logger.debug("saving auth method: " + authenticationMethod);
                    authenticationMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);

                    if(authenticationMethod.getOrder() == null)
                    {
                        authenticationMethod.setOrder(securityService.getNumberOfAuthenticationMethods() + 1);
                    }

                    securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
                }
                catch(RuntimeException e)
                {
                    logger.error("An error occurred saving an authentication method", e);

                    Notification.show("Error trying to save the authentication method!" + e.getMessage());

                    return;
                }

                Notification.show("Saved", 3000, Notification.Position.MIDDLE);
                close();
            }
            else
            {
                binder.validate();
                Notification.show("Please check forms for errors", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> close());

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);
        save.getStyle().set("marginRight", "10px");

        formLayout.add(actions);

        this.add(formLayout);
    }

    /**
     * Helper method to create the form binder and define validation.
     *
     * @return the form binder.
     */
    protected Binder<AuthenticationMethod> setupBinderAndValidation()
    {
        Binder<AuthenticationMethod> binder = new Binder<>();

        binder.forField(this.directoryName)
            .withValidator(new StringLengthValidator(
                "Please add the directory name!", 1, null))
            .bind(AuthenticationMethod::getName, AuthenticationMethod::setName);

        binder.forField(this.accountTypeAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the account type attribute name!", 1, null))
            .bind(AuthenticationMethod::getAccountTypeAttributeName, AuthenticationMethod::setAccountTypeAttributeName);

        binder.forField(this.applicationSecurityBaseDn)
            .withValidator(new StringLengthValidator(
                "Please add the group dn!", 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityBaseDn, AuthenticationMethod::setApplicationSecurityBaseDn);

        binder.forField(this.applicationSecurityDescriptionAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the group description attribute name!", 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityDescriptionAttributeName, AuthenticationMethod::setApplicationSecurityDescriptionAttributeName);

        binder.forField(this.applicationSecurityGroupAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the group name attribute name!", 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityGroupAttributeName, AuthenticationMethod::setApplicationSecurityGroupAttributeName);

        binder.forField(this.departmentAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the user department attribute name!", 1, null))
            .bind(AuthenticationMethod::getDepartmentAttributeName, AuthenticationMethod::setDepartmentAttributeName);

        binder.forField(this.emailAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the email attribute name!", 1, null))
            .bind(AuthenticationMethod::getEmailAttributeName, AuthenticationMethod::setEmailAttributeName);

        binder.forField(this.firstNameAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the first name attribute name!", 1, null))
            .bind(AuthenticationMethod::getFirstNameAttributeName, AuthenticationMethod::setFirstNameAttributeName);

        binder.forField(this.groupSynchronisationFilter)
            .withValidator(new StringLengthValidator(
                "Please add the group synchronisation filter!", 1, null))
            .bind(AuthenticationMethod::getGroupSynchronisationFilter, AuthenticationMethod::setGroupSynchronisationFilter);

        binder.forField(this.ldapBindUserDn)
            .withValidator(new StringLengthValidator(
                "Please add the ldap username!", 1, null))
            .bind(AuthenticationMethod::getLdapBindUserDn, AuthenticationMethod::setLdapBindUserDn);

        binder.forField(this.ldapBindUserPassword)
            .withValidator(new StringLengthValidator(
                "Please add the ldap password!", 1, null))
            .bind(AuthenticationMethod::getLdapBindUserPassword, AuthenticationMethod::setLdapBindUserPassword);

        binder.forField(this.ldapServerUrl)
            .withValidator(new StringLengthValidator(
                "Please add the ldap server url!", 1, null))
            .bind(AuthenticationMethod::getLdapServerUrl, AuthenticationMethod::setLdapServerUrl);

        binder.forField(this.ldapUserDescriptionAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the description attribute name!", 1, null))
            .bind(AuthenticationMethod::getLdapUserDescriptionAttributeName, AuthenticationMethod::setLdapUserDescriptionAttributeName);

        binder.forField(this.ldapUserSearchDn)
            .withValidator(new StringLengthValidator(
                "Please add the user dn!", 1, null))
            .bind(AuthenticationMethod::getLdapUserSearchBaseDn, AuthenticationMethod::setLdapUserSearchBaseDn);

        binder.forField(this.ldapUserSearchFilter)
            .withValidator(new StringLengthValidator(
                "Please add the user synchronisation filter!", 1, null))
            .bind(AuthenticationMethod::getLdapUserSearchFilter, AuthenticationMethod::setLdapUserSearchFilter);

        binder.forField(this.memberofAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the member of attribute name!", 1, null))
            .bind(AuthenticationMethod::getMemberofAttributeName, AuthenticationMethod::setMemberofAttributeName);

        binder.forField(this.surnameAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the surname attribute name!", 1, null))
            .bind(AuthenticationMethod::getSurnameAttributeName, AuthenticationMethod::setSurnameAttributeName);

        binder.forField(this.userAccountNameAttributeName)
            .withValidator(new StringLengthValidator(
                "Please add the user account attribute name!", 1, null))
            .bind(AuthenticationMethod::getUserAccountNameAttributeName, AuthenticationMethod::setUserAccountNameAttributeName);

        binder.forField(this.userSynchronisationFilter)
            .withValidator(new StringLengthValidator(
                "Please add the user synchronisation filter!", 1, null))
            .bind(AuthenticationMethod::getUserSynchronisationFilter, AuthenticationMethod::setUserSynchronisationFilter);

        return binder;
    }

}
