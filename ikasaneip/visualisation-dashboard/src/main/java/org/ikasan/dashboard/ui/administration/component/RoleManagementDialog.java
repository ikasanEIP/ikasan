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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.administration.filter.GroupFilter;
import org.ikasan.dashboard.ui.administration.filter.PolicyFilter;
import org.ikasan.dashboard.ui.administration.filter.UserLiteFilter;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.*;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RoleManagementDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventService systemEventService;
    private SystemEventLogger systemEventLogger;
    private UserService userService;

    private FilteringGrid<Policy> policyGrid;
    private FilteringGrid<IkasanPrincipalLite> groupGrid;
    private FilteringGrid<UserLite> userGrid;

    /**
     * Constructor
     *
     * @param role
     * @param securityService
     * @param userService
     * @param systemEventService
     * @param systemEventLogger
     */
    public RoleManagementDialog(Role role, SecurityService securityService, UserService userService,
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
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
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

    /**
     * Initialise this dialog
     */
    private void init()
    {
        Accordion accordion = new Accordion();
        accordion.add("Associated Users", createAssociatedUserLayout());
        accordion.add("Associated Groups", createAssociatedGroupsLayout());
        accordion.add("Associated Policies", createIkasanPoliciesLayout());

        accordion.close();

        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1), new Flex(2))
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

    /**
     * Create the policy layout
     *
     * @return layout containing the relevant policy components.
     */
    private VerticalLayout createIkasanPoliciesLayout()
    {
        H3 policyLabel = new H3("Ikasan Policies");

        PolicyFilter policyFilter = new PolicyFilter();

        this.policyGrid = new FilteringGrid<>(policyFilter);
        policyGrid.setClassName("my-userGrid");
        policyGrid.addColumn(Policy::getName).setKey("name").setHeader("Name").setSortable(true).setFlexGrow(1);
        policyGrid.addColumn(Policy::getDescription).setKey("description").setHeader("Description").setSortable(true).setFlexGrow(4);
        policyGrid.addColumn(new ComponentRenderer<>(policy->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                role.getPolicies().remove(policy);
                securityService.saveRole(role);

                String action = String.format("Policy [%s] removed from role [%s]", policy.getName(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.updatePoliciesGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = this.policyGrid.appendHeaderRow();
        this.policyGrid.addGridFiltering(hr, policyFilter::setNameFilter, "name");
        this.policyGrid.addGridFiltering(hr, policyFilter::setDescriptionFilter, "description");

        policyGrid.setSizeFull();

        Button addPolicyButton = new Button("Add policy");
        addPolicyButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectPolicyForRoleDialog dialog = new SelectPolicyForRoleDialog(this.role, this.securityService, this.systemEventLogger);
            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                    this.updatePoliciesGrid();
                }
            });

            dialog.open();
        });

        this.updatePoliciesGrid();

        return this.layoutAssociatedEntityComponents(policyGrid, addPolicyButton, policyLabel);
    }

    /**
     * Helper method to update the policies grid.
     */
    private void updatePoliciesGrid()
    {
        this.policyGrid.setItems(role.getPolicies());
    }

    /**
     * Create the associated users layout
     *
     * @return layout containing the relevant assoicated users components.
     */
    private VerticalLayout createAssociatedUserLayout()
    {
        H3 associatedUsersLabel = new H3("Associated Users");

        UserLiteFilter userLiteFilter = new UserLiteFilter();

        this.userGrid = new FilteringGrid<>(userLiteFilter);

        userGrid.setClassName("my-userGrid");
        userGrid.addColumn(UserLite::getUsername).setKey("username").setHeader("Username").setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getFirstName).setKey("firstname").setHeader("Firstname").setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getSurname).setKey("surname").setHeader("Surname").setSortable(true).setFlexGrow(4);
        userGrid.addColumn(UserLite::getEmail).setKey("email").setHeader("Email").setSortable(true).setFlexGrow(4);
        userGrid.addColumn(UserLite::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        userGrid.addColumn(new ComponentRenderer<>(userLite->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal ikasanPrincipal = this.securityService.findPrincipalByName(userLite.getUsername());
                ikasanPrincipal.getRoles().remove(this.role);

                this.securityService.savePrincipal(ikasanPrincipal);

                String action = String.format("User [%s] removed from role [%s]", userLite.getUsername(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.updateAssociatedUsersGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = userGrid.appendHeaderRow();
        this.userGrid.addGridFiltering(hr, userLiteFilter::setUsernameFilter, "username");
        this.userGrid.addGridFiltering(hr, userLiteFilter::setNameFilter, "firstname");
        this.userGrid.addGridFiltering(hr, userLiteFilter::setLastNameFilter, "surname");
        this.userGrid.addGridFiltering(hr, userLiteFilter::setEmailFilter, "email");
        this.userGrid.addGridFiltering(hr, userLiteFilter::setDepartmentFilter, "department");

        Button addUser = new Button("Add user");
        addUser.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectUserForRoleDialog dialog = new SelectUserForRoleDialog(this.role, this.userService, this.getAssociatedUsers(),
                this.securityService, this.systemEventLogger);
            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                    this.updateAssociatedUsersGrid();
                }
            });

            dialog.open();
        });

        userGrid.setSizeFull();

        this.updateAssociatedUsersGrid();

        return this.layoutAssociatedEntityComponents(userGrid, addUser, associatedUsersLabel);
    }

    /**
     * Helper method to get the associated users.
     *
     * @return users associated with the role.
     */
    private List<UserLite> getAssociatedUsers()
    {
        List<IkasanPrincipal> principals = this.securityService.getAllPrincipalsWithRole(role.getName());

        List<UserLite> users = this.userService.getUserLites();
        HashMap<String, UserLite> userMap = new HashMap<String, UserLite>();

        for(UserLite user: users)
        {
            userMap.put(user.getUsername(), user);
        }

        users = new ArrayList<>();
        for(IkasanPrincipal principal: principals)
        {
            if(principal.getType().equals("user"))
            {
                UserLite user = userMap.get(principal.getName());

                if(user != null)
                {
                    users.add(user);
                }
            }
        }

        return users;
    }

    /**
     * Helper method to update the associated users grid.
     */
    private void updateAssociatedUsersGrid()
    {
        this.userGrid.setItems(this.getAssociatedUsers());
    }

    /**
     * Create the associated groups layout
     *
     * @return layout containing the relevant associated groups components.
     */
    private VerticalLayout createAssociatedGroupsLayout()
    {
        H3 associatedGroupsLabel = new H3("Associated Groups");

        GroupFilter groupFilter = new GroupFilter();

        groupGrid = new FilteringGrid<>(groupFilter);
        groupGrid.setClassName("my-userGrid");
        groupGrid.addColumn(IkasanPrincipalLite::getName).setKey("name").setHeader("Name").setSortable(true);
        groupGrid.addColumn(IkasanPrincipalLite::getDescription).setKey("description").setHeader("Description").setSortable(true);
        groupGrid.addColumn(new ComponentRenderer<>(principalLite->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal ikasanPrincipal = this.securityService.findPrincipalByName(principalLite.getName());
                ikasanPrincipal.getRoles().remove(this.role);

                this.securityService.savePrincipal(ikasanPrincipal);

                String action = String.format("Group [%s] removed from role [%s]", principalLite.getName(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.updateAssociatedGroupsGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = groupGrid.appendHeaderRow();
        this.groupGrid.addGridFiltering(hr, groupFilter::setNameFilter, "name");
        this.groupGrid.addGridFiltering(hr, groupFilter::setDescriptionFilter, "description");

        Button addGroup = new Button("Add group");
        addGroup.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectGroupForRoleDialog dialog = new SelectGroupForRoleDialog(this.role, getAssociatedGroups()
                , this.securityService, this.systemEventLogger);
            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                    this.updateAssociatedGroupsGrid();
                }
            });

            dialog.open();
        });

        groupGrid.setSizeFull();

        this.updateAssociatedGroupsGrid();

        return this.layoutAssociatedEntityComponents(groupGrid, addGroup, associatedGroupsLabel);
    }

    /**
     * Helper method to get the associated groups.
     *
     * @return list of associated groups
     */
    private List<IkasanPrincipalLite> getAssociatedGroups()
    {
        List<IkasanPrincipal> principals = this.securityService.getAllPrincipalsWithRole(role.getName());

        List<IkasanPrincipalLite> principalLites = this.securityService.getAllPrincipalLites();
        HashMap<String, IkasanPrincipalLite> principalMap = new HashMap<String, IkasanPrincipalLite>();

        for(IkasanPrincipalLite principalLite: principalLites)
        {
            principalMap.put(principalLite.getName(), principalLite);
        }

        principalLites = new ArrayList<>();
        for(IkasanPrincipal principal: principals)
        {
            if(principal.getType().equals("application"))
            {
                IkasanPrincipalLite ikasanPrincipalLite = principalMap.get(principal.getName());

                if(ikasanPrincipalLite != null)
                {
                    principalLites.add(ikasanPrincipalLite);
                }
            }
        }

        return principalLites;
    }

    /**
     * Helper method to update the associated groups grid.
     */
    private void updateAssociatedGroupsGrid()
    {
        this.groupGrid.setItems(this.getAssociatedGroups());
    }

    /**
     * General layout for all associated entities.
     *
     * @param grid
     * @param button
     * @param label
     *
     * @return the general layout
     */
    private VerticalLayout layoutAssociatedEntityComponents(Grid grid, Button button, H3 label)
    {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(button);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, button);

        ComponentSecurityVisibility.applySecurity(button, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.USER_ADMINISTRATION_ADMIN
            , SecurityConstants.USER_ADMINISTRATION_WRITE);

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.add(label);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.add(labelLayout, buttonLayout);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, grid);
        layout.setWidth("100%");
        layout.setHeight("400px");
        return layout;
    }

    /**
     * Init the role form.
     *
     * @return
     */
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
        description.setHeight("130px");
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
