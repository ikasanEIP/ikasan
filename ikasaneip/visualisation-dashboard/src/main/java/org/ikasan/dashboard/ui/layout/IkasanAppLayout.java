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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.ikasan.dashboard.ui.administration.view.*;
import org.ikasan.dashboard.ui.home.view.HomeView;
import org.ikasan.dashboard.ui.search.view.SearchView;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.ikasan.dashboard.ui.visualisation.view.MapView;
import org.ikasan.dashboard.ui.visualisation.view.ModuleView;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;


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

        Button jpButton = new Button("JP", new Icon(VaadinIcon.ARROW_RIGHT));
        jpButton.setIconAfterText(true);

        Button enButton = new Button("EN", new Icon(VaadinIcon.ARROW_RIGHT));
        enButton.setIconAfterText(true);

        Button deButton = new Button("DE", new Icon(VaadinIcon.ARROW_RIGHT));
        deButton.setIconAfterText(true);


        jpButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->UI.getCurrent().setLocale(Locale.JAPAN));
        enButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->UI.getCurrent().setLocale(Locale.ENGLISH));
        deButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->UI.getCurrent().setLocale(Locale.GERMAN));

        init(AppLayoutBuilder
            .get(Behaviour.LEFT_HYBRID_SMALL)
            .withIconComponent(ikasan)
            .withAppBar(AppBarBuilder.get()
                .add(enButton)
                .add(jpButton)
                .add(deButton)
                .add(logout)
                .build())
            .withAppMenu(LeftAppMenuBuilder.get()
//                .add(new LeftNavigationItem(getTranslation("menu-item.home", UI.getCurrent().getLocale(), null), VaadinIcon.HOME.create(), HomeView.class))
//                .add(new LeftNavigationItem(getTranslation("menu-item.map", UI.getCurrent().getLocale(), null), VaadinIcon.GLOBE.create(), MapView.class))
                .add(new LeftNavigationItem(getTranslation("menu-item.search", UI.getCurrent().getLocale(), null), VaadinIcon.SEARCH.create(), SearchView.class))
                .add(new LeftNavigationItem(getTranslation("menu-item.visualisation", UI.getCurrent().getLocale(), null), VaadinIcon.CLUSTER.create(), GraphView.class))
//                .add(new LeftNavigationItem(getTranslation("menu-item.integration-modules", UI.getCurrent().getLocale(), null), VaadinIcon.MODAL.create(), ModuleView.class))
                .add(LeftSubMenuBuilder
                    .get(getTranslation("menu-item.administration", UI.getCurrent().getLocale(), null), VaadinIcon.TOOLS.create())
                    .add(new LeftNavigationItem(getTranslation("menu-item.users", UI.getCurrent().getLocale(), null), VaadinIcon.USERS.create(), UserManagementView.class))
                    .add(new LeftNavigationItem(getTranslation("menu-item.groups", UI.getCurrent().getLocale(), null), VaadinIcon.GROUP.create(), GroupManagementView.class))
                    .add(new LeftNavigationItem(getTranslation("menu-item.roles", UI.getCurrent().getLocale(), null), VaadinIcon.DOCTOR.create(), RoleManagementView.class))
                    .add(new LeftNavigationItem(getTranslation("menu-item.policies", UI.getCurrent().getLocale(), null), VaadinIcon.SAFE.create(), PolicyManagementView.class))
                    .add(new LeftNavigationItem(getTranslation("menu-item.user-directories", UI.getCurrent().getLocale(), null), VaadinIcon.COG.create(), UserDirectoriesView.class))
                    .build())
                .build()
            ).build());
    }

}
