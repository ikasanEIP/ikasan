package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
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
    private ListDataProvider<Role> roleListDataProvider;

    public SelectRoleForPolicyDialog(Policy policy, SecurityService securityService, SystemEventLogger systemEventLogger,
                                     ListDataProvider<Role> roleListDataProvider)
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
        this.roleListDataProvider = roleListDataProvider;
        if(this.roleListDataProvider == null)
        {
            throw new IllegalArgumentException("roleListDataProvider cannot be null!");
        }

        init();
    }

    private void init()
    {
        H3 selectRoleLabel = new H3(getTranslation("label.select-role", UI.getCurrent().getLocale(), null));

        List<Role> roleList = this.securityService.getAllRoles();
        roleList.removeAll(policy.getRoles());

        RoleFilter roleFilter = new RoleFilter();

        FilteringGrid<Role> roleGrid = new FilteringGrid<>(roleFilter);
        roleGrid.setSizeFull();

        ListDataProvider<Role> roleDataProvider = new ListDataProvider<>(roleList);
        roleGrid.setDataProvider(roleDataProvider);

        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setHeader(getTranslation("table-header.role-name", UI.getCurrent().getLocale(), null)).setKey("name").setSortable(true).setFlexGrow(2);
        roleGrid.addColumn(Role::getDescription).setHeader(getTranslation("table-header.role-description", UI.getCurrent().getLocale(), null)).setKey("description").setSortable(true).setFlexGrow(5);

        roleGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Role>>) roleItemDoubleClickEvent ->
        {
            policy.getRoles().add(roleItemDoubleClickEvent.getItem());

            this.securityService.savePolicy(policy);

            String action = String.format("Policy [%s] added to role [%s].", policy.getName(), roleItemDoubleClickEvent.getItem().getName());

            this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action,null);

            this.roleListDataProvider.getItems().add(roleItemDoubleClickEvent.getItem());
            this.roleListDataProvider.refreshAll();

            roleDataProvider.getItems().remove(roleItemDoubleClickEvent.getItem());
            roleDataProvider.refreshAll();
        });

        HeaderRow hr = roleGrid.appendHeaderRow();
        roleGrid.addGridFiltering(hr, roleFilter::setNameFilter, "name");
        roleGrid.addGridFiltering(hr, roleFilter::setDescriptionFilter, "description");

        VerticalLayout layout = new VerticalLayout();
        layout.add(selectRoleLabel, roleGrid);

        layout.setWidth("600px");
        layout.setHeight("300px");

        this.add(layout);
    }
}
