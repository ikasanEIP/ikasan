package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.util.DateTimeUtil;
import org.ikasan.dashboard.ui.util.SecurityConstants;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemEventSearchForm extends VerticalLayout {
    private Button searchButton;
    private Tooltip searchButtonTooltip;

    private TextArea searchText = new TextArea();
    private DatePicker startDate;
    private DatePicker endDate;
    private TimePicker startTimePicker = new TimePicker();
    private TimePicker endTimePicker = new TimePicker();

    private List<SearchListener> searchListeners = new ArrayList<>();

    public SystemEventSearchForm() {
        this.createSearchForm();
    }

    /**
     * Create the search form that appears at the top of the screen.
     */
    protected void createSearchForm()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("1000px");
        searchLayout.setSpacing(false);
        searchLayout.setPadding(false);

        LocalDate nowDate = LocalDate.now();

        HorizontalLayout startDateTimeLayout = new HorizontalLayout();
        this.startDate = new DatePicker(nowDate);
        this.startDate.setLocale(UI.getCurrent().getLocale());
        this.startDate.setWidth("150px");

        this.startTimePicker.setStep(Duration.ofMinutes(15l));
        this.startTimePicker.setLocale(UI.getCurrent().getLocale());
        this.startTimePicker.setValue(LocalTime.of(0, 0));
        this.startTimePicker.setWidth("150px");

        startDateTimeLayout.add(startDate, startTimePicker);

        HorizontalLayout endDateTimeLayout = new HorizontalLayout();
        this.endDate = new DatePicker(nowDate.plus(1, ChronoUnit.DAYS));
        this.endDate.setLocale(UI.getCurrent().getLocale());
        this.endDate.setWidth("150px");


        this.endTimePicker.setStep(Duration.ofMinutes(15l));
        this.endTimePicker.setLocale(UI.getCurrent().getLocale());
        this.endTimePicker.setValue(LocalTime.of(0, 0));
        this.endTimePicker.setWidth("150px");

        endDateTimeLayout.add(endDate, endTimePicker);

        VerticalLayout dateTimePickersLayout = new VerticalLayout();
        dateTimePickersLayout.add(startDateTimeLayout, endDateTimeLayout);
        dateTimePickersLayout.setWidth("350px");

        this.searchText.setWidth("600px");
        this.searchText.setHeight("80px");
        this.searchText.setPlaceholder("search term");
        this.searchText.setRequired(true);

        HorizontalLayout searchTextLayout = new HorizontalLayout();
        searchTextLayout.setMargin(true);
        searchTextLayout.add(searchText);

        Image searchButtonImage = new Image("frontend/images/search-icon.png", "");
        searchButtonImage.setHeight("50px");
        this.searchButton = new Button(searchButtonImage);
        this.searchButton.setHeight("54px");
        this.searchButton.setWidth("54px");
        this.searchButton.setId("searchFormSearchButton");

        addButtonSearchListener(this.searchButton);

        this.searchButtonTooltip = TooltipHelper.getTooltipForComponentBottom(searchButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        searchTextLayout.add(this.searchButton, this.searchButtonTooltip);
        searchTextLayout.setVerticalComponentAlignment(Alignment.CENTER, searchButton);


        searchLayout.add(dateTimePickersLayout, searchTextLayout);

        this.getElement().getThemeList().remove("padding");
        this.add(searchLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchLayout);
    }

    /**
     * Add the search listener to a button.
     *
     * @param button
     */
    private void addButtonSearchListener(Button button)
    {
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            Binder<SearchTerm> searchTextBinder = new Binder<>(SearchTerm.class);
            SearchTerm searchTerm = new SearchTerm();

            searchTextBinder.forField(this.searchText)
                .bind(SearchTerm::getTerm, SearchTerm::setTerm);

            try
            {
                searchTextBinder.writeBean(searchTerm);
            }
            catch (ValidationException e)
            {
                return;
            }

            this.searchListeners.forEach(searchListener -> {
                searchListener.search(searchTerm.getTerm(), List.of("systemEvent"), false,
                    Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.startTimePicker.getValue()),
                    Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.endTimePicker.getValue()));
            });
        });
    }

    private class SearchTerm
    {
        private String term;

        public String getTerm()
        {
            return term;
        }

        public void setTerm(String term)
        {
            this.term = term;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.searchButtonTooltip.attachToComponent(this.searchButton);
    }

    public void addSearchListener(SearchListener searchListener) {
        this.searchListeners.add(searchListener);
    }
}
