package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Route(value = "userManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class UserManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(UserManagementView.class);

    @Resource
    private UserService userService;

    private Grid<User> userGrid;

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

        this.userGrid.addColumn(User::getUsername).setHeader("Username");
        this.userGrid.addColumn(User::getFirstName).setHeader("First Name");
        this.userGrid.addColumn(User::getSurname).setHeader("Surname");
        this.userGrid.addColumn(User::getEmail).setHeader("Email");
        this.userGrid.addColumn(User::getDepartment).setHeader("Department");
        this.userGrid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access");

        add(this.userGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        List<User> users = this.userService.getUsers();

        this.userGrid.setItems(users);
    }
}
