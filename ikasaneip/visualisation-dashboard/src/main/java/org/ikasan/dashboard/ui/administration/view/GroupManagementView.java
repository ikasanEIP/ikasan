package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "groupManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class GroupManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(GroupManagementView.class);

    @Resource
    private SecurityService securityService;

    private Grid<IkasanPrincipalLite> groupGrid;

    /**
     * Constructor
     */
    public GroupManagementView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(true);

        H2 groupManagementLabel = new H2("Group Management");
        add(groupManagementLabel);

        this.groupGrid = new Grid<>();
        this.groupGrid.setSizeFull();
        this.groupGrid.setClassName("my-grid");

        this.groupGrid.addColumn(IkasanPrincipalLite::getName).setHeader("Name");
        this.groupGrid.addColumn(IkasanPrincipalLite::getType).setHeader("Type");
        this.groupGrid.addColumn(IkasanPrincipalLite::getDescription).setHeader("Description");

        add(this.groupGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        List<IkasanPrincipalLite> principals = this.securityService.getAllPrincipalLites();

        this.groupGrid.setItems(principals.stream()
            .filter(principal -> principal.getType() != null && principal.getType().equals("application"))
            .collect(Collectors.toList()));
    }
}
