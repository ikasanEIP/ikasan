package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.ikasan.dashboard.ui.administration.filter.GroupFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.UserLite;
import org.ikasan.security.service.SecurityService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SelectGroupForRoleDialog extends Dialog
{
    private Role role;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;
    private List<IkasanPrincipalLite> associatedGroups;
    private FilteringGrid<IkasanPrincipalLite> ikasanPrincipalLiteFilteringGrid;

    public SelectGroupForRoleDialog(Role role, List<IkasanPrincipalLite> associatedGroups, SecurityService securityService
        , SystemEventLogger systemEventLogger, FilteringGrid<IkasanPrincipalLite> ikasanPrincipalLiteFilteringGrid)
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
        this.ikasanPrincipalLiteFilteringGrid = ikasanPrincipalLiteFilteringGrid;
        if(this.ikasanPrincipalLiteFilteringGrid == null)
        {
            throw new IllegalArgumentException("groupDataProvider cannot be null!");
        }

        init();
    }

    private void init()
    {
        H3 selectGroupLabel = new H3(getTranslation("label.select-group", UI.getCurrent().getLocale(), null));

        List<IkasanPrincipalLite> principals = this.securityService.getAllPrincipalLites();
        principals.removeAll(associatedGroups);

        GroupFilter groupFilter = new GroupFilter();
        
        FilteringGrid<IkasanPrincipalLite> groupGrid = new FilteringGrid<>(groupFilter);
        groupGrid.setSizeFull();
        groupGrid.setClassName("my-grid");

        groupGrid.setItems(principals.stream()
            .filter(principal -> principal.getType() != null && principal.getType().equals("application"))
            .collect(Collectors.toList()));

        groupGrid.addColumn(IkasanPrincipalLite::getName)
            .setHeader(getTranslation("table-header.group-name", UI.getCurrent().getLocale(), null))
            .setKey("name")
            .setSortable(true)
            .setFlexGrow(2);
        groupGrid.addColumn(IkasanPrincipalLite::getDescription)
            .setHeader(getTranslation("table-header.group-description", UI.getCurrent().getLocale(), null))
            .setKey("description")
            .setSortable(true)
            .setFlexGrow(8);

        groupGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<IkasanPrincipalLite>>) ikasanPrincipalLiteItemDoubleClickEvent ->
        {
                IkasanPrincipal ikasanPrincipal = this.securityService.findPrincipalByName(ikasanPrincipalLiteItemDoubleClickEvent.getItem().getName());
                ikasanPrincipal.addRole(this.role);

                this.securityService.savePrincipal(ikasanPrincipal);

                String action = String.format("Role [%s] added to user [%s].", role.getName(), ikasanPrincipal.getName());

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_PRINCIPAL_ROLE_CHANGED_CONSTANTS, action, null);

                Collection<IkasanPrincipalLite> items = this.ikasanPrincipalLiteFilteringGrid.getItems();
                items.add(ikasanPrincipalLiteItemDoubleClickEvent.getItem());

                this.ikasanPrincipalLiteFilteringGrid.setItems(items);
                this.ikasanPrincipalLiteFilteringGrid.getDataProvider().refreshAll();

                groupGrid.getItems().remove(ikasanPrincipalLiteItemDoubleClickEvent.getItem());
                groupGrid.getDataProvider().refreshAll();
        });

        HeaderRow hr = groupGrid.appendHeaderRow();
        groupGrid.addGridFiltering(hr, groupFilter::setNameFilter, "name");
        groupGrid.addGridFiltering(hr, groupFilter::setDescriptionFilter, "description");

        groupGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(selectGroupLabel, groupGrid);

        layout.setWidth("1200px");
        layout.setHeight("700px");

        this.add(layout);
    }
}
