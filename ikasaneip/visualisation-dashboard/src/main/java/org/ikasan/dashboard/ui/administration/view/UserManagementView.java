package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.UserManagementDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.systemevent.service.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.ikasan.dashboard.ui.general.component.FilteringGrid;
import org.ikasan.dashboard.ui.administration.filter.UserFilter;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Route(value = "userManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class UserManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(UserManagementView.class);

    @Resource
    private UserService userService;

    @Resource
    private SecurityService securityService;

    @Resource
    private SystemEventService systemEventService;

    @Resource
    private SystemEventLogger systemEventLogger;

    private FilteringGrid<User> userGrid;

    private DataProvider<User, UserFilter> dataProvider;
    private ConfigurableFilterDataProvider<User,Void,UserFilter> filteredDataProvider;

    private List<User> users;

    private UserFilter userFilter = new UserFilter();

    /**
     * Constructor
     */
    public UserManagementView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(true);

        H2 userDirectories = new H2("User Management");
        add(userDirectories);

        this.userGrid = new FilteringGrid<>(userFilter);
        this.userGrid.setSizeFull();
        this.userGrid.setClassName("my-grid");

        this.userGrid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);
        this.userGrid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        this.userGrid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        this.userGrid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        this.userGrid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        this.userGrid.addColumn(TemplateRenderer.<User>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                user -> DateFormatter.getFormattedDate(user.getPreviousAccessTimestamp())))
            .setKey("lastaccess").setHeader("Last Access").setSortable(true);

        HeaderRow hr = userGrid.appendHeaderRow();
        this.userGrid.addGridFiltering(hr, userFilter::setUsernameFilter, "username");
        this.userGrid.addGridFiltering(hr, userFilter::setNameFilter, "firstname");
        this.userGrid.addGridFiltering(hr, userFilter::setLastNameFilter, "surname");
        this.userGrid.addGridFiltering(hr, userFilter::setEmailFilter, "email");
        this.userGrid.addGridFiltering(hr, userFilter::setDepartmentFilter, "department");

        this.userGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<User>>) userItemDoubleClickEvent ->
        {
            UserManagementDialog dialog = new UserManagementDialog(userItemDoubleClickEvent.getItem(), userService
                , this.securityService, this.systemEventService, this.systemEventLogger);

            dialog.open();
        });

        add(this.userGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(users != null)
        {
            return;
        }

        this.users = this.userService.getUsers();

        this.userGrid.setItems(users);
    }
}
