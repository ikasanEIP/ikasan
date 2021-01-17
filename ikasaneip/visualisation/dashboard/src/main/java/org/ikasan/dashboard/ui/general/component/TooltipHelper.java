package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.componentfactory.TooltipAlignment;
import com.vaadin.componentfactory.TooltipPosition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TooltipHelper
{
    public static Tooltip getTooltipForComponentTopLeft(Component component, String message)
    {
        return getTooltip(component, message, TooltipPosition.TOP, TooltipAlignment.RIGHT);
    }

    public static Tooltip getTooltipForComponentBottom(Component component, String message)
    {
        return getTooltip(component, message, TooltipPosition.BOTTOM, TooltipAlignment.BOTTOM);
    }

    public static Tooltip getTooltip(Component component, String message, TooltipPosition position, TooltipAlignment alignment)
    {
        Tooltip tooltip = new Tooltip();

        tooltip.getElement().getStyle().set("background-color", "#232F34");
        tooltip.getElement().getStyle().set("color", "#FFFFFF");
        tooltip.getElement().getStyle().set("border-radius", "10px");
        tooltip.getElement().getStyle().set("padding", "10px");

        tooltip.attachToComponent(component);

        tooltip.setPosition(position);
        tooltip.setAlignment(alignment);

        tooltip.add(new Paragraph(message));

        return tooltip;
    }

    public static Tooltip getDesignerTooltip(Image image, Component component, String message, TooltipPosition position, TooltipAlignment alignment)
    {
        Tooltip tooltip = new Tooltip();

        tooltip.getElement().getStyle().set("background-color", "#ffffff");
        tooltip.getElement().getStyle().set("color", "#000000");
        tooltip.getElement().getStyle().set("border-radius", "10px");
        tooltip.getElement().getStyle().set("padding", "10px");

        tooltip.attachToComponent(component);

        tooltip.setPosition(position);
        tooltip.setAlignment(alignment);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(image, new Paragraph(message));

        tooltip.add(verticalLayout);

        return tooltip;
    }
}
