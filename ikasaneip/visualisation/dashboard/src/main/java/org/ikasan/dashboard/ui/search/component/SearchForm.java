package org.ikasan.dashboard.ui.search.component;

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

public class SearchForm extends VerticalLayout {
    private Button searchButton;
    private Tooltip searchButtonTooltip;
    private Image wiretapImage;
    private Tooltip wiretapButtonTooltip;
    private Image hospitalImage;
    private Tooltip hospitalButtonTooltip;
    private Image errorImage;
    private Tooltip errorButtonTooltip;
    private Image replayImage;
    private Tooltip replaySearchButtonTooltip;

    private Button wiretapCheckButton;
    private Button errorCheckButton;
    private Button hospitalCheckButton;
    private Button replayCheckButton;

    private boolean wiretapChecked = true;
    private boolean errorChecked = true;
    private boolean hospitalChecked = true;
    private boolean replayChecked = true;

    private TextArea searchText = new TextArea();
    private DatePicker startDate;
    private DatePicker endDate;
    private TimePicker startTimePicker = new TimePicker();
    private TimePicker endTimePicker = new TimePicker();
    private Checkbox negateQueryCheckbox;

    private List<SearchListener> searchListeners = new ArrayList<>();

    public SearchForm() {
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

        addButtonSearchListener(this.searchButton);

        this.searchButtonTooltip = TooltipHelper.getTooltipForComponentBottom(searchButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        searchTextLayout.add(this.searchButton, this.searchButtonTooltip);
        searchTextLayout.setVerticalComponentAlignment(Alignment.CENTER, searchButton);


        searchLayout.add(dateTimePickersLayout, searchTextLayout);

        this.wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        this.wiretapImage.setHeight("40px");
        this.wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.wiretapImage, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));


        this.wiretapImage = new Image("frontend/images/wiretap-inverse.png", "");
        this.wiretapImage.setHeight("40px");
        this.wiretapCheckButton = new Button(this.wiretapImage);
        this.wiretapCheckButton.setHeight("46px");
        this.wiretapCheckButton.setWidth("44px");

        this.wiretapCheckButton.addClickListener(buttonClickEvent -> {
            this.wiretapChecked = !this.wiretapChecked;

            if(wiretapChecked) {
                this.wiretapImage = new Image("frontend/images/wiretap-inverse.png", "");
                this.wiretapImage.setHeight("40px");
                this.wiretapCheckButton.setIcon(this.wiretapImage);
            }
            else {
                this.wiretapImage = new Image("frontend/images/wiretap-service.png", "");
                this.wiretapImage.setHeight("40px");
                this.wiretapCheckButton.setIcon(this.wiretapImage);
            }
        });

        this.wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.wiretapCheckButton, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));

        this.errorImage = new Image("frontend/images/error-inverse.png", "");
        this.errorImage.setHeight("40px");
        this.errorCheckButton = new Button(errorImage);
        this.errorCheckButton.setHeight("46px");
        this.errorCheckButton.setWidth("44px");

        this.errorCheckButton.addClickListener(buttonClickEvent -> {
            this.errorChecked = !this.errorChecked;

            if(this.errorChecked) {
                this.errorImage = new Image("frontend/images/error-inverse.png", "");
                this.errorImage.setHeight("40px");
                this.errorCheckButton.setIcon(this.errorImage);
            }
            else {
                this.errorImage = new Image("frontend/images/error-service.png", "");
                this.errorImage.setHeight("40px");
                this.errorCheckButton.setIcon(this.errorImage);
            }
        });

        this.errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.errorCheckButton, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));

        this.hospitalImage = new Image("frontend/images/hospital-inverse.png", "");
        this.hospitalImage.setHeight("40px");
        this.hospitalCheckButton = new Button(hospitalImage);
        this.hospitalCheckButton.setHeight("46px");
        this.hospitalCheckButton.setWidth("44px");

        this.hospitalCheckButton.addClickListener(buttonClickEvent -> {
            this.hospitalChecked = !this.hospitalChecked;

            if(this.hospitalChecked) {
                this.hospitalImage = new Image("frontend/images/hospital-inverse.png", "");
                this.hospitalImage.setHeight("40px");
                this.hospitalCheckButton.setIcon(this.hospitalImage);
            }
            else {
                this.hospitalImage = new Image("frontend/images/hospital-service.png", "");
                this.hospitalImage.setHeight("40px");
                this.hospitalCheckButton.setIcon(this.hospitalImage);
            }
        });

        this.hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.hospitalCheckButton, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));

        this.replayImage = new Image("frontend/images/replay-inverse.png", "");
        this.replayImage.setHeight("40px");
        this.replayCheckButton = new Button(replayImage);
        this.replayCheckButton.setHeight("46px");
        this.replayCheckButton.setWidth("44px");

        this.replayCheckButton.addClickListener(buttonClickEvent -> {
            this.replayChecked = !this.replayChecked;

            if(this.replayChecked) {
                this.replayImage = new Image("frontend/images/replay-inverse.png", "");
                this.replayImage.setHeight("40px");
                this.replayCheckButton.setIcon(this.replayImage);
            }
            else {
                this.replayImage = new Image("frontend/images/replay-service.png", "");
                this.replayImage.setHeight("40px");
                this.replayCheckButton.setIcon(this.replayImage);
            }
        });

        this.replaySearchButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.replayCheckButton, getTranslation("tooltip.search-replay-events", UI.getCurrent().getLocale()));

        this.negateQueryCheckbox = new Checkbox(getTranslation("checkbox-label.negate-search", UI.getCurrent().getLocale()));
        this.negateQueryCheckbox.setWidth("100px");
        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setWidth("100px");
        checkboxLayout.add(this.negateQueryCheckbox);
        checkboxLayout.setVerticalComponentAlignment(Alignment.CENTER, this.negateQueryCheckbox);


        HorizontalLayout searchIconLayout = new HorizontalLayout();
        searchIconLayout.setSpacing(true);
        searchIconLayout.add(this.wiretapCheckButton, this.wiretapButtonTooltip, this.replayCheckButton
            , this.replaySearchButtonTooltip, this.hospitalCheckButton, this.hospitalButtonTooltip
            , this.errorCheckButton, this.errorButtonTooltip, checkboxLayout);
        searchIconLayout.setVerticalComponentAlignment(Alignment.CENTER, checkboxLayout);

        ComponentSecurityVisibility.applySecurity(this.wiretapCheckButton, SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(this.errorCheckButton, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(this.hospitalCheckButton, SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(this.replayCheckButton, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

        this.add(searchLayout, searchIconLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchIconLayout);
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

            List<String> entityTypes = new ArrayList<>();

            if(this.errorChecked) {
                entityTypes.add("error");
            }
            if(this.wiretapChecked) {
                entityTypes.add("wiretap");
            }
            if(this.hospitalChecked) {
                entityTypes.add("exclusion");
            }
            if(this.replayChecked) {
                entityTypes.add("replay");
            }

            this.searchListeners.forEach(searchListener -> {
                searchListener.search(searchTerm.getTerm(), entityTypes, this.negateQueryCheckbox.getValue(),
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
        this.wiretapButtonTooltip.attachToComponent(this.wiretapCheckButton);
        this.errorButtonTooltip.attachToComponent(this.errorCheckButton);
        this.hospitalButtonTooltip.attachToComponent(this.hospitalCheckButton);
        this.replaySearchButtonTooltip.attachToComponent(this.replayCheckButton);
    }

    public void addSearchListener(SearchListener searchListener) {
        this.searchListeners.add(searchListener);
    }
}
