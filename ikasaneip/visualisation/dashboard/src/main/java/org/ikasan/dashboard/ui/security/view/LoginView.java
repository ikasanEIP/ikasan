package org.ikasan.dashboard.ui.security.view;


import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.security.ContextCache;
import org.ikasan.dashboard.security.CustomRequestCache;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Ikasan - Login")
@HtmlImport("frontend://styles/shared-styles.html")
@HtmlImport("frontend://bower_components/vaadin-lumo-styles/presets/compact.html")
@Component
@UIScope
public class LoginView extends VerticalLayout implements PageConfigurator, HasUrlParameter<String>
{
    public static final String ROUTE = "login";

    @Resource
    private AuthenticationService authenticationService;


    private LoginForm login = new LoginForm();
    private CustomRequestCache customRequestCache;
    private String route;

    public LoginView()
    {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        login.setForgotPasswordButtonVisible(false);

        Image ikasan = new Image("frontend/images/mr_squid_titling_dashboard.png", "");
        ikasan.setHeight("180px");

        Div loginDiv = new Div();
        loginDiv.add(login);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, ikasan, loginDiv);

        layout.add(ikasan, loginDiv);

        login.addLoginListener((ComponentEventListener<AbstractLogin.LoginEvent>) loginEvent ->
        {

            try
            {
                Authentication authentication = this.authenticationService.login(loginEvent.getUsername(),
                    loginEvent.getPassword());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            catch (AuthenticationServiceException e)
            {
                login.setError(true);
            }

            String context = ContextCache.getContext(UI.getCurrent().getSession().getSession().getId());
            if(context != null && isRouteValid(context)) {
                UI.getCurrent().navigate(context);
            }
            else {
                UI.getCurrent().navigate("");
            }
        });

        this.add(layout);
    }

    private boolean isRouteValid(String context) {
        if(context.isEmpty()) {
            return true;
        }

        List<RouteData> routes = RouteConfiguration.forApplicationScope().getAvailableRoutes();

        return routes.stream().anyMatch(route -> {
            String url = route.getUrl();

            if(!url.isEmpty() && context.startsWith(url + "/")){
                return true;
            }

            return false;
        });
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("rel", "shortcut icon");
        attributes.put("type", "image/png");
        settings.addLink("icons/icon.png", attributes);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        this.route = parameter;
    }
}