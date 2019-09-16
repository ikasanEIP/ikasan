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

    public SelectPolicyForRoleDialog(Role role, SecurityService securityService, SystemEventLogger systemEventLogger)
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

        init();
    }

    private void init()
    {
        List<Policy> policiesList = this.securityService.getAllPolicies();

        policiesList.removeAll(role.getPolicies());

        PolicyFilter policyFilter = new PolicyFilter();

        FilteringGrid<Policy> policyGrid = new FilteringGrid<>(policyFilter);
        policyGrid.setSizeFull();

        policyGrid.setClassName("my-grid");
        policyGrid.addColumn(Policy::getName).setHeader("Name").setKey("name").setFlexGrow(2).setSortable(true);
        policyGrid.addColumn(Policy::getDescription).setHeader("Description").setKey("description").setFlexGrow(3).setSortable(true);
        policyGrid.addColumn(new ComponentRenderer<>(policy ->
        {
            Button addRoleButton = new Button(VaadinIcon.PLUS.create());

            addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                role.addPolicy(policy);

                this.securityService.saveRole(role);

                String action = String.format("Policy [%s] added to role [%s].", policy.getName(), role.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.close();
            });

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(addRoleButton);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, addRoleButton);
            return verticalLayout;
        })).setFlexGrow(1);

        HeaderRow hr = policyGrid.appendHeaderRow();
        policyGrid.addGridFiltering(hr, policyFilter::setNameFilter, "name");
        policyGrid.addGridFiltering(hr, policyFilter::setDescriptionFilter, "description");

        policyGrid.setItems(policiesList);

        policyGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(policyGrid);

        layout.setWidth("600px");
        layout.setHeight("300px");

        this.add(layout);
    }
}
