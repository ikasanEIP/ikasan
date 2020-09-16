package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.ikasan.dashboard.security.SecurityConfiguration;
import org.ikasan.dashboard.ui.administration.schedule.LdapDirectorySynchronisationConfiguration;
import org.ikasan.dashboard.ui.administration.schedule.LdapDirectorySynchronisationService;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.SecurityService;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

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

    private SecurityService securityService;

    private LdapDirectorySynchronisationService ldapDirectorySynchronisationService;

    private Checkbox isScheduled;
    private TextField synchronisationSchedule;
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
    public UserDirectoryDialog(SecurityService securityService, AuthenticationMethod authenticationMethod,
                               LdapDirectorySynchronisationService ldapDirectorySynchronisationService)
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
        this.ldapDirectorySynchronisationService = ldapDirectorySynchronisationService;
        if(this.ldapDirectorySynchronisationService == null)
        {
            throw new IllegalArgumentException("ldapDirectorySynchronisationService cannot be null!");
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

        final H2 configureUserDirectories = new H2(getTranslation("label.configure-user-directory", UI.getCurrent().getLocale()));
        this.add(configureUserDirectories);


        FormLayout formLayout = new FormLayout();

        this.isScheduled  = new Checkbox(getTranslation("label.user-directory-is-synchronisation-scheduled", UI.getCurrent().getLocale()));

        formLayout.add(isScheduled);
        formLayout.add(new Div());

        this.synchronisationSchedule = new TextField(getTranslation("label.user-directory-synchronisation-schedule-cron-expression", UI.getCurrent().getLocale()));
        this.synchronisationSchedule.setWidth("600px");
        this.synchronisationSchedule.setVisible(this.isScheduled.getValue());

        Div synchronisationScheduleDiv = new Div();
        synchronisationScheduleDiv.add(this.synchronisationSchedule);

        this.isScheduled.addValueChangeListener(checkboxClickEvent -> {
            this.synchronisationSchedule.setVisible(checkboxClickEvent.getValue());
        });

        formLayout.add(synchronisationScheduleDiv);
        formLayout.add(new Div());

        final H3 serverSettings = new H3(getTranslation("label.user-directory-server-settings", UI.getCurrent().getLocale(), null));

        formLayout.add(serverSettings);
        formLayout.add(new Div());

        this.directoryName = new TextField(getTranslation("text-field.user-directory-name", UI.getCurrent().getLocale(), null));
        this.directoryName.setWidth("600px");
        this.directoryName.setRequired(true);

        Div directoryNameDiv = new Div();
        directoryNameDiv.add(this.directoryName);

        formLayout.add(directoryNameDiv);

        this.ldapServerUrl = new TextField(getTranslation("text-field.user-directory-server-url", UI.getCurrent().getLocale(), null));
        this.ldapServerUrl.setWidth("600px");
        this.ldapServerUrl.setRequired(true);
        H6 hostnameExample = new H6(getTranslation("label.user-directory-server-url-help", UI.getCurrent().getLocale(), null));

        Div ldapServerUrlDiv = new Div();
        ldapServerUrlDiv.add(ldapServerUrl, hostnameExample);
        formLayout.add(ldapServerUrlDiv);

        this.ldapBindUserDn = new TextField(getTranslation("text-field.user-directory-username", UI.getCurrent().getLocale(), null));
        this.ldapBindUserDn.setWidth("600px");
        this.ldapBindUserDn.setRequired(true);

        H6 usernameExample = new H6(getTranslation("label.user-directory-username-help", UI.getCurrent().getLocale(), null));

        Div ldapBindUserDnDiv = new Div();
        ldapBindUserDnDiv.add(this.ldapBindUserDn, usernameExample);

        formLayout.add(ldapBindUserDnDiv);

        this.ldapBindUserPassword = new PasswordField(getTranslation("text-field.user-directory-password", UI.getCurrent().getLocale(), null));
        this.ldapBindUserPassword.setWidth("300px");
        this.ldapBindUserPassword.setRequired(true);

        formLayout.add(new Div(this.ldapBindUserPassword));

        final H3 ldapSchema = new H3(getTranslation("label.user-directory-ldap-schema", UI.getCurrent().getLocale(), null));
        formLayout.add(ldapSchema, new Div());

        this.ldapUserSearchDn = new TextField(getTranslation("text-field.user-directory-user-dn", UI.getCurrent().getLocale(), null));
        this.ldapUserSearchDn.setRequired(true);
        this.ldapUserSearchDn.setWidth("600px");

        H6 userDnExample = new H6(getTranslation("label.user-directory-base-dn-help", UI.getCurrent().getLocale(), null));

        Div ldapUserSearchDnDiv = new Div();
        ldapUserSearchDnDiv.add(this.ldapUserSearchDn, userDnExample);

        formLayout.add(ldapUserSearchDnDiv);

        this.applicationSecurityBaseDn = new TextField(getTranslation("text-field.user-directory-group-dn", UI.getCurrent().getLocale(), null));
        this.applicationSecurityBaseDn.setRequired(true);
        this.applicationSecurityBaseDn.setWidth("600px");

        H6 groupDnExample = new H6(getTranslation("label.user-directory-group-dn-help", UI.getCurrent().getLocale(), null));

        Div applicationSecurityBaseDnDiv = new Div();
        applicationSecurityBaseDnDiv.add(this.applicationSecurityBaseDn, groupDnExample);

        formLayout.add(applicationSecurityBaseDnDiv);

        final H3 ldapAttributes = new H3(getTranslation("label.user-directory-group-ldap-attributes", UI.getCurrent().getLocale(), null));
        formLayout.add(ldapAttributes, new Div());

        Checkbox checkbox = new Checkbox(getTranslation("label.user-directory-group-populate-ldap-attributes", UI.getCurrent().getLocale(), null));
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

        this.userSynchronisationFilter = new TextField(getTranslation("text-field.user-directory-user-synchronisation-filter", UI.getCurrent().getLocale(), null));
        this.userSynchronisationFilter.setWidth("300px");
        this.userSynchronisationFilter.setRequired(true);
        formLayout.add(new Div(this.userSynchronisationFilter));

        this.groupSynchronisationFilter = new TextField(getTranslation("text-field.user-directory-group-synchronisation-filter", UI.getCurrent().getLocale(), null));
        this.groupSynchronisationFilter.setWidth("300px");
        this.groupSynchronisationFilter.setRequired(true);
        formLayout.add(new Div(this.groupSynchronisationFilter));

        this.ldapUserSearchFilter = new TextField(getTranslation("text-field.user-directory-ldap-user-search-filter", UI.getCurrent().getLocale(), null));
        this.ldapUserSearchFilter.setWidth("300px");
        this.ldapUserSearchFilter.setRequired(true);
        formLayout.add(new Div(this.ldapUserSearchFilter));

        this.emailAttributeName = new TextField(getTranslation("text-field.user-directory-email", UI.getCurrent().getLocale(), null));
        this.emailAttributeName.setWidth("300px");
        this.emailAttributeName.setRequired(true);
        formLayout.add(new Div(this.emailAttributeName));

        this.userAccountNameAttributeName = new TextField(getTranslation("text-field.user-directory-account-name", UI.getCurrent().getLocale(), null));
        this.userAccountNameAttributeName.setWidth("300px");
        this.userAccountNameAttributeName.setRequired(true);
        formLayout.add(new Div(this.userAccountNameAttributeName));

        this.accountTypeAttributeName = new TextField(getTranslation("text-field.user-directory-account-type", UI.getCurrent().getLocale(), null));
        this.accountTypeAttributeName.setWidth("300px");
        this.accountTypeAttributeName.setRequired(true);
        formLayout.add(new Div(this.accountTypeAttributeName));

        this.firstNameAttributeName = new TextField(getTranslation("text-field.user-directory-first-name", UI.getCurrent().getLocale(), null));
        this.firstNameAttributeName.setWidth("300px");
        this.firstNameAttributeName.setRequired(true);
        formLayout.add(new Div(this.firstNameAttributeName));

        this.surnameAttributeName = new TextField(getTranslation("text-field.user-directory-surname", UI.getCurrent().getLocale(), null));
        this.surnameAttributeName.setWidth("300px");
        this.surnameAttributeName.setRequired(true);
        formLayout.add(new Div(this.surnameAttributeName));

        this.departmentAttributeName = new TextField(getTranslation("text-field.user-directory-user-department", UI.getCurrent().getLocale(), null));
        this.departmentAttributeName.setWidth("300px");
        this.departmentAttributeName.setRequired(true);
        formLayout.add(new Div(this.departmentAttributeName));

        this.ldapUserDescriptionAttributeName = new TextField(getTranslation("text-field.user-directory-user-description", UI.getCurrent().getLocale(), null));
        this.ldapUserDescriptionAttributeName.setWidth("300px");
        this.ldapUserDescriptionAttributeName.setRequired(true);
        formLayout.add(new Div(this.ldapUserDescriptionAttributeName));

        this.memberofAttributeName = new TextField(getTranslation("text-field.user-directory-member-of", UI.getCurrent().getLocale(), null));
        this.memberofAttributeName.setWidth("300px");
        this.memberofAttributeName.setRequired(true);
        formLayout.add(new Div(this.memberofAttributeName));

        this.applicationSecurityGroupAttributeName = new TextField(getTranslation("text-field.user-directory-group-name", UI.getCurrent().getLocale(), null));
        this.applicationSecurityGroupAttributeName.setWidth("300px");
        this.applicationSecurityGroupAttributeName.setRequired(true);
        formLayout.add(new Div(this.applicationSecurityGroupAttributeName));

        this.applicationSecurityDescriptionAttributeName = new TextField(getTranslation("text-field.user-directory-group-description", UI.getCurrent().getLocale(), null));
        this.applicationSecurityDescriptionAttributeName.setWidth("300px");
        this.applicationSecurityDescriptionAttributeName.setRequired(true);
        formLayout.add(new Div(this.applicationSecurityDescriptionAttributeName), new Div());


        Binder<AuthenticationMethod> binder = this.setupBinderAndValidation();

        Button save = new Button(getTranslation("button.save", UI.getCurrent().getLocale(), null));
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
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
                    this.ldapDirectorySynchronisationService.scheduleJobs();
                }
                catch(RuntimeException e)
                {
                    logger.error("An error occurred saving an authentication method", e);

                    NotificationHelper.showErrorNotification(String.format(getTranslation("message.error-saving-auth-method", UI.getCurrent().getLocale(), null), e.getMessage()));

                    return;
                }

                NotificationHelper.showUserNotification(getTranslation("message.saved", UI.getCurrent().getLocale(), null));
                close();
            }
            else
            {
                binder.validate();
                NotificationHelper.showErrorNotification(getTranslation("message.auth-method-check-form", UI.getCurrent().getLocale(), null));
            }
        });

        Button cancel = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale(), null));
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> close());

        HorizontalLayout actions = new HorizontalLayout();
        actions.setMargin(true);
        actions.setSpacing(true);
        actions.add(save, cancel);
        save.getStyle().set("marginRight", "10px");

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.add(actions);
        buttonLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, actions);

        this.add(formLayout, buttonLayout);
    }

    /**
     * Helper method to create the form binder and define validation.
     *
     * @return the form binder.
     */
    protected Binder<AuthenticationMethod> setupBinderAndValidation()
    {
        Binder<AuthenticationMethod> binder = new Binder<>();

        binder.setBean(this.authenticationMethod);

        binder.forField(this.isScheduled)
            .bind(AuthenticationMethod::isScheduled, AuthenticationMethod::setScheduled);

        binder.forField(this.synchronisationSchedule)
            .withValidator((s, valueContext) -> {
                if(this.isScheduled.getValue()) {
                    if(!CronExpression.isValidExpression(s)){
                        return ValidationResult.error(getTranslation("form-validation.user-directory-cron-schedule", UI.getCurrent().getLocale(), null));
                    }

                    return ValidationResult.ok();
                }
                else {
                    return ValidationResult.ok();
                }
            })
            .bind(AuthenticationMethod::getSynchronisationCronExpression, AuthenticationMethod::setSynchronisationCronExpression);

        binder.forField(this.directoryName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-name", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getName, AuthenticationMethod::setName);

        binder.forField(this.accountTypeAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-account-type", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getAccountTypeAttributeName, AuthenticationMethod::setAccountTypeAttributeName);

        binder.forField(this.applicationSecurityBaseDn)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-dn", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityBaseDn, AuthenticationMethod::setApplicationSecurityBaseDn);

        binder.forField(this.applicationSecurityDescriptionAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-description", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityDescriptionAttributeName, AuthenticationMethod::setApplicationSecurityDescriptionAttributeName);

        binder.forField(this.applicationSecurityGroupAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-name", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getApplicationSecurityGroupAttributeName, AuthenticationMethod::setApplicationSecurityGroupAttributeName);

        binder.forField(this.departmentAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-user-department", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getDepartmentAttributeName, AuthenticationMethod::setDepartmentAttributeName);

        binder.forField(this.emailAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-email", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getEmailAttributeName, AuthenticationMethod::setEmailAttributeName);

        binder.forField(this.firstNameAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-first-name", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getFirstNameAttributeName, AuthenticationMethod::setFirstNameAttributeName);

        binder.forField(this.groupSynchronisationFilter)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-sync-filter", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getGroupSynchronisationFilter, AuthenticationMethod::setGroupSynchronisationFilter);

        binder.forField(this.ldapBindUserDn)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-ldap-username", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapBindUserDn, AuthenticationMethod::setLdapBindUserDn);

        binder.forField(this.ldapBindUserPassword)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-group-ldap-password", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapBindUserPassword, AuthenticationMethod::setLdapBindUserPassword);

        binder.forField(this.ldapServerUrl)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-server-url", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapServerUrl, AuthenticationMethod::setLdapServerUrl);

        binder.forField(this.ldapUserDescriptionAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-description", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapUserDescriptionAttributeName, AuthenticationMethod::setLdapUserDescriptionAttributeName);

        binder.forField(this.ldapUserSearchDn)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-user-dn", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapUserSearchBaseDn, AuthenticationMethod::setLdapUserSearchBaseDn);

        binder.forField(this.ldapUserSearchFilter)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-ldap-user-search-filter", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getLdapUserSearchFilter, AuthenticationMethod::setLdapUserSearchFilter);

        binder.forField(this.memberofAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-member-of", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getMemberofAttributeName, AuthenticationMethod::setMemberofAttributeName);

        binder.forField(this.surnameAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-surname", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getSurnameAttributeName, AuthenticationMethod::setSurnameAttributeName);

        binder.forField(this.userAccountNameAttributeName)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-user-account", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getUserAccountNameAttributeName, AuthenticationMethod::setUserAccountNameAttributeName);

        binder.forField(this.userSynchronisationFilter)
            .withValidator(new StringLengthValidator(
                getTranslation("form-validation.user-directory-user-sync-filter", UI.getCurrent().getLocale(), null), 1, null))
            .bind(AuthenticationMethod::getUserSynchronisationFilter, AuthenticationMethod::setUserSynchronisationFilter);

        return binder;
    }

}
