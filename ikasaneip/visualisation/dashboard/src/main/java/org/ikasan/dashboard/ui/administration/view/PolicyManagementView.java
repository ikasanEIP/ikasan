package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.ikasan.dashboard.ui.administration.component.PolicyManagementDialog;
import org.ikasan.dashboard.ui.administration.component.RoleManagementDialog;
import org.ikasan.dashboard.ui.administration.filter.PolicyFilter;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Route(value = "policyManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
@PageTitle("Ikasan - Policy Management")
public class PolicyManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(PolicyManagementView.class);

    @Resource
    private SecurityService securityService;

    @Resource
    private SystemEventLogger systemEventLogger;

    private FilteringGrid<Policy> policyGrid;

    /**
     * Constructor
     */
    public PolicyManagementView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(true);

        H2 policyManagementLabel = new H2(getTranslation("label.policy-management", UI.getCurrent().getLocale(), null));

        HorizontalLayout leftLayout = new HorizontalLayout();
        leftLayout.setJustifyContentMode(JustifyContentMode.START);
        leftLayout.setWidth("100%");
        leftLayout.add(policyManagementLabel);
        leftLayout.setVerticalComponentAlignment(Alignment.CENTER, policyManagementLabel);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        layout.add(leftLayout);
        add(layout);

        PolicyFilter policyFilter = new PolicyFilter();

        this.policyGrid = new FilteringGrid<>(policyFilter);
        this.policyGrid.setSizeFull();
        this.policyGrid.setClassName("my-grid");

        this.policyGrid.addColumn(Policy::getName).setHeader(getTranslation("table-header.policy-name", UI.getCurrent().getLocale(), null)).setKey("name").setSortable(true).setFlexGrow(4);
        this.policyGrid.addColumn(Policy::getDescription).setHeader(getTranslation("table-header.policy-description", UI.getCurrent().getLocale(), null)).setKey("description").setSortable(true).setFlexGrow(7);

        HeaderRow hr = this.policyGrid.appendHeaderRow();
        this.policyGrid.addGridFiltering(hr, policyFilter::setNameFilter, "name");
        this.policyGrid.addGridFiltering(hr, policyFilter::setDescriptionFilter, "description");

        this.policyGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Policy>>) userItemDoubleClickEvent ->
        {
            PolicyManagementDialog dialog = new PolicyManagementDialog(userItemDoubleClickEvent.getItem()
                , this.securityService, this.systemEventLogger);

            dialog.open();
        });

        add(this.policyGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        List<Policy> policies = this.securityService.getAllPolicies();

        this.policyGrid.setItems(policies);
    }
}
