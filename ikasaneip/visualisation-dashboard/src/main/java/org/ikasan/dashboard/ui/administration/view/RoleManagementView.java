package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Route(value = "roleManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class RoleManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(RoleManagementView.class);

    @Resource
    private SecurityService securityService;

    private Grid<Role> roleGrid;

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

        H2 roleManagementLabel = new H2("Role Management");

        Button addRoleButton = new Button(VaadinIcon.PLUS.create());

        HorizontalLayout leftLayout = new HorizontalLayout();
        leftLayout.setJustifyContentMode(JustifyContentMode.START);
        leftLayout.setWidth("100%");
        leftLayout.add(roleManagementLabel);
        leftLayout.setVerticalComponentAlignment(Alignment.CENTER, roleManagementLabel);

        HorizontalLayout rightLayout = new HorizontalLayout();
        rightLayout.setJustifyContentMode(JustifyContentMode.END);
        rightLayout.setWidth("100%");
        rightLayout.add(addRoleButton);
        rightLayout.setVerticalComponentAlignment(Alignment.CENTER, addRoleButton);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        layout.add(leftLayout, rightLayout);
        add(layout);


        this.roleGrid = new Grid<>();
        this.roleGrid.setSizeFull();
        this.roleGrid.setClassName("my-grid");

        this.roleGrid.addColumn(Role::getName).setHeader("Name");
        this.roleGrid.addColumn(Role::getDescription).setHeader("Description");
        this.roleGrid.addColumn(new ComponentRenderer<>(role ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            Button trash = new Button(VaadinIcon.TRASH.create());
            trash.getStyle().set("width", "40px");
            trash.getStyle().set("height", "40px");
            trash.getStyle().set("font-size", "16pt");
            trash.addClickListener(buttonClickEvent ->
            {
                securityService.deleteRole(role);
            });

            horizontalLayout.add(trash);

            return horizontalLayout;
        }));

        add(this.roleGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        List<Role> roles = this.securityService.getAllRoles();

        this.roleGrid.setItems(roles);
    }
}
