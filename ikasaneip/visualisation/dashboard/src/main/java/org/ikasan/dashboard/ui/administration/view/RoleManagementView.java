package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.NewRoleDialog;
import org.ikasan.dashboard.ui.administration.component.RoleManagementDialog;
import org.ikasan.dashboard.ui.administration.filter.RoleFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Route(value = "roleManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
@PageTitle("Ikasan - Role Management")
public class RoleManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(RoleManagementView.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SystemEventService systemEventService;

    @Autowired
    private SystemEventLogger systemEventLogger;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleMetaDataService moduleMetadataService;

    private FilteringGrid<Role> roleGrid;

    /**
     * Constructor
     */
    public RoleManagementView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(true);

        H2 roleManagementLabel = new H2(getTranslation("label.role-management", UI.getCurrent().getLocale(), null));

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setJustifyContentMode(JustifyContentMode.START);
        labelLayout.setVerticalComponentAlignment(Alignment.CENTER, roleManagementLabel);
        labelLayout.setWidth("100%");
        labelLayout.add(roleManagementLabel);

        Button addRoleButton = new Button(VaadinIcon.PLUS.create());
        addRoleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            NewRoleDialog newRoleDialog = new NewRoleDialog(this.securityService, this.systemEventLogger);
            newRoleDialog.open();

            newRoleDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(dialogOpenedChangeEvent.isOpened() == false)
                {
                   this.updateRoles();
                }
            });
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMargin(true);
        buttonLayout.setVerticalComponentAlignment(Alignment.CENTER, addRoleButton);
        buttonLayout.setWidth("100%");
        buttonLayout.add(addRoleButton);

        HorizontalLayout wrapperLayout = new HorizontalLayout();
        wrapperLayout.setWidth("100%");

        wrapperLayout.add(labelLayout, buttonLayout);
        add(wrapperLayout);

        RoleFilter roleFilter = new RoleFilter();

        this.roleGrid = new FilteringGrid<>(roleFilter);
        this.roleGrid.setSizeFull();
        this.roleGrid.setClassName("my-grid");

        this.roleGrid.addColumn(Role::getName).setHeader(getTranslation("table-header.role-name", UI.getCurrent().getLocale(), null)).setKey("name").setSortable(true).setFlexGrow(4);
        this.roleGrid.addColumn(Role::getDescription).setHeader(getTranslation("table-header.role-description", UI.getCurrent().getLocale(), null)).setKey("description").setSortable(true).setFlexGrow(7);
        this.roleGrid.addColumn(new ComponentRenderer<>(role ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            Button trash = new TableButton(VaadinIcon.TRASH.create());
            trash.addClickListener(buttonClickEvent ->
            {
                securityService.deleteRole(role);

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_ROLE_DELETED
                    , "New role " + role.getName() + " added.", null);

                this.updateRoles();
            });

            horizontalLayout.add(trash);

            return horizontalLayout;
        })).setFlexGrow(1);

        HeaderRow hr = roleGrid.appendHeaderRow();
        roleGrid.addGridFiltering(hr, roleFilter::setNameFilter, "name");
        roleGrid.addGridFiltering(hr, roleFilter::setDescriptionFilter, "description");

        this.roleGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Role>>) userItemDoubleClickEvent ->
        {
            RoleManagementDialog dialog = new RoleManagementDialog(userItemDoubleClickEvent.getItem()
                , this.securityService, this.userService, this.systemEventService, this.systemEventLogger,
                this.moduleMetadataService);

            dialog.open();
        });

        add(this.roleGrid);
    }

    protected void updateRoles()
    {
        List<Role> roles = this.securityService.getAllRoles();

        this.roleGrid.setItems(roles);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.updateRoles();
    }
}
