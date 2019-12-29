package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.administration.filter.ModuleFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.RoleModule;
import org.ikasan.security.service.SecurityService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;

import java.util.List;
import java.util.stream.Collectors;

public class SelectModuleForRoleDialog extends Dialog
{
    private Role role;
    private ModuleMetaDataService moduleMetadataService;
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;

    public SelectModuleForRoleDialog(Role role, ModuleMetaDataService moduleMetadataService,  SecurityService securityService
        , SystemEventLogger systemEventLogger)
    {
        this.role = role;
        if(this.role == null)
        {
            throw new IllegalArgumentException("role cannot be null!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null)
        {
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
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
        H3 selectUserLabel = new H3(getTranslation("label.select-module", UI.getCurrent().getLocale(), null));

        List<ModuleMetaData> moduleMetaDataList = this.moduleMetadataService.findAll();
        moduleMetaDataList = this.removeAlreadyAssociatedModules(moduleMetaDataList);

        ModuleFilter moduleFilter = new ModuleFilter();

        FilteringGrid<ModuleMetaData> userGrid = new FilteringGrid<>(moduleFilter);
        userGrid.setSizeFull();

        userGrid.addColumn(ModuleMetaData::getName)
            .setKey("name")
            .setHeader(getTranslation("table-header.moduleName", UI.getCurrent().getLocale(), null))
            .setSortable(true)
            .setFlexGrow(2);

        userGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<ModuleMetaData>>) moduleItemDoubleClickEvent ->
        {
            RoleModule roleModule = new RoleModule();
            roleModule.setRole(this.role);
            roleModule.setModuleName(moduleItemDoubleClickEvent.getItem().getName());

            role.addRoleModule(roleModule);

            this.securityService.saveRole(this.role);

            String action = String.format("Module [%s] added to role [%s].", moduleItemDoubleClickEvent.getItem().getName(), role.getName());

            this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_MODULE_ROLE_CHANGE_CONSTANTS, action, null);

            this.close();
        });

        HeaderRow hr = userGrid.appendHeaderRow();
        userGrid.addGridFiltering(hr, moduleFilter::setModuleNameFilter, "name");

        userGrid.setItems(moduleMetaDataList);

        userGrid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(selectUserLabel, userGrid);

        layout.setWidth("1200px");
        layout.setHeight("500px");

        this.add(layout);
    }

    protected List<ModuleMetaData> removeAlreadyAssociatedModules(List<ModuleMetaData> moduleMetaDataList)
    {
        List<String> modules = role.getRoleModules().stream()
            .map(roleModule -> roleModule.getModuleName()).collect(Collectors.toList());

        return moduleMetaDataList.stream()
            .filter(moduleMetaData -> !modules.contains(moduleMetaData.getName()))
            .collect(Collectors.toList());
    }
}
