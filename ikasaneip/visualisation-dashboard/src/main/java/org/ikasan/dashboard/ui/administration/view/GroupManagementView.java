package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.GroupManagementDialog;
import org.ikasan.dashboard.ui.administration.filter.GroupFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.service.SecurityService;
import org.ikasan.systemevent.service.SystemEventService;
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

    @Resource
    private SystemEventService systemEventService;

    @Resource
    private SystemEventLogger systemEventLogger;

    private FilteringGrid<IkasanPrincipalLite> groupGrid;

    private GroupFilter groupFilter = new GroupFilter();

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

        H2 groupManagementLabel = new H2(getTranslation("label.group-management", UI.getCurrent().getLocale(), null));
        add(groupManagementLabel);

        this.groupGrid = new FilteringGrid<>(groupFilter);
        this.groupGrid.setSizeFull();
        this.groupGrid.setClassName("my-grid");

        this.groupGrid.addColumn(IkasanPrincipalLite::getName).setHeader(getTranslation("table-header.group-name", UI.getCurrent().getLocale(), null)).setKey("name").setSortable(true);
        this.groupGrid.addColumn(IkasanPrincipalLite::getType).setHeader(getTranslation("table-header.group-type", UI.getCurrent().getLocale(), null)).setKey("type").setSortable(true);
        this.groupGrid.addColumn(IkasanPrincipalLite::getDescription).setHeader(getTranslation("table-header.group-description", UI.getCurrent().getLocale(), null)).setKey("description").setSortable(true);

        HeaderRow hr = groupGrid.appendHeaderRow();
        this.groupGrid.addGridFiltering(hr, groupFilter::setNameFilter, "name");
        this.groupGrid.addGridFiltering(hr, groupFilter::setTypeFilter, "type");
        this.groupGrid.addGridFiltering(hr, groupFilter::setDescriptionFilter, "description");

        this.groupGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<IkasanPrincipalLite>>) userItemDoubleClickEvent ->
        {
            GroupManagementDialog dialog = new GroupManagementDialog(userItemDoubleClickEvent.getItem()
                , this.securityService, this.systemEventService, this.systemEventLogger);

            dialog.open();
        });

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
