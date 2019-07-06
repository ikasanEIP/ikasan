package org.ikasan.dashboard.ui.layout;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftSubMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.ikasan.dashboard.ui.administration.view.*;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.springframework.security.core.context.SecurityContextHolder;


@Push(transport = Transport.LONG_POLLING)
@HtmlImport("frontend://styles/shared-styles.html")
@Theme(Lumo.class)
public class IkasanAppLayout extends AppLayoutRouterLayout
{

    public IkasanAppLayout()
    {
        Image ikasan = new Image("frontend/images/ikasan-titling-transparent.png", "");
        ikasan.setHeight("50px");

        IconButton logout = new IconButton(VaadinIcon.EXIT.create());
        logout.addClickListener((ComponentEventListener<ClickEvent<Div>>) divClickEvent ->
        {
            SecurityContextHolder.getContext().setAuthentication(null);
            UI.getCurrent().navigate("");
            UI.getCurrent().getSession().close();
        });

        init(AppLayoutBuilder
            .get(Behaviour.LEFT_HYBRID_SMALL)
            .withIconComponent(ikasan)
            .withAppBar(AppBarBuilder.get()
                .add(logout)
                .build())
            .withAppMenu(LeftAppMenuBuilder.get()
                .add(new LeftNavigationItem("Home", VaadinIcon.CLUSTER.create(), GraphView.class))
                .add(LeftSubMenuBuilder
                    .get("Administration", VaadinIcon.TOOLS.create())
                    .add(new LeftNavigationItem("Users", VaadinIcon.USERS.create(), UserManagementView.class))
                    .add(new LeftNavigationItem("Groups", VaadinIcon.GROUP.create(), GroupManagementView.class))
                    .add(new LeftNavigationItem("Roles", VaadinIcon.DOCTOR.create(), RoleManagementView.class))
                    .add(new LeftNavigationItem("Policies", VaadinIcon.SAFE.create(), PolicyManagementView.class))
                    .add(new LeftNavigationItem("User Directories", VaadinIcon.COG.create(), UserDirectoriesView.class))
                    .build())
                .build()
            ).build());
    }

//    public IkasanAppLayout()
//    {
//        this.setSizeFull();
//        Image ikasan = new Image("frontend/images/mr_squid_titling_dashboard.png", "");
//        ikasan.setHeight("80px");
//
//        add(ikasan);
//
//        Div div = new Div();
//        div.setWidth("100%");
//        div.setHeight("3px");
//        div.add(new Html("<hr/>"));
//
//        add(div);
//    }
}
