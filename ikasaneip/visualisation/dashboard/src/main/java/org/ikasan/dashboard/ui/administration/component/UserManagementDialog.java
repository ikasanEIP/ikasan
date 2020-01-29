package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventService;

import java.util.ArrayList;
import java.util.List;


public class UserManagementDialog extends Dialog
{
    private User user;
    private UserService userService;
    private SecurityService securityService;
    private SystemEventService systemEventService;
    private SystemEventLogger systemEventLogger;
    private Grid<SystemEvent> securityChangesGrid = new Grid<>();

    private Grid<Role> roleGrid = new Grid<>();
    private ListDataProvider<Role> roleListDataProvider;

    /**
     * Constructor
     *
     * @param user
     * @param userService
     * @param securityService
     * @param systemEventService
     * @param systemEventLogger
     */
    public UserManagementDialog(User user, UserService userService,
        SecurityService securityService, SystemEventService systemEventService, SystemEventLogger systemEventLogger)
    {
        this.user = user;
        if(this.user == null)
        {
            throw new IllegalArgumentException("User cannot be null!");
        }
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("User Service cannot be null!");
        }
        this.securityService = securityService;
        if(this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("systemEventService cannot be null!");
        }
        this.systemEventLogger = systemEventLogger;
        if(this.systemEventLogger == null)
        {
            throw new IllegalArgumentException("systemEventLogger cannot be null!");
        }

        init();
    }

    private void init()
    {
        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1), new Flex(1.5), new Flex(1.5))
            .withTemplateColumns(new Flex(1.5), new Flex(1.5), new Flex(2))
            .withRowAndColumn(initUserForm(), 1,1, 1, 3)
            .withRowAndColumn(createLastAccessGrid(), 2,3, 3, 3)
            .withRowAndColumn(createRolesAccessGrid(), 2,1,  2, 3)
            .withRowAndColumn(createSecurityChangesGrid(), 3, 3)
            .withRowAndColumn(createLdapGroupGrid(), 3,1, 3, 3)
            .withPadding(true)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);
        layout.setSizeFull();
        this.setWidth("1400px");
        this.setHeight("100%");
        add(layout);

    }

    private VerticalLayout createLastAccessGrid()
    {
        H3 dashboardActivityLabel = new H3(getTranslation("label.dashboard-activity", UI.getCurrent().getLocale(), null));

        Grid<SystemEvent> dashboardActivityGrid = new Grid<>();

        dashboardActivityGrid.setClassName("my-grid");
        dashboardActivityGrid.addColumn(SystemEvent::getAction).setKey("action").setHeader(getTranslation("table-header.action", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
        dashboardActivityGrid.addColumn(SystemEvent::getTimestamp).setKey("datetime").setHeader(getTranslation("table-header.date-time", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(1);

        dashboardActivityGrid.setSizeFull();

        ArrayList<String> subjects = new ArrayList<String>();
        subjects.add(SystemEventConstants.DASHBOARD_LOGIN_CONSTANTS);
        subjects.add(SystemEventConstants.DASHBOARD_LOGOUT_CONSTANTS);
        subjects.add(SystemEventConstants.DASHBOARD_SESSION_EXPIRED_CONSTANTS);

        List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);
        dashboardActivityGrid.setItems(events);

        Button dummy = new Button("button");
        dummy.setVisible(false);

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.add(dashboardActivityLabel, dummy);

        VerticalLayout layout = new VerticalLayout();
        layout.add(labelLayout, dashboardActivityGrid);

        return layout;
    }

    private VerticalLayout createRolesAccessGrid()
    {
        H3 rolesLabel = new H3("Ikasan Roles");

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setKey("username").setHeader(getTranslation("table-header.role-name", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(1);
        roleGrid.addColumn(Role::getDescription).setKey("firstname").setHeader(getTranslation("table-header.role-description", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(6);
        roleGrid.addColumn(new ComponentRenderer<>(role->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());
                principal.getRoles().remove(role);
                securityService.savePrincipal(principal);

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS
                    , "Role " + role.getName() + " removed.", user.getName());

                this.updateRolesGrid();
                this.updateSecurityChangesGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, deleteButton);
            return layout;
        })).setFlexGrow(1);

        roleGrid.setSizeFull();

        this.updateRolesGrid();

        Button addRoleButton = new Button(getTranslation("button.add-role", UI.getCurrent().getLocale(), null));
        addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            IkasanPrincipal principal = securityService.findPrincipalByName(this.user.getUsername());
            SelectRoleDialog dialog = new SelectRoleDialog(principal, this.securityService, this.systemEventLogger, this.roleListDataProvider);

            dialog.open();
        });

        ComponentSecurityVisibility.applySecurity(addRoleButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.USER_ADMINISTRATION_ADMIN
            , SecurityConstants.USER_ADMINISTRATION_WRITE);

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.add(rolesLabel);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(addRoleButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, addRoleButton);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.add(labelLayout, buttonLayout);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, this.roleGrid);
        return layout;
    }

    private void updateRolesGrid()
    {
        IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());
        if(principal!=null)
        {
            this.roleListDataProvider = new ListDataProvider<>(principal.getRoles());
            this.roleGrid.setDataProvider(this.roleListDataProvider);
        }
    }

    private VerticalLayout createLdapGroupGrid()
    {
        H3 ldapGroupsLabel = new H3(getTranslation("label.ldap-groups", UI.getCurrent().getLocale(), null));
        Grid<IkasanPrincipal> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(IkasanPrincipal::getName).setKey("name").setHeader(getTranslation("table-header.group-name", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
        grid.addColumn(IkasanPrincipal::getDescription).setKey("description").setHeader(getTranslation("table-header.group-description", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);

        List<IkasanPrincipal> ldapGroups = new ArrayList<>();

        for(IkasanPrincipal ikasanPrincipal: user.getPrincipals())
        {
            if(!ikasanPrincipal.getType().equals("user"))
            {
               ldapGroups.add(ikasanPrincipal);
            }
        }

        grid.setItems(ldapGroups);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(ldapGroupsLabel, grid);
        return layout;
    }

    private VerticalLayout createSecurityChangesGrid()
    {
        H3 userSecurityChangesLabel = new H3(getTranslation("label.user-security-changes", UI.getCurrent().getLocale(), null));

        securityChangesGrid.setClassName("my-grid");
        securityChangesGrid.addColumn(SystemEvent::getAction).setKey("action").setHeader(getTranslation("table-header.action", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
        securityChangesGrid.addColumn(SystemEvent::getTimestamp).setKey("datetime").setHeader(getTranslation("table-header.date-time", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(1);

        securityChangesGrid.setSizeFull();

        this.updateSecurityChangesGrid();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userSecurityChangesLabel, securityChangesGrid);
        return layout;
    }

    private void updateSecurityChangesGrid()
    {
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS);

        List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);

        securityChangesGrid.setItems(events);
    }

    private VerticalLayout initUserForm()
    {
        H3 userProfileLabel = new H3(String.format(getTranslation("label.user-profile", UI.getCurrent().getLocale(), null),  this.user.getUsername()));

        FormLayout formLayout = new FormLayout();

        TextField firstnameTf = new TextField(getTranslation("text-field.first-name", UI.getCurrent().getLocale(), null));
        firstnameTf.setReadOnly(true);
        firstnameTf.setValue(this.user.getFirstName());
        formLayout.add(firstnameTf);

        TextField surnameTf = new TextField(getTranslation("text-field.surname", UI.getCurrent().getLocale(), null));
        surnameTf.setReadOnly(true);
        surnameTf.setValue(this.user.getSurname());
        formLayout.add(surnameTf);

        TextField departmentTf = new TextField(getTranslation("text-field.department", UI.getCurrent().getLocale(), null));
        departmentTf.setReadOnly(true);
        departmentTf.setValue(this.user.getDepartment() == null ? "" : this.user.getDepartment());
        formLayout.add(departmentTf);

        TextField emailTf = new TextField(getTranslation("text-field.email", UI.getCurrent().getLocale(), null));
        emailTf.setReadOnly(true);
        formLayout.add(emailTf);
        emailTf.setValue(this.user.getEmail()== null ? "" : this.user.getEmail());
        formLayout.setSizeFull();

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userProfileLabel, formLayout);
        return layout;
    }
}
