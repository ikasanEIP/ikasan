package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.ikasan.dashboard.ui.administration.filter.PolicyFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;

import java.util.List;

public class SelectPolicyForRoleDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;
    private FilteringGrid<Policy> policyGrid;
    private List<Policy> policiesList;
    private ListDataProvider<Policy> policyDataProvider;

    public SelectPolicyForRoleDialog(Role role, SecurityService securityService, SystemEventLogger systemEventLogger,
                                     ListDataProvider<Policy> policyDataProvider)
    {
        this.role = role;
        if(this.role == null)
        {
            throw new IllegalArgumentException("role cannot be null!");
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
        this.policyDataProvider = policyDataProvider;
        if(this.policyDataProvider == null)
        {
            throw new IllegalArgumentException("policyDataProvider cannot be null!");
        }

        init();
    }

    private void init()
    {
        H3 selectGroupLabel = new H3(getTranslation("label.select-policy", UI.getCurrent().getLocale(), null));

        policiesList = this.securityService.getAllPolicies();
        policiesList.removeAll(role.getPolicies());

        PolicyFilter policyFilter = new PolicyFilter();

        policyGrid = new FilteringGrid<>(policyFilter);
        policyGrid.setSizeFull();

        policyGrid.setClassName("my-grid");
        policyGrid.addColumn(Policy::getName).setHeader(getTranslation("table-header.policy-name", UI.getCurrent().getLocale(), null)).setKey("name").setFlexGrow(2).setSortable(true);
        policyGrid.addColumn(Policy::getDescription).setHeader(getTranslation("table-header.policy-description", UI.getCurrent().getLocale(), null)).setKey("description").setFlexGrow(3).setSortable(true);

        policyGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Policy>>) policyItemDoubleClickEvent ->
        {
            role.addPolicy(policyItemDoubleClickEvent.getItem());

            this.securityService.saveRole(role);

            String action = String.format("Policy [%s] added to role [%s].", policyItemDoubleClickEvent.getItem().getName(), role.getName());

            this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

            policiesList.removeAll(role.getPolicies());

            this.updatePolicyGrid();

            this.policyDataProvider.getItems().add(policyItemDoubleClickEvent.getItem());
            this.policyDataProvider.refreshAll();
        });

        HeaderRow hr = policyGrid.appendHeaderRow();
        policyGrid.addGridFiltering(hr, policyFilter::setNameFilter, "name");
        policyGrid.addGridFiltering(hr, policyFilter::setDescriptionFilter, "description");

        policyGrid.setSizeFull();

        this.updatePolicyGrid();

        VerticalLayout layout = new VerticalLayout();
        layout.add(selectGroupLabel, policyGrid);

        layout.setWidth("1200px");
        layout.setHeight("700px");

        this.add(layout);
    }

    private void updatePolicyGrid()
    {
        policyGrid.setItems(policiesList);
    }
}
