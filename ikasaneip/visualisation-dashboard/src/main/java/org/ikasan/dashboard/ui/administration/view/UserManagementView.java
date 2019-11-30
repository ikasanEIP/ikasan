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
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.NewRoleDialog;
import org.ikasan.dashboard.ui.administration.component.UserManagementDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.systemevent.SystemEventService;
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

        H2 userDirectoriesLabel = new H2(getTranslation("label.user-management", UI.getCurrent().getLocale(), null));

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setJustifyContentMode(JustifyContentMode.START);
        labelLayout.setVerticalComponentAlignment(Alignment.CENTER, userDirectoriesLabel);
        labelLayout.setWidth("100%");
        labelLayout.add(userDirectoriesLabel);

        Button addNewUserButton = new Button(VaadinIcon.PLUS.create());
        addNewUserButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
//            NewRoleDialog newRoleDialog = new NewRoleDialog(this.securityService, this.systemEventLogger);
//            newRoleDialog.open();
//
//            newRoleDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
//            {
//                if(dialogOpenedChangeEvent.isOpened() == false)
//                {
//                    this.updateRoles();
//                }
//            });
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMargin(true);
        buttonLayout.setVerticalComponentAlignment(Alignment.CENTER, addNewUserButton);
        buttonLayout.setWidth("100%");
        buttonLayout.add(addNewUserButton);

        HorizontalLayout wrapperLayout = new HorizontalLayout();
        wrapperLayout.setWidth("100%");

        wrapperLayout.add(labelLayout, buttonLayout);
        add(wrapperLayout);

        this.userGrid = new FilteringGrid<>(userFilter);
        this.userGrid.setSizeFull();
        this.userGrid.setClassName("my-grid");

        this.userGrid.addColumn(User::getUsername).setKey("username").setHeader(getTranslation("table-header.username", UI.getCurrent().getLocale(), null)).setSortable(true);
        this.userGrid.addColumn(User::getFirstName).setKey("firstname").setHeader(getTranslation("table-header.firstname", UI.getCurrent().getLocale(), null)).setSortable(true);
        this.userGrid.addColumn(User::getSurname).setKey("surname").setHeader(getTranslation("table-header.surname", UI.getCurrent().getLocale(), null)).setSortable(true);
        this.userGrid.addColumn(User::getEmail).setKey("email").setHeader(getTranslation("table-header.email", UI.getCurrent().getLocale(), null)).setSortable(true);
        this.userGrid.addColumn(User::getDepartment).setKey("department").setHeader(getTranslation("table-header.department", UI.getCurrent().getLocale(), null)).setSortable(true);
        this.userGrid.addColumn(TemplateRenderer.<User>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                user -> DateFormatter.getFormattedDate(user.getPreviousAccessTimestamp())))
            .setKey("lastaccess").setHeader(getTranslation("table-header.last-access", UI.getCurrent().getLocale(), null)).setSortable(true);

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
