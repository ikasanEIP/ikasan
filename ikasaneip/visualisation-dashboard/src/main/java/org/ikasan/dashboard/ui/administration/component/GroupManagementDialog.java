package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.administration.filter.UserFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;


public class GroupManagementDialog extends Dialog
{
    private IkasanPrincipalLite group;
    private SecurityService securityService;
    private SystemEventService systemEventService;
    private SystemEventLogger systemEventLogger;
    private Grid<Role> roleGrid = new Grid<>();
    Grid<SystemEvent> securityChangesGrid = new Grid<>();

    /**
     * Constructor
     */
    public GroupManagementDialog(IkasanPrincipalLite group, SecurityService securityService,
                                 SystemEventService systemEventService, SystemEventLogger systemEventLogger)
    {
        this.group = group;
        if(this.group == null)
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
        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1.5), new Flex(2.5), new Flex(2.5))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(initGroupForm(), 1, 1, 1, 1)
            .withRowAndColumn(createRolesAccessGrid(), 2, 1, 2, 1)
            .withRowAndColumn(createAssociatedUserGrid(), 3, 1, 3, 1)
            .withPadding(true)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);
        layout.setSizeFull();
        this.setWidth("1400px");
        this.setHeight("100%");
        add(layout);

    }

    private VerticalLayout createRolesAccessGrid()
    {
        H3 rolesLabel = new H3("Ikasan Roles");

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setKey("username").setHeader("Name").setSortable(true).setFlexGrow(1);
        roleGrid.addColumn(Role::getDescription).setKey("firstname").setHeader("Description").setSortable(true).setFlexGrow(4);
        roleGrid.addColumn(new ComponentRenderer<>(role->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal principal = securityService.findPrincipalByName(group.getName());
                principal.getRoles().remove(role);
                securityService.savePrincipal(principal);

                String action = String.format("Role [%s] removed from group [%s].", role.getName(), principal.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.updateRolesGrid();
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
            IkasanPrincipal principal = securityService.findPrincipalByName(group.getName());

            SelectRoleDialog dialog = new SelectRoleDialog(principal, this.securityService, this.systemEventLogger);
            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                    this.updateRolesGrid();
                }
            });

            dialog.open();
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
        return layout;
    }

    private void updateRolesGrid()
    {
        IkasanPrincipal principal = securityService.findPrincipalByName(this.group.getName());
        if(principal!=null)
        {
            roleGrid.setItems(principal.getRoles());
        }
    }

    private VerticalLayout createAssociatedUserGrid()
    {
        H3 associatedUsersLabel = new H3("Associated Users");

        UserFilter userFilter = new UserFilter();

        FilteringGrid<User> grid = new FilteringGrid<>(userFilter);

        grid.setClassName("my-grid");
        grid.addColumn(User::getName).setKey("username").setHeader("Username").setSortable(true);
        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("Firstname").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);

        HeaderRow hr = grid.appendHeaderRow();
        grid.addGridFiltering(hr, userFilter::setUsernameFilter, "username");
        grid.addGridFiltering(hr, userFilter::setNameFilter, "firstname");
        grid.addGridFiltering(hr, userFilter::setLastNameFilter, "surname");
        grid.addGridFiltering(hr, userFilter::setEmailFilter, "email");
        grid.addGridFiltering(hr, userFilter::setDepartmentFilter, "department");

        grid.setSizeFull();

        List<User> userList = this.securityService.getUsersAssociatedWithPrincipal(group.getId());

        grid.setItems(userList);

        VerticalLayout layout = new VerticalLayout();
        layout.add(associatedUsersLabel, grid);
        return layout;
    }

    private VerticalLayout initGroupForm()
    {
        H3 userProfileLabel = new H3("Group Profile - " + this.group.getName());

        FormLayout formLayout = new FormLayout();

        TextField groupName = new TextField("Group name");
        groupName.setValue(this.group.getName());
        formLayout.add(groupName);
        formLayout.setColspan(groupName, 1);

        TextField groupType = new TextField("Group type");
        groupType.setValue(this.group.getType());
        formLayout.add(groupType);
        formLayout.setColspan(groupType, 1);

        TextField description = new TextField("Description");
        description.setValue(this.group.getDescription());
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
