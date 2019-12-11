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
        Notification notification = new Notification(errorMessage);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(errorNotificationDuration);
        notification.setOpened(true);
    }

    public static void showUserNotification(String message)
    {
        Notification notification = new Notification(message);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(errorNotificationDuration);
        notification.setOpened(true);
    }
}
