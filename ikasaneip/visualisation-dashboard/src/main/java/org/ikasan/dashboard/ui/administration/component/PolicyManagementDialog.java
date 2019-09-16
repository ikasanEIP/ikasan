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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.dashboard.ui.administration.filter.RoleFilter;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.*;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;


public class PolicyManagementDialog extends Dialog
{
    private Policy policy;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;
    private FilteringGrid<Role> roleGrid;

    /**
     * Constructor
     */
    public PolicyManagementDialog(Policy policy, SecurityService securityService,
                                  SystemEventLogger systemEventLogger)
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
        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1.5), new Flex(2.5))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(initPolicyForm(), 1, 1, 1, 1)
            .withRowAndColumn(createRolesAccessGrid(), 2, 1, 2, 1)
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

        RoleFilter roleFilter = new RoleFilter();

        roleGrid = new FilteringGrid<>(roleFilter);
        roleGrid.setClassName("my-grid");
        roleGrid.addColumn(Role::getName).setKey("name").setHeader("Name").setSortable(true).setFlexGrow(1);
        roleGrid.addColumn(Role::getDescription).setKey("description").setHeader("Description").setSortable(true).setFlexGrow(4);
        roleGrid.addColumn(new ComponentRenderer<>(role->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                policy.getRoles().remove(role);
                securityService.savePolicy(policy);

                String action = String.format("Role [%s] removed from policy [%s].", role.getName(), policy.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.updateRolesGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        HeaderRow hr = roleGrid.appendHeaderRow();
        roleGrid.addGridFiltering(hr, roleFilter::setNameFilter, "name");
        roleGrid.addGridFiltering(hr, roleFilter::setDescriptionFilter, "description");

        roleGrid.setSizeFull();

        this.updateRolesGrid();

        Button addRoleButton = new Button("Associate policy with role");
        addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            SelectRoleForPolicyDialog dialog = new SelectRoleForPolicyDialog(policy, this.securityService, this.systemEventLogger);
            dialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                    this.updateRolesGrid();
                }
            });

            dialog.open();
        });

        ComponentSecurityVisibility.applySecurity(addRoleButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.USER_ADMINISTRATION_ADMIN
            , SecurityConstants.USER_ADMINISTRATION_WRITE);

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
        roleGrid.setItems(this.policy.getRoles());
    }


    private VerticalLayout initPolicyForm()
    {
        H3 userProfileLabel = new H3("Policy Profile - " + this.policy.getName());

        FormLayout formLayout = new FormLayout();

        TextField policyName = new TextField("Name");
        policyName.setValue(this.policy.getName());
        policyName.setReadOnly(true);
        formLayout.add(policyName);
        formLayout.setColspan(policyName, 1);

        TextArea description = new TextArea("Description");
        description.setValue(this.policy.getDescription());
        description.setHeight("150px");
        description.setReadOnly(true);
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
