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
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.ikasan.dashboard.ui.administration.view.*;
import org.ikasan.dashboard.ui.home.view.HomeView;
import org.ikasan.dashboard.ui.search.SearchView;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.ikasan.dashboard.ui.visualisation.view.MapView;
import org.ikasan.dashboard.ui.visualisation.view.ModuleView;
import org.springframework.security.core.context.SecurityContextHolder;


@Push(transport = Transport.LONG_POLLING)
@HtmlImport("frontend://styles/shared-styles.html")
@HtmlImport("frontend://bower_components/vaadin-lumo-styles/presets/compact.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme(Lumo.class)
public class IkasanAppLayout extends AppLayoutRouterLayout
{

    public IkasanAppLayout()
    {
        Image ikasan = new Image("frontend/images/ikasan-titling-transparent.png", "");
        ikasan.setHeight("50px");

        IconButton logout = new IconButton(VaadinIcon.SIGN_OUT.create());
        logout.getElement().setProperty("title", "Log Out");

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
                .add(new LeftNavigationItem("Home", VaadinIcon.HOME.create(), HomeView.class))
                .add(new LeftNavigationItem("Map", VaadinIcon.GLOBE.create(), MapView.class))
                .add(new LeftNavigationItem("Search", VaadinIcon.SEARCH.create(), SearchView.class))
                .add(new LeftNavigationItem("Visualisation", VaadinIcon.CLUSTER.create(), GraphView.class))
                .add(new LeftNavigationItem("Integration Modules", VaadinIcon.MODAL.create(), ModuleView.class))
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

}
