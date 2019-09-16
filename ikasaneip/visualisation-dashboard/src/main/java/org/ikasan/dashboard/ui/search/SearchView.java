package org.ikasan.dashboard.ui.search;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.User;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Route(value = "search", layout = IkasanAppLayout.class)
@UIScope
@Component
public class SearchView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(SearchView.class);

    private Grid<IkasanSolrDocument> searchResulsGrid;

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
        searchLayout.setWidth("100%");

        LocalDate nowDate = LocalDate.now();

        Checkbox wiretapCheckbox = new Checkbox("Wiretap");
        Checkbox errorCheckbox = new Checkbox("Error");
        Checkbox exclusionCheckbox = new Checkbox("Exclusion");
        Checkbox replayCheckbox = new Checkbox("Replay");


        searchLayout.add(wiretapCheckbox, errorCheckbox, exclusionCheckbox, replayCheckbox);

        DatePicker startDate = new DatePicker(nowDate);
        startDate.setLabel("Start Date");
        searchLayout.add(startDate);

        TimePicker startTimePicker = new TimePicker();
        startTimePicker.setLabel("Start Time");
        startTimePicker.setStep(Duration.ofMinutes(15l));
        startTimePicker.setValue(LocalTime.of(0, 0));

        searchLayout.add(startTimePicker);

        DatePicker endDate = new DatePicker(nowDate.plus(1, ChronoUnit.DAYS));
        endDate.setLabel("End Date");
        searchLayout.add(endDate);

        TimePicker endTimePicker = new TimePicker();
        endTimePicker.setLabel("End Time");
        endTimePicker.setStep(Duration.ofMinutes(15l));
        endTimePicker.setValue(LocalTime.of(0, 0));

        searchLayout.add(endTimePicker);

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

    protected void createSearchResultGrid()
    {
        this.searchResulsGrid = new Grid<>();

        this.searchResulsGrid.addColumn(IkasanSolrDocument::getModuleName).setKey("modulename").setHeader("Module Name").setSortable(true);
        this.searchResulsGrid.addColumn(IkasanSolrDocument::getFlowName).setKey("flowname").setHeader("Flow Name").setSortable(true);
        this.searchResulsGrid.addColumn(IkasanSolrDocument::getComponentName).setKey("conponentname").setHeader("Component Name").setSortable(true);
        this.searchResulsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.eventIdentifier]]</div>")
            .withProperty("eventIdentifier",
                ikasanSolrDocument -> {
                    return Optional.ofNullable(ikasanSolrDocument.getErrorUri()).orElse(ikasanSolrDocument.getEventId());
                })).setKey("email").setHeader("Event Id / Error URI").setSortable(true);
        this.searchResulsGrid.addColumn(IkasanSolrDocument::getEvent).setKey("event").setHeader("Details").setSortable(true);
//        this.searchResulsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
//            "<div>[[item.date]]</div>")
//            .withProperty("date",
//                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp()))).setHeader("Last Access").setSortable(true);
    }

    protected void search(String searchTerm, Date startDate, Date endDate)
    {

    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
    }



}

