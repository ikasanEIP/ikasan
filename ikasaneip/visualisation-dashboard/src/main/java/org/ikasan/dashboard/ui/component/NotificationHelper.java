package org.ikasan.dashboard.ui.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationHelper
{
    private static Integer errorNotificationDuration = 1000;

    @Value("${error.notification.duration}")
    public void setErrorNotificationDuration(Integer duration)
    {
        NotificationHelper.errorNotificationDuration = duration;
    }

    public static void showErrorNotification(String errorMessage)
    {
        Icon errorIcon = new Icon(VaadinIcon.EXCLAMATION);
        errorIcon.setColor("white");

        Div content = new Div();
        content.setWidth("500px");
        content.getStyle().set("color", "red");
        content.setText(errorMessage);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setSizeFull();
        layout.add(content);

        Notification notification = new Notification(layout);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(errorNotificationDuration);
        notification.setOpened(true);
    }

    public static void showUserNotification(String message)
    {
        Icon errorIcon = new Icon(VaadinIcon.EXCLAMATION);
        errorIcon.setColor("white");

        Span content = new Span();
        content.setText(message);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setSizeFull();
        layout.add(content);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, content);

        Notification notification = new Notification(layout);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(errorNotificationDuration);
        notification.setOpened(true);
    }
}
