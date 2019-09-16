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
import org.ikasan.dashboard.ui.administration.filter.RoleFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;

import java.util.List;

public class SelectRoleForPolicyDialog extends Dialog
{
    private Policy policy;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;

    public SelectRoleForPolicyDialog(Policy policy, SecurityService securityService, SystemEventLogger systemEventLogger)
    {
        this.policy = policy;
        if(this.policy == null)
        {
            throw new IllegalArgumentException("policy cannot be null!");
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
        List<Role> roleList = this.securityService.getAllRoles();

        roleList.removeAll(policy.getRoles());

        RoleFilter roleFilter = new RoleFilter();

        FilteringGrid<Role> roleGrid = new FilteringGrid<>(roleFilter);
        roleGrid.setSizeFull();

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setHeader("Name").setKey("name").setSortable(true).setFlexGrow(2);
        roleGrid.addColumn(Role::getDescription).setHeader("Description").setKey("description").setSortable(true).setFlexGrow(5);
        roleGrid.addColumn(new ComponentRenderer<>(role ->
        {
            Button addRoleButton = new Button(VaadinIcon.PLUS.create());

            addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                policy.getRoles().add(role);

                this.securityService.savePolicy(policy);

                String action = String.format("Policy [%s] added to role [%s].", policy.getName(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action,null);

                this.close();
            });

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(addRoleButton);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, addRoleButton);
            return verticalLayout;
        })).setFlexGrow(1);

        HeaderRow hr = roleGrid.appendHeaderRow();
        roleGrid.addGridFiltering(hr, roleFilter::setNameFilter, "name");
        roleGrid.addGridFiltering(hr, roleFilter::setDescriptionFilter, "description");

        roleGrid.setItems(roleList);

        VerticalLayout layout = new VerticalLayout();
        layout.add(roleGrid);

        layout.setWidth("600px");
        layout.setHeight("300px");

        this.add(layout);
    }
}
