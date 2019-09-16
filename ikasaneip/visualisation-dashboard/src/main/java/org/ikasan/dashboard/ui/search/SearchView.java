package org.ikasan.dashboard.ui.search;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Route(value = "search", layout = IkasanAppLayout.class)
@UIScope
@Component
public class SearchView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(SearchView.class);

    /**
     * Constructor
     */
    public SearchView()
    {
        this.setMargin(true);
        this.setSizeFull();


        this.createSearchForm();
    }

    protected void createSearchForm()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("50%");

        LocalDate now = LocalDate.now();

        Checkbox wiretapCheckbox = new Checkbox("Wiretap");
        Checkbox errorCheckbox = new Checkbox("Error");
        Checkbox exclusionCheckbox = new Checkbox("Exclusion");
        Checkbox replayCheckbox = new Checkbox("Replay");


        searchLayout.add(wiretapCheckbox, errorCheckbox, exclusionCheckbox, replayCheckbox);

        DatePicker startDate = new DatePicker(now.minus(1, ChronoUnit.DAYS));
        searchLayout.add(startDate);

        DatePicker endDate = new DatePicker(now.plus(1, ChronoUnit.DAYS));
        searchLayout.add(endDate);

        TextField searchText = new TextField();
        searchText.setWidth("600px");
        searchText.setHeight("30px");

        Button searchButton = new Button("Search");
        searchButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> search(searchText.getValue(),
            Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())));

        this.add(searchText, searchLayout, searchButton);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchText);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchButton);
    }

    protected void search(String searchTerm, Date startDate, Date endDate)
    {

    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
    }



}

