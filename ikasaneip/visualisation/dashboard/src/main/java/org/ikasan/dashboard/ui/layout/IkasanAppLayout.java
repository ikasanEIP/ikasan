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
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.dashboard.ui.visualisation.view.BusinessStreamDesignerView;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
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
    @Resource
    private SystemEventLogger systemEventLogger;

    private Component leftAppMenu;
    private Component leftSubmenu;
    private LeftNavigationItem searchMenuItem;
    private LeftNavigationItem visualisationMenuItem;
    private LeftNavigationItem systemEventMenuItem;
    private LeftNavigationItem userManagementMenuItem;
    private LeftNavigationItem groupManagementMenuItem;
    private LeftNavigationItem roleManagementMenuItem;
    private LeftNavigationItem policyManagementMenuItem;
    private LeftNavigationItem userDirectoryManagementMenuItem;
    private LeftNavigationItem businessStreamDesignerMenuItem;

    public IkasanAppLayout()
    {
        Image ikasan = new Image("frontend/images/ikasan-titling-transparent.png", "");
        ikasan.setHeight("30px");

        IconButton logout = new IconButton(VaadinIcon.SIGN_OUT.create());
        logout.getElement().setProperty("title", "Log Out");
        logout.setId("logoutButton");

        logout.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->
        {
            IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext()
                .getAuthentication();
            this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_LOGOUT_CONSTANTS
                , SystemEventConstants.DASHBOARD_LOGOUT_CONSTANTS, authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(null);
            UI.getCurrent().navigate("");
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().executeJs("window.location.href=''");
        });


        Button aboutButton = new Button(new Icon(VaadinIcon.QUESTION));
        aboutButton.setId("aboutButton");
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

        this.searchMenuItem = new LeftNavigationItem(getTranslation("menu-item.search", UI.getCurrent().getLocale(), null), VaadinIcon.SEARCH.create(), SearchView.class);
        this.searchMenuItem.setId("searchMenuItem");
        leftAppMenuBuilder = leftAppMenuBuilder.add(searchMenuItem);


        this.visualisationMenuItem = new LeftNavigationItem(getTranslation("menu-item.visualisation", UI.getCurrent().getLocale(), null), VaadinIcon.CLUSTER.create(), GraphView.class);
        this.visualisationMenuItem.setId("visualisationMenuItem");

        leftAppMenuBuilder = leftAppMenuBuilder.add(this.visualisationMenuItem);

        LeftSubMenuBuilder leftSubMenuBuilder = LeftSubMenuBuilder
            .get(getTranslation("menu-item.administration", UI.getCurrent().getLocale(), null), VaadinIcon.TOOLS.create());

        this.systemEventMenuItem = new LeftNavigationItem(getTranslation("menu-item.administration-events", UI.getCurrent().getLocale(), null), VaadinIcon.CROSSHAIRS.create(), AdministrationSearchView.class);
        this.systemEventMenuItem.setId("systemEventMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.systemEventMenuItem);

        this.userManagementMenuItem = new LeftNavigationItem(getTranslation("menu-item.users",
            UI.getCurrent().getLocale(), null), VaadinIcon.USERS.create(), UserManagementView.class);
        this.userManagementMenuItem.setId("userManagementMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.userManagementMenuItem);

        this.groupManagementMenuItem = new LeftNavigationItem(getTranslation("menu-item.groups",
            UI.getCurrent().getLocale(), null), VaadinIcon.GROUP.create(), GroupManagementView.class);
        this.groupManagementMenuItem.setId("groupManagementMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.groupManagementMenuItem);

        this.roleManagementMenuItem = new LeftNavigationItem(getTranslation("menu-item.roles",
            UI.getCurrent().getLocale(), null), VaadinIcon.DOCTOR.create(), RoleManagementView.class);
        this.roleManagementMenuItem.setId("roleManagementMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.roleManagementMenuItem);

        this.policyManagementMenuItem = new LeftNavigationItem(getTranslation("menu-item.policies",
            UI.getCurrent().getLocale(), null), VaadinIcon.SAFE.create(), PolicyManagementView.class);
        this.policyManagementMenuItem.setId("policyManagementMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.policyManagementMenuItem);

        this.userDirectoryManagementMenuItem = new LeftNavigationItem(getTranslation("menu-item.user-directories",
            UI.getCurrent().getLocale(), null), VaadinIcon.COG.create(), UserDirectoriesView.class);
        this.userDirectoryManagementMenuItem.setId("userDirectoryManagementMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.userDirectoryManagementMenuItem);

        this.businessStreamDesignerMenuItem = new LeftNavigationItem("Designer", VaadinIcon.PALETE.create(), BusinessStreamDesignerView.class);
        this.businessStreamDesignerMenuItem.setId("businessStreamDesignerMenuItem");
        leftSubMenuBuilder = leftSubMenuBuilder.add(this.businessStreamDesignerMenuItem);

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
        this.searchMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.SEARCH_WRITE,
            SecurityConstants.ALL_AUTHORITY));

        this.visualisationMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.WIRETAP_READ,
            SecurityConstants.ERROR_ADMIN, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE,
            SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_READ,
            SecurityConstants.REPLAY_ADMIN, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE,
            SecurityConstants.ALL_AUTHORITY));

        this.leftSubmenu.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_ADMINISTRATION_ADMIN, SecurityConstants.USER_ADMINISTRATION_WRITE,
            SecurityConstants.USER_ADMINISTRATION_READ, SecurityConstants.USER_DIRECTORY_ADMIN, SecurityConstants.USER_DIRECTORY_WRITE, SecurityConstants.USER_DIRECTORY_READ,
            SecurityConstants.GROUP_ADMINISTRATION_ADMIN, SecurityConstants.GROUP_ADMINISTRATION_WRITE, SecurityConstants.GROUP_ADMINISTRATION_READ,
            SecurityConstants.POLICY_ADMINISTRATION_ADMIN, SecurityConstants.POLICY_ADMINISTRATION_READ, SecurityConstants.POLICY_ADMINISTRATION_WRITE,
            SecurityConstants.ROLE_ADMINISTRATION_ADMIN, SecurityConstants.ROLE_ADMINISTRATION_READ, SecurityConstants.ROLE_ADMINISTRATION_WRITE,SecurityConstants.ALL_AUTHORITY,
            SecurityConstants.SYSTEM_EVENT_ADMIN, SecurityConstants.SYSTEM_EVENT_READ, SecurityConstants.SYSTEM_EVENT_READ));

        this.systemEventMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.SYSTEM_EVENT_ADMIN, SecurityConstants.SYSTEM_EVENT_READ,
            SecurityConstants.SYSTEM_EVENT_WRITE));

        this.userManagementMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_ADMINISTRATION_ADMIN, SecurityConstants.USER_ADMINISTRATION_WRITE,
            SecurityConstants.USER_ADMINISTRATION_READ));

        this.groupManagementMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.GROUP_ADMINISTRATION_ADMIN, SecurityConstants.GROUP_ADMINISTRATION_WRITE,
            SecurityConstants.GROUP_ADMINISTRATION_READ));

        this.roleManagementMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.ROLE_ADMINISTRATION_ADMIN, SecurityConstants.ROLE_ADMINISTRATION_READ,
            SecurityConstants.ROLE_ADMINISTRATION_WRITE));

        this.policyManagementMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.POLICY_ADMINISTRATION_ADMIN, SecurityConstants.POLICY_ADMINISTRATION_READ,
            SecurityConstants.POLICY_ADMINISTRATION_WRITE));

        this.userDirectoryManagementMenuItem.setVisible(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ALL_AUTHORITY, SecurityConstants.USER_DIRECTORY_ADMIN, SecurityConstants.USER_DIRECTORY_WRITE,
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
