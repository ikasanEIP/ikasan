package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.data.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class UserManagementDialog extends Dialog
{
    private User user;
    private UserService userService;
    private SecurityService securityService;
    private SystemEventService systemEventService;

    /**
     * Constructor
     *
     * @param user
     * @param userService
     * @param securityService
     * @param systemEventService
     */
    public UserManagementDialog(User user, UserService userService,
        SecurityService securityService, SystemEventService systemEventService)
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
        if(this.userService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("systemEventService cannot be null!");
        }

        init();
    }

    private void init()
    {
        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1), new Flex(1.25), new Flex(1.25))
            .withTemplateColumns(new Flex(1), new Flex(1), new Flex(1))
            .withRowAndColumn(initUserForm(), 1,1, 1, 3)
            .withRowAndColumn(createLastAccessGrid(), 2,3, 3, 3)
            .withRowAndColumn(createRolesAccessGrid(), 2,1,  2, 3)
            .withRowAndColumn(createSecurityChangesGrid(), 3, 3)
            .withRowAndColumn(createLdapGroupGrid(), 3,1, 3, 3)
            .withPadding(true)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);
        layout.setSizeFull();
        setSizeFull();
        add(layout);
    }

    private VerticalLayout createLastAccessGrid()
    {
        H3 dashboardActivityLabel = new H3("Dashboard Activity");

        Grid<SystemEvent> dashboardActivityGrid = new Grid<>();

        dashboardActivityGrid.setClassName("my-grid");
        dashboardActivityGrid.addColumn(SystemEvent::getAction).setKey("action").setHeader("Action").setSortable(true);
        dashboardActivityGrid.addColumn(SystemEvent::getTimestamp).setKey("datetime").setHeader("Date/Time").setSortable(true);

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

        Grid<Role> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(Role::getName).setKey("username").setHeader("Name").setSortable(true);
        grid.addColumn(Role::getDescription).setKey("firstname").setHeader("Description").setSortable(true);

        grid.setSizeFull();

        IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());
        if(principal!=null)
        {
            grid.setItems(principal.getRoles());
        }

        Button addRoleButton = new Button("Add role");

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
            authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_ADMIN)
            || authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_WRITE))
        {
            addRoleButton.setVisible(true);
        }
        else
        {
            addRoleButton.setVisible(false);
        }

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
        layout.add(headerLayout, grid);
        return layout;
    }

    private VerticalLayout createLdapGroupGrid()
    {
        H3 ldapGroupsLabel = new H3("LDAP Groups");
        Grid<IkasanPrincipal> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(IkasanPrincipal::getName).setKey("name").setHeader("LDAP Group").setSortable(true);
        grid.addColumn(IkasanPrincipal::getType).setKey("type").setHeader("Type").setSortable(true);
        grid.addColumn(IkasanPrincipal::getDescription).setKey("description").setHeader("Description").setSortable(true);

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
        H3 userSecurityChangesLabel = new H3("User Security Changes");

        Grid<SystemEvent> lastAccessGrid = new Grid<>();

        lastAccessGrid.setClassName("my-grid");
        lastAccessGrid.addColumn(SystemEvent::getAction).setKey("action").setHeader("Action").setSortable(true);
        lastAccessGrid.addColumn(SystemEvent::getTimestamp).setKey("datetime").setHeader("Date/Time").setSortable(true);

        lastAccessGrid.setSizeFull();

        ArrayList<String> subjects = new ArrayList<String>();
        subjects.add(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS);

        List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);

        lastAccessGrid.setItems(events);

        VerticalLayout layout = new VerticalLayout();
        layout.add(userSecurityChangesLabel, lastAccessGrid);
        return layout;
    }

    private VerticalLayout initUserForm()
    {
        H3 userProfileLabel = new H3("User Profile - " + this.user.getUsername());

        FormLayout formLayout = new FormLayout();

        TextField firstnameTf = new TextField("First name");
        firstnameTf.setValue(this.user.getFirstName());
        formLayout.add(firstnameTf);

        TextField surnameTf = new TextField("Surname");
        surnameTf.setValue(this.user.getSurname());
        formLayout.add(surnameTf);

        TextField departmentTf = new TextField("Department");
        departmentTf.setValue(this.user.getDepartment() == null ? "" : this.user.getDepartment());
        formLayout.add(departmentTf);

        TextField emailTf = new TextField("Email");
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
