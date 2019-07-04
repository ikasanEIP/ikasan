package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Route(value = "userManagement", layout = IkasanAppLayout.class)
@VaadinSessionScope
@Component
public class UserManagementView extends VerticalLayout
{
    private Logger logger = LoggerFactory.getLogger(UserManagementView.class);


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
        this.setWidth("100%");
        this.setSpacing(true);

        H2 userDirectories = new H2("User Management");
        add(userDirectories);
    }
}
