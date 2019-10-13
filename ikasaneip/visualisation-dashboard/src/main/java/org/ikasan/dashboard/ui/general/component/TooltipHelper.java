package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.componentfactory.TooltipAlignment;
import com.vaadin.componentfactory.TooltipPosition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;

public class TooltipHelper
{
    public static Tooltip getTooltipForComponentTopLeft(Component component, String message)
    {
        Tooltip tooltip = new Tooltip();

        tooltip.attachToComponent(component);

        tooltip.setPosition(TooltipPosition.TOP);
        tooltip.setAlignment(TooltipAlignment.LEFT);

        tooltip.add(new Paragraph(message));

        return tooltip;
    }

    public static Tooltip getTooltipForComponentTopRight(Component component, String message)
    {
        Tooltip tooltip = new Tooltip();

        tooltip.attachToComponent(component);

        tooltip.setPosition(TooltipPosition.BOTTOM);
        tooltip.setAlignment(TooltipAlignment.BOTTOM);

        tooltip.add(new Paragraph(message));

        return tooltip;
    }
}
