package org.ikasan.dashboard.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CustomLeftHeaderItem extends Composite<VerticalLayout>
{
    public CustomLeftHeaderItem(String title, String subtitle, String src)
    {
        VerticalLayout content = (VerticalLayout)this.getContent();
        content.setPadding(false);
        content.getStyle().set("padding", "var(--app-layout-menu-header-padding)");
        content.setMargin(false);
        this.setId("menu-header-wrapper");
        if (src != null)
        {
            Image image = new Image(src, "");
            image.setHeight("56px");
            image.setWidth("56px");
            content.add(new Component[]{image});
        }

        Label subtitleLabel;
        if (title != null)
        {
            subtitleLabel = new Label(title);
            subtitleLabel.setId("menu-header-title");
            content.add(new Component[]{subtitleLabel});
        }

        if (subtitle != null)
        {
            subtitleLabel = new Label(subtitle);
            subtitleLabel.setId("menu-header-subtitle");
            content.add(new Component[]{subtitleLabel});
        }

    }
}
