package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.administration.filter.GroupFilter;
import org.ikasan.dashboard.ui.administration.filter.PolicyFilter;
import org.ikasan.dashboard.ui.administration.filter.RoleModuleFilter;
import org.ikasan.dashboard.ui.administration.filter.UserLiteFilter;
import org.ikasan.dashboard.ui.general.component.AbstractCloseableResizableDialog;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.*;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.systemevent.SystemEventService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RoleManagementDialog extends AbstractCloseableResizableDialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventService systemEventService;
    private SystemEventLogger systemEventLogger;
    private UserService userService;
    private ModuleMetaDataService moduleMetadataService;

    private FilteringGrid<UserLite> userGrid;

    private FilteringGrid<IkasanPrincipalLite> groupGrid;

    private FilteringGrid<Policy> policyGrid;

    private FilteringGrid<RoleModule> roleModuleGrid;

    /**
     * Constructor
     *
     * @param role
     * @param securityService
     * @param userService
     * @param systemEventService
     * @param systemEventLogger
     * @param moduleMetadataService
     */
    public RoleManagementDialog(Role role, SecurityService securityService, UserService userService,
                                SystemEventService systemEventService, SystemEventLogger systemEventLogger,
                                ModuleMetaDataService moduleMetadataService)
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
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null)
        {
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
        }

        init();
    }

    /**
     * Initialise this dialog
     */
    private void init()
    {
        Accordion accordion = new Accordion();
        accordion.add(getTranslation("accordian-label.associated-users", UI.getCurrent().getLocale(), null), createAssociatedUserLayout());
        accordion.add(getTranslation("accordian-label.associated-groups", UI.getCurrent().getLocale(), null), createAssociatedGroupsLayout());
        accordion.add(getTranslation("accordian-label.associated-policies", UI.getCurrent().getLocale(), null), createIkasanPoliciesLayout());
        accordion.add(getTranslation("accordian-label.associated-integration-modules", UI.getCurrent().getLocale(), null), this.createAssociatedIntegrationModules());

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
        this.content.add(layout);
    }

    /**
     * Create the policy layout
     *
     * @return layout containing the relevant policy components.
     */
    private VerticalLayout createIkasanPoliciesLayout()
    {
        super.title.setText(getTranslation("label.role-ikasan-policies", UI.getCurrent().getLocale()));
        H3 policyLabel = new H3(getTranslation("label.role-ikasan-policies", UI.getCurrent().getLocale()));

        PolicyFilter policyFilter = new PolicyFilter();

        this.policyGrid = new FilteringGrid<>(policyFilter);
        policyGrid.setClassName("my-userGrid");
        policyGrid.addColumn(Policy::getName).setKey("name").setHeader(getTranslation("table-header.role-name", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(1);
        policyGrid.addColumn(Policy::getDescription).setKey("description").setHeader(getTranslation("table-header.role-description", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
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

            deleteButton.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.POLICY_ADMINISTRATION_WRITE,
                SecurityConstants.POLICY_ADMINISTRATION_ADMIN, SecurityConstants.ALL_AUTHORITY));

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

        Button addPolicyButton = new Button(getTranslation("button.add-policy", UI.getCurrent().getLocale(), null));
        addPolicyButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectPolicyForRoleDialog dialog = new SelectPolicyForRoleDialog(this.role, this.securityService, this.systemEventLogger, this.policyGrid);

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
        H3 associatedUsersLabel = new H3(getTranslation("label.role-associated-users", UI.getCurrent().getLocale(), null));

        UserLiteFilter userLiteFilter = new UserLiteFilter();

        this.userGrid = new FilteringGrid<>(userLiteFilter);

        userGrid.setClassName("my-userGrid");
        userGrid.addColumn(UserLite::getUsername).setKey("username").setHeader(getTranslation("table-header.username", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getFirstName).setKey("firstname").setHeader(getTranslation("table-header.firstname", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getSurname).setKey("surname").setHeader(getTranslation("table-header.surname", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
        userGrid.addColumn(UserLite::getEmail).setKey("email").setHeader(getTranslation("table-header.email", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(4);
        userGrid.addColumn(UserLite::getDepartment).setKey("department").setHeader(getTranslation("table-header.department", UI.getCurrent().getLocale(), null)).setSortable(true);
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

            deleteButton.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.USER_ADMINISTRATION_WRITE,
                SecurityConstants.USER_ADMINISTRATION_ADMIN, SecurityConstants.ALL_AUTHORITY));

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

        Button addUser = new Button(getTranslation("button.add-user", UI.getCurrent().getLocale(), null));
        addUser.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectUserForRoleDialog dialog = new SelectUserForRoleDialog(this.role, this.userService, this.getAssociatedUsers(),
                this.securityService, this.systemEventLogger, this.userGrid);

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
        H3 associatedGroupsLabel = new H3(getTranslation("label.role-associated-groups", UI.getCurrent().getLocale(), null));

        GroupFilter groupFilter = new GroupFilter();

        groupGrid = new FilteringGrid<>(groupFilter);
        groupGrid.setClassName("my-userGrid");
        groupGrid.addColumn(IkasanPrincipalLite::getName).setKey("name").setHeader(getTranslation("table-header.group-name", UI.getCurrent().getLocale(), null)).setSortable(true);
        groupGrid.addColumn(IkasanPrincipalLite::getDescription).setKey("description").setHeader(getTranslation("table-header.group-description", UI.getCurrent().getLocale(), null)).setSortable(true);
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

            deleteButton.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.GROUP_ADMINISTRATION_WRITE,
                SecurityConstants.GROUP_ADMINISTRATION_ADMIN, SecurityConstants.ALL_AUTHORITY));

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = groupGrid.appendHeaderRow();
        this.groupGrid.addGridFiltering(hr, groupFilter::setNameFilter, "name");
        this.groupGrid.addGridFiltering(hr, groupFilter::setDescriptionFilter, "description");

        Button addGroup = new Button(getTranslation("button.add-group", UI.getCurrent().getLocale(), null));
        addGroup.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectGroupForRoleDialog dialog = new SelectGroupForRoleDialog(this.role, getAssociatedGroups()
                , this.securityService, this.systemEventLogger, this.groupGrid);
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
     * Create the associated integration modules
     *
     * @return layout containing the relevant associated integration module components.
     */
    private VerticalLayout createAssociatedIntegrationModules()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        H3 associatedRoleModulesLabel = new H3(getTranslation("label.role-associated-integration-modules", UI.getCurrent().getLocale(), null));

        verticalLayout.add(associatedRoleModulesLabel);

        RoleModuleFilter roleModuleFilter = new RoleModuleFilter();

        this.roleModuleGrid = new FilteringGrid<>(roleModuleFilter);
        this.roleModuleGrid.setClassName("my-userGrid");
        this.roleModuleGrid.addColumn(RoleModule::getModuleName).setKey("name").setHeader(getTranslation("table-header.moduleName", UI.getCurrent().getLocale(), null)).setSortable(true).setFlexGrow(2);
        this.roleModuleGrid.addColumn(new ComponentRenderer<>(roleModule->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                this.role.getRoleModules().remove(roleModule);
                this.securityService.saveRole(role);
                this.securityService.deleteRoleModule(roleModule);

                String action = String.format("Module [%s] removed from role [%s]", roleModule.getModuleName(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_MODULE_ROLE_CHANGE_CONSTANTS, action, null);

                this.updateRoleModuleGrid();
            });

            deleteButton.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ROLE_ADMINISTRATION_WRITE,
                SecurityConstants.ROLE_ADMINISTRATION_WRITE, SecurityConstants.ALL_AUTHORITY));

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = roleModuleGrid.appendHeaderRow();
        this.roleModuleGrid.addGridFiltering(hr, roleModuleFilter::setModuleNameFilter, "name");

        Button addModule = new Button(getTranslation("button.add-role-module", UI.getCurrent().getLocale(), null));
        addModule.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectModuleForRoleDialog dialog = new SelectModuleForRoleDialog(this.role, this.moduleMetadataService,
                this.securityService, this.systemEventLogger, this.roleModuleGrid);

            dialog.open();
        });

        userGrid.setSizeFull();

        this.updateRoleModuleGrid();

        return this.layoutAssociatedEntityComponents(this.roleModuleGrid, addModule, associatedRoleModulesLabel);
    }

    protected void updateRoleModuleGrid()
    {
        this.roleModuleGrid.setItems(this.role.getRoleModules());
    }

    /**
     * Init the role form.
     *
     * @return
     */
    private VerticalLayout initRoleForm()
    {
        H3 userProfileLabel = new H3(String.format(getTranslation("label.role-profile", UI.getCurrent().getLocale(), null), this.role.getName()));

        FormLayout formLayout = new FormLayout();

        TextField groupName = new TextField(getTranslation("text-field.group-name", UI.getCurrent().getLocale(), null));
        groupName.setReadOnly(true);
        groupName.setValue(this.role.getName());
        formLayout.add(groupName);
        formLayout.setColspan(groupName, 2);

        TextArea description = new TextArea(getTranslation("text-field.group-description", UI.getCurrent().getLocale(), null));
        description.setReadOnly(true);
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
