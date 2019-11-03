package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.html.Span;

public class Divider extends Span
{

    public Divider() {
        getStyle().set("background-color", "rgba(241, 90, 35, 0.5)");
        getStyle().set("flex", "0 0 2px");
        getStyle().set("align-self", "stretch");
    }
}
