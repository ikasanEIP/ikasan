package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.administration.filter.PolicyFilter;
import org.ikasan.dashboard.ui.administration.filter.UserFilter;
import org.ikasan.dashboard.ui.administration.filter.UserLiteFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.*;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import java.util.Comparator;
import java.util.List;

public class SelectUserForRoleDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;
    private UserService userService;
    private List<UserLite> associatedUsers;

    public SelectUserForRoleDialog(Role role, UserService userService, List<UserLite> associatedUsers, SecurityService securityService
        , SystemEventLogger systemEventLogger)
    {
        this.role = role;
        if(this.role == null)
        {
            throw new IllegalArgumentException("role cannot be null!");
        }
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
        }
        this.associatedUsers = associatedUsers;
        if(this.associatedUsers == null)
        {
            throw new IllegalArgumentException("associatedUsers cannot be null!");
        }
        this.securityService = securityService;
        if(this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
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
        List<UserLite> usersList = this.userService.getUserLites();
        usersList.removeAll(associatedUsers);

        UserLiteFilter userFilter = new UserLiteFilter();

        FilteringGrid<UserLite> userGrid = new FilteringGrid<>(userFilter);
        userGrid.setSizeFull();

        userGrid.addColumn(UserLite::getUsername).setKey("username").setHeader("Username").setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getSurname).setKey("surname").setHeader("Surname").setSortable(true).setFlexGrow(2);
        userGrid.addColumn(UserLite::getEmail).setKey("email").setHeader("Email").setSortable(true).setFlexGrow(4);
        userGrid.addColumn(UserLite::getDepartment).setKey("department").setHeader("Department").setSortable(true).setFlexGrow(4);
        userGrid.addColumn(new ComponentRenderer<>(userLite ->
        {
            Button addUserButton = new Button(VaadinIcon.PLUS.create());

            addUserButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal ikasanPrincipal = this.securityService.findPrincipalByName(userLite.getUsername());
                ikasanPrincipal.addRole(this.role);

                this.securityService.savePrincipal(ikasanPrincipal);

                String action = String.format("Role [%s] added to user [%s].", role.getName(), ikasanPrincipal.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.close();
            });

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(addUserButton);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, addUserButton);
            return verticalLayout;
        })).setFlexGrow(1);

        HeaderRow hr = userGrid.appendHeaderRow();
        userGrid.addGridFiltering(hr, userFilter::setUsernameFilter, "username");
        userGrid.addGridFiltering(hr, userFilter::setNameFilter, "firstname");
        userGrid.addGridFiltering(hr, userFilter::setLastNameFilter, "surname");
        userGrid.addGridFiltering(hr, userFilter::setEmailFilter, "email");
        userGrid.addGridFiltering(hr, userFilter::setDepartmentFilter, "department");

        userGrid.setItems(usersList);

        userGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userGrid);

        layout.setWidth("1200px");
        layout.setHeight("500px");

        this.add(layout);
    }
}
