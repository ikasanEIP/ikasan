package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

public class SelectRoleDialog extends Dialog
{
    private User user;
    private UserService userService;
    private SecurityService securityService;
    private SystemEventService systemEventService;

    public SelectRoleDialog(User user, UserService userService, SecurityService securityService, SystemEventService systemEventService)
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

        init();
    }

    private void init()
    {
        List<Role> roles = this.securityService.getAllRoles();

        IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());

        Set<Role> principalRoles = principal.getRoles();

        roles.removeAll(principalRoles);

        Grid<Role> roleGrid = new Grid<>();
        roleGrid.setSizeFull();

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setKey("role").setHeader("Role").setSortable(true);
        roleGrid.addColumn(new ComponentRenderer<>(role ->
        {
            Button addRoleButton = new Button(VaadinIcon.PLUS.create());

            addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                principal.getRoles().add(role);

                this.securityService.savePrincipal(principal);

                IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)SecurityContextHolder.getContext().getAuthentication();

                String action = "Role " + role.getName() + " added by " + ikasanAuthentication.getName();

                systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, principal.getName());

                this.close();
            });

            return addRoleButton;
        }));

        roleGrid.setItems(roles);

        roleGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(roleGrid);

        layout.setWidth("300px");
        layout.setHeight("200px");

        this.add(layout);
    }
}
