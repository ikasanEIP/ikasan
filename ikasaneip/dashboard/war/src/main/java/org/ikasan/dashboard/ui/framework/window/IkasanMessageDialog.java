package org.ikasan.dashboard.ui.framework.window;

import org.ikasan.dashboard.ui.framework.action.Action;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * 
 * @author CMI2 Development Team
 *
 */
public class IkasanMessageDialog extends Window
{
    private static final long serialVersionUID = 300587169393808040L;
    private Action action;

    /**
     * Constructor
     * 
     * @param dialogName
     * @param message
     * @param action
     */
    public IkasanMessageDialog(String dialogName, String message, Action action)
    {
        super(dialogName);
        this.action = action;
        init(message);
    }

    /**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init(String message)
    {
        super.setModal(true);
        super.setHeight(20.0f, Unit.PERCENTAGE);
        super.setWidth(30.0f, Unit.PERCENTAGE);
        super.center();
        super.setStyleName("ikasan");
        
        FormLayout layout = new FormLayout();
        layout.setMargin(true);
        layout.addComponent(new Label(message));
        
        Button okButton = new Button("OK");
        okButton.setStyleName(Reindeer.BUTTON_SMALL);

        okButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                action.exectuteAction();
                close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                action.ignoreAction();
                close();
            }
        });

        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidth(100, Unit.PERCENTAGE);
        HorizontalLayout hlayout = new HorizontalLayout();
        wrapper.addComponent(hlayout);
        wrapper.setComponentAlignment(hlayout, Alignment.MIDDLE_CENTER);
        hlayout.addComponent(okButton);
        hlayout.addComponent(cancelButton);

        layout.addComponent(wrapper);
        
        super.setContent(layout);
    }
}
