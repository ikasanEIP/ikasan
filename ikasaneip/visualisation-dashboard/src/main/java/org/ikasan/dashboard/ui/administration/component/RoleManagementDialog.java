package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;


public class RoleManagementDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventService systemEventService;
    private SystemEventLogger systemEventLogger;
    private Grid<Role> roleGrid = new Grid<>();
    Grid<SystemEvent> securityChangesGrid = new Grid<>();

    /**
     * Constructor
     */
    public RoleManagementDialog(Role role, SecurityService securityService,
                                SystemEventService systemEventService, SystemEventLogger systemEventLogger)
    {
        this.role = role;
        if(this.role == null)
        {
            throw new IllegalArgumentException("Group cannot be null!");
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
        Accordion accordion = new Accordion();
        accordion.add("Associated Policies",createIkasanPoliciesGrid());
        accordion.add("Associated Users",createAssociatedUserGrid());
        accordion.add("Associated Groups",createAssociatedGroupsGrid());

        accordion.close();

        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1), new Flex(2.8))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(initRoleForm(), 1, 1, 1, 1)
            .withRowAndColumn(accordion, 2, 1, 2, 1)
            .withPadding(true)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);
        layout.setSizeFull();
        this.setWidth("1400px");
        this.setHeight("100%");
        add(layout);

    }

    private VerticalLayout createIkasanPoliciesGrid()
    {
        H3 rolesLabel = new H3("Ikasan Policies");

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setKey("username").setHeader("Name").setSortable(true).setFlexGrow(1);
        roleGrid.addColumn(Role::getDescription).setKey("firstname").setHeader("Description").setSortable(true).setFlexGrow(4);
        roleGrid.addColumn(new ComponentRenderer<>(role->
        {
            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
//                IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());
//                principal.getRoles().remove(role);
//                securityService.savePrincipal(principal);
//
//                String action = "Role " + role.getName() + " removed.";
//
//                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action);
//
//                this.updateRolesGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        roleGrid.setSizeFull();

        this.updateRolesGrid();

        Button addRoleButton = new Button("Add role");
        addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
//            SelectRoleDialog dialog = new SelectRoleDialog(this.user, this.userService, this.securityService, this.systemEventService);
//            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
//            {
//                if(dialogOpenedChangeEvent.isOpened() == false)
//                {
//                    this.updateRolesGrid();
//                }
//            });
//
//            dialog.open();
        });

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
        layout.add(headerLayout, this.roleGrid);
        layout.setWidth("100%");
        layout.setHeight("400px");
        return layout;
    }

    private void updateRolesGrid()
    {
        IkasanPrincipal principal = securityService.findPrincipalByName(this.role.getName());
        if(principal!=null)
        {
            roleGrid.setItems(principal.getRoles());
        }
    }

    private VerticalLayout createAssociatedUserGrid()
    {
        H3 associatedUsersLabel = new H3("Associated Users");
        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getName).setKey("username").setHeader("Username").setSortable(true);
        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("Firstname").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);

//        grid.setItems(ldapGroups);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(associatedUsersLabel, grid);
        layout.setWidth("100%");
        layout.setHeight("400px");
        return layout;
    }

    private VerticalLayout createAssociatedGroupsGrid()
    {
        H3 associatedUsersLabel = new H3("Associated Groups");
        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getName).setKey("username").setHeader("Username").setSortable(true);
        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("Firstname").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);

//        grid.setItems(ldapGroups);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(associatedUsersLabel, grid);
        layout.setWidth("100%");
        layout.setHeight("400px");
        return layout;
    }

    private VerticalLayout initRoleForm()
    {
        H3 userProfileLabel = new H3("Role Profile - " + this.role.getName());

        FormLayout formLayout = new FormLayout();

        TextField groupName = new TextField("Group name");
        groupName.setValue(this.role.getName());
        formLayout.add(groupName);
        formLayout.setColspan(groupName, 2);

        TextArea description = new TextArea("Description");
        description.setValue(this.role.getDescription());
        formLayout.add(description);
        formLayout.setColspan(description, 2);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userProfileLabel, formLayout);
        return layout;
    }
}
