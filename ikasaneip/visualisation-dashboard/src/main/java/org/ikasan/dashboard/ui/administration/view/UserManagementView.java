package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.UserManagementDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.systemevent.service.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Grid<User> userGrid;

    private UserFilter userFilter;

    DataProvider<User, UserFilter> dataProvider;
    ConfigurableFilterDataProvider<User,Void,UserFilter> filteredDataProvider;

    List<User> users;

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

        this.userGrid = new Grid<>();
        this.userGrid.setSizeFull();
        this.userGrid.setClassName("my-grid");

        userFilter = new UserFilter();

        this.userGrid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);

        this.userGrid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        this.userGrid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        this.userGrid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        this.userGrid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        this.userGrid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access").setSortable(true);

        HeaderRow hr = userGrid.appendHeaderRow();
        this.addGridFiltering(hr, userFilter::setUsernameFilter, "username");
        this.addGridFiltering(hr, userFilter::setNameFilter, "firstname");
        this.addGridFiltering(hr, userFilter::setLastNameFilter, "surname");
        this.addGridFiltering(hr, userFilter::setEmailFilter, "email");
        this.addGridFiltering(hr, userFilter::setDepartmentFilter, "department");

        this.userGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<User>>) userItemDoubleClickEvent ->
        {
            UserManagementDialog dialog = new UserManagementDialog(userItemDoubleClickEvent.getItem(), userService
                , this.securityService, this.systemEventService, this.systemEventLogger);

            dialog.open();
        });

        add(this.userGrid);
    }

    protected void addGridFiltering(HeaderRow hr, Consumer<String> setFilter, String columnKey)
    {
        TextField textField = new TextField();
        textField.setWidthFull();

        textField.addValueChangeListener(ev->{

            setFilter.accept(ev.getValue());

            filteredDataProvider.refreshAll();
        });

        hr.getCell(userGrid.getColumnByKey(columnKey)).setComponent(textField);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(users != null)
        {
            return;
        }

        this.users = this.userService.getUsers();

        dataProvider = DataProvider.fromFilteringCallbacks(query ->
        {

            Optional<UserFilter> filter = query.getFilter();

            logger.info("Filtering" + query.getOffset());
            logger.info("Filtering" + query.getOffset());

            if(filter.isPresent())
            {
                if(query.getSortOrders() != null)
                {
                    query.getSortOrders().forEach(querySortOrder -> System.out.println(querySortOrder.getSorted() + " " + querySortOrder.getDirection()));
                }

                return getFilterStream(filter)
                    .skip(query.getOffset())
                    .limit(query.getLimit());
            }
            else
            {
                return users.stream().limit(query.getLimit());
            }
        }, query ->
        {

            Optional<UserFilter> filter = query.getFilter();

            if(filter.isPresent())
            {
                return getFilterStream(filter)
                    .collect(Collectors.toList()).size();
            }
            else
            {
                return users.size();
            }

        });

        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(userFilter);

        this.userGrid.setDataProvider(filteredDataProvider);
    }

    private Stream<User> getFilterStream(Optional<UserFilter> filter)
    {
        return users
            .stream()
            .filter(user ->
            {
                if(filter.get().getUsernameFilter() == null || filter.get().getUsernameFilter().isEmpty() || user.getUsername() == null)
                {
                    return true;
                }
                else
                {
                    return user.getUsername().toLowerCase().startsWith(filter.get().getUsernameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(filter.get().getNameFilter() == null || filter.get().getNameFilter().isEmpty() || user.getFirstName() == null)
                {
                    return true;
                }
                else
                {
                    return user.getFirstName().toLowerCase().startsWith(filter.get().getNameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(filter.get().getLastNameFilter() == null || filter.get().getLastNameFilter().isEmpty() || user.getSurname() == null)
                {
                    return true;
                }
                else
                {
                    return user.getSurname().toLowerCase().startsWith(filter.get().getLastNameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(filter.get().getEmailFilter() == null || filter.get().getEmailFilter().isEmpty() || user.getEmail() == null)
                {
                    return true;
                }
                else
                {
                    return user.getEmail().toLowerCase().startsWith(filter.get().getEmailFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(filter.get().getDepartmentFilter() == null || filter.get().getDepartmentFilter().isEmpty() || user.getDepartment() == null)
                {
                    return true;
                }
                else
                {
                    return user.getDepartment().toLowerCase().startsWith(filter.get().getDepartmentFilter().toLowerCase());
                }
            });
    }

    private class UserFilter
    {
        private String usernameFilter = null;
        private String nameFilter = null;
        private String lastNameFilter = null;
        private String emailFilter = null;
        private String departmentFilter;


        public String getNameFilter()
        {
            return nameFilter;
        }

        public String getLastNameFilter()
        {

            return lastNameFilter;

        }

        public void setNameFilter(String nameFilter)
        {

            this.nameFilter = nameFilter;

        }

        public void setLastNameFilter(String lastNameFilter)
        {

            this.lastNameFilter = lastNameFilter;

        }

        public String getUsernameFilter()
        {
            return usernameFilter;
        }

        public void setUsernameFilter(String usernameFilter)
        {
            this.usernameFilter = usernameFilter;
        }

        public String getEmailFilter()
        {
            return emailFilter;
        }

        public void setEmailFilter(String emailFilter)
        {
            this.emailFilter = emailFilter;
        }

        public String getDepartmentFilter()
        {
            return departmentFilter;
        }

        public void setDepartmentFilter(String departmentFilter)
        {
            this.departmentFilter = departmentFilter;
        }
    }
}
