package org.ikasan.dashboard.ui.layout;


import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftSubMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.ikasan.dashboard.ui.administration.view.*;
import org.ikasan.dashboard.ui.general.component.AboutIkasanDialog;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.search.view.SearchView;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Locale;


@Push
@JsModule("./styles/shared-styles.js")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme(Material.class)
@PreserveOnRefresh
@PWA(name = "Ikasan Visualisation Dashboard",
    shortName = "Ikasan",
    enableInstallPrompt = false)
public class IkasanAppLayout extends AppLayoutRouterLayout<LeftLayouts.LeftHybridSmall> implements PageConfigurator
{
    private Component leftAppMenu;
    private Component leftSubmenu;
    private LeftNavigationItem searchItem;
    private LeftNavigationItem visualisationItem;
    private LeftNavigationItem userManagementItem;
    private LeftNavigationItem groupManagementItem;
    private LeftNavigationItem roleManagementItem;
    private LeftNavigationItem policyManagementItem;
    private LeftNavigationItem userDirectoryManagementItem;

    public IkasanAppLayout()
    {
        Image ikasan = new Image("frontend/images/ikasan-titling-transparent.png", "");
        ikasan.setHeight("50px");

        IconButton logout = new IconButton(VaadinIcon.SIGN_OUT.create());
        logout.getElement().setProperty("title", "Log Out");

        logout.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->
        {
            SecurityContextHolder.getContext().setAuthentication(null);
            UI.getCurrent().navigate("");
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().executeJs("window.location.href=''");
        });


        Button aboutButton = new Button(new Icon(VaadinIcon.QUESTION));
        aboutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            AboutIkasanDialog aboutIkasanDialog = new AboutIkasanDialog();
            aboutIkasanDialog.open();
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

        AppLayoutBuilder appLayoutBuilder = AppLayoutBuilder
            .get(LeftLayouts.LeftHybridSmall.class)
            .withIconComponent(ikasan)
            .withAppBar(AppBarBuilder.get()
                .add(enButton)
                .add(jpButton)
                .add(deButton)
                .add(aboutButton)
                .add(logout)
                .build());

        LeftAppMenuBuilder leftAppMenuBuilder = LeftAppMenuBuilder.get();

        this.searchItem = new LeftNavigationItem(getTranslation("menu-item.search", UI.getCurrent().getLocale(), null), VaadinIcon.SEARCH.create(), SearchView.class);
        leftAppMenuBuilder = leftAppMenuBuilder.add(searchItem);


        this.visualisationItem = new LeftNavigationItem(getTranslation("menu-item.visualisation", UI.getCurrent().getLocale(), null), VaadinIcon.CLUSTER.create(), GraphView.class);
        leftAppMenuBuilder = leftAppMenuBuilder.add(this.visualisationItem);

        LeftSubMenuBuilder leftSubMenuBuilder = LeftSubMenuBuilder
            .get(getTranslation("menu-item.administration", UI.getCurrent().getLocale(), null), VaadinIcon.TOOLS.create());

        this.userManagementItem = new LeftNavigationItem(getTranslation("menu-item.users",
            UI.getCurrent().getLocale(), null), VaadinIcon.USERS.create(), UserManagementView.class);
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.userManagementItem);

        this.groupManagementItem = new LeftNavigationItem(getTranslation("menu-item.groups",
            UI.getCurrent().getLocale(), null), VaadinIcon.GROUP.create(), GroupManagementView.class);
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.groupManagementItem);

        this.roleManagementItem = new LeftNavigationItem(getTranslation("menu-item.roles",
            UI.getCurrent().getLocale(), null), VaadinIcon.DOCTOR.create(), RoleManagementView.class);
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.roleManagementItem);

        this.policyManagementItem = new LeftNavigationItem(getTranslation("menu-item.policies",
            UI.getCurrent().getLocale(), null), VaadinIcon.SAFE.create(), PolicyManagementView.class);
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.policyManagementItem);

        this.userDirectoryManagementItem = new LeftNavigationItem(getTranslation("menu-item.user-directories",
            UI.getCurrent().getLocale(), null), VaadinIcon.COG.create(), UserDirectoriesView.class);
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.userDirectoryManagementItem);

        if(leftSubMenuBuilder != null)
        {
            this.leftSubmenu = leftSubMenuBuilder.build();
            leftAppMenuBuilder.add(this.leftSubmenu);
        }

        this.leftAppMenu = leftAppMenuBuilder.build();

        init((LeftLayouts.LeftHybridSmall)appLayoutBuilder.withAppMenu(leftAppMenu).build());
    }

    @Override
    public void onAttach(AttachEvent attachEvent)
    {
        super.onAttach(attachEvent);
        this.searchItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.SEARCH_WRITE,
            SecurityConstants.ALL_AUTHORITY));

        this.visualisationItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.WIRETAP_READ,
            SecurityConstants.ERROR_ADMIN, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE,
            SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.ERROR_WRITE, SecurityConstants.EXCLUSION_READ,
            SecurityConstants.REPLAY_ADMIN, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE,
            SecurityConstants.ALL_AUTHORITY));

        this.leftSubmenu.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_ADMINISTRATION_ADMIN, SecurityConstants.USER_ADMINISTRATION_WRITE,
            SecurityConstants.USER_ADMINISTRATION_READ, SecurityConstants.USER_DIRECTORY_ADMIN, SecurityConstants.USER_DIRECTORY_WRITE, SecurityConstants.USER_DIRECTORY_READ,
            SecurityConstants.GROUP_ADMINISTRATION_ADMIN, SecurityConstants.GROUP_ADMINISTRATION_WRITE, SecurityConstants.GROUP_ADMINISTRATION_READ,
            SecurityConstants.POLICY_ADMINISTRATION_ADMIN, SecurityConstants.POLICY_ADMINISTRATION_READ, SecurityConstants.POLICY_ADMINISTRATION_WRITE,
            SecurityConstants.ROLE_ADMINISTRATION_ADMIN, SecurityConstants.ROLE_ADMINISTRATION_READ, SecurityConstants.ROLE_ADMINISTRATION_WRITE));

        this.userManagementItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_ADMINISTRATION_ADMIN, SecurityConstants.USER_ADMINISTRATION_WRITE,
            SecurityConstants.USER_ADMINISTRATION_READ));

        this.groupManagementItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.GROUP_ADMINISTRATION_ADMIN, SecurityConstants.GROUP_ADMINISTRATION_WRITE,
            SecurityConstants.GROUP_ADMINISTRATION_READ));

        this.roleManagementItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.ROLE_ADMINISTRATION_ADMIN, SecurityConstants.ROLE_ADMINISTRATION_READ,
            SecurityConstants.ROLE_ADMINISTRATION_WRITE));

        this.policyManagementItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.POLICY_ADMINISTRATION_ADMIN, SecurityConstants.POLICY_ADMINISTRATION_READ,
            SecurityConstants.POLICY_ADMINISTRATION_WRITE));

        this.userDirectoryManagementItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_DIRECTORY_ADMIN, SecurityConstants.USER_DIRECTORY_WRITE,
            SecurityConstants.USER_DIRECTORY_READ));
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("rel", "shortcut icon");
        attributes.put("type", "image/png");
        settings.addLink("icons/icon.png", attributes);
    }
}
