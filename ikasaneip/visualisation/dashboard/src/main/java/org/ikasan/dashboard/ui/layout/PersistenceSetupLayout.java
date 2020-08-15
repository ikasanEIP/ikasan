package org.ikasan.dashboard.ui.layout;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;


@Push(transport = Transport.LONG_POLLING)
@HtmlImport("frontend://styles/shared-styles.html")
@Theme(Lumo.class)
public class PersistenceSetupLayout extends VerticalLayout implements RouterLayout
{
    public PersistenceSetupLayout()
    {
        this.setSizeFull();
        Image ikasan = new Image("frontend/images/mr_squid_titling_dashboard.png", "");
        ikasan.setHeight("80px");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button returnToLoginScreenButton = new Button(VaadinIcon.SIGN_IN.create());
        returnToLoginScreenButton.getStyle().set("margin-right", "auto");
        returnToLoginScreenButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
            iconButtonClickEvent -> UI.getCurrent().navigate(""));

        horizontalLayout.add(ikasan, returnToLoginScreenButton);
        horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, returnToLoginScreenButton);

        add(horizontalLayout);

        Div div = new Div();
        div.setWidth("100%");
        div.setHeight("3px");
        div.add(new Html("<hr/>"));

        add(div);
    }
}
