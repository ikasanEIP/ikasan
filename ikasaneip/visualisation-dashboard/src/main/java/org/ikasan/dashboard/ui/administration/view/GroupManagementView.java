package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Route(value = "groupManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class GroupManagementView extends VerticalLayout
{
    private Logger logger = LoggerFactory.getLogger(GroupManagementView.class);


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
        this.setWidth("100%");
        this.setSpacing(true);

        H2 userDirectories = new H2("Group Management");
        add(userDirectories);
    }
}
