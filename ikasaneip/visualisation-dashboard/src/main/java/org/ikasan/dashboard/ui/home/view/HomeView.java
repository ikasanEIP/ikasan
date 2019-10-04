package org.ikasan.dashboard.ui.home.view;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@Route(value = "", layout = IkasanAppLayout.class)
@UIScope
@Component
public class HomeView extends HorizontalLayout
{
    public HomeView()
    {
    }
}

