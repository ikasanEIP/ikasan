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
import org.ikasan.dashboard.ui.administration.filter.GroupFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;

import java.util.List;
import java.util.stream.Collectors;

public class SelectGroupForRoleDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;
    private List<IkasanPrincipalLite> associatedGroups;

    public SelectGroupForRoleDialog(Role role, List<IkasanPrincipalLite> associatedGroups, SecurityService securityService
        , SystemEventLogger systemEventLogger)
    {
        this.role = role;
        if(this.role == null)
        {
            throw new IllegalArgumentException("role cannot be null!");
        }
        this.associatedGroups = associatedGroups;
        if(this.associatedGroups == null)
        {
            throw new IllegalArgumentException("associatedGroups cannot be null!");
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
        List<IkasanPrincipalLite> principals = this.securityService.getAllPrincipalLites();
        principals.removeAll(associatedGroups);

        GroupFilter groupFilter = new GroupFilter();
        
        FilteringGrid<IkasanPrincipalLite> groupGrid = new FilteringGrid<>(groupFilter);
        groupGrid.setSizeFull();
        groupGrid.setClassName("my-grid");

        groupGrid.addColumn(IkasanPrincipalLite::getName).setHeader("Name").setKey("name").setSortable(true).setFlexGrow(2);
        groupGrid.addColumn(IkasanPrincipalLite::getDescription).setHeader("Description").setKey("description").setSortable(true).setFlexGrow(8);
        groupGrid.addColumn(new ComponentRenderer<>(principalLite ->
        {
            Button addGroupButton = new Button(VaadinIcon.PLUS.create());

            addGroupButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                IkasanPrincipal ikasanPrincipal = this.securityService.findPrincipalByName(principalLite.getName());
                ikasanPrincipal.addRole(this.role);

                this.securityService.savePrincipal(ikasanPrincipal);

                String action = String.format("Role [%s] added to user [%s].", role.getName(), ikasanPrincipal.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                this.close();
            });

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(addGroupButton);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, addGroupButton);
            return verticalLayout;
        })).setFlexGrow(1);

        HeaderRow hr = groupGrid.appendHeaderRow();
        groupGrid.addGridFiltering(hr, groupFilter::setNameFilter, "name");
        groupGrid.addGridFiltering(hr, groupFilter::setDescriptionFilter, "description");

        groupGrid.setItems(principals.stream()
            .filter(principal -> principal.getType() != null && principal.getType().equals("application"))
            .collect(Collectors.toList()));

        groupGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(groupGrid);

        layout.setWidth("1000px");
        layout.setHeight("500px");

        this.add(layout);
    }
}
