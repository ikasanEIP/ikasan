package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

public class TableButton extends Button
{
    public TableButton()
    {
        this.init();
    }

    public TableButton(String text)
    {
        super(text);
        this.init();
    }

    public TableButton(Component icon)
    {
        super(icon);
        this.init();
    }

    public TableButton(String text, Component icon)
    {
        super(text, icon);
        this.init();
    }

    public TableButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener)
    {
        super(text, clickListener);
        this.init();
    }

    public TableButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener)
    {
        super(icon, clickListener);
        this.init();
    }

    public TableButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener)
    {
        super(text, icon, clickListener);
        this.init();
    }

    protected void init()
    {
        this.getStyle().set("width", "30px");
        this.getStyle().set("height", "30px");
        this.getStyle().set("font-size", "12pt");
    }
}
