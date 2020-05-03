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
import org.ikasan.dashboard.ui.search.view.SearchView;
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
    private Tooltip allButtonTooltip;
    private Image wiretapImage;
    private Tooltip wiretapButtonTooltip;
    private Image hospitalImage;
    private Tooltip hospitalButtonTooltip;
    private Image errorImage;
    private Tooltip errorButtonTooltip;
    private Image replayImage;
    private Tooltip replaySearchButtonTooltip;

    private Checkbox wiretapCheckbox;
    private Checkbox errorCheckbox;
    private Checkbox hospitalCheckbox;
    private Checkbox replayCheckbox;

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
        searchLayout.setSpacing(true);

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
        this.searchButton.setHeight("50px");
        this.searchButton.setWidth("50px");

        addButtonSearchListener(this.searchButton);

        this.allButtonTooltip = TooltipHelper.getTooltipForComponentBottom(searchButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        searchTextLayout.add(this.searchButton, this.allButtonTooltip);
        searchTextLayout.setVerticalComponentAlignment(Alignment.CENTER, searchButton);


        searchLayout.add(dateTimePickersLayout, searchTextLayout);

        this.wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        this.wiretapImage.setHeight("40px");
        this.wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.wiretapImage, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));


        this.wiretapCheckbox = new Checkbox();
        this.wiretapCheckbox.setValue(true);
        VerticalLayout wiretapFilterLayout = createSearchFilterLayout(this.wiretapImage, this.wiretapCheckbox);
        wiretapFilterLayout.add(this.wiretapButtonTooltip);

        this.errorImage = new Image("frontend/images/error-service.png", "");
        this.errorImage.setHeight("40px");
        this.errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.errorImage, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));

        this.errorCheckbox = new Checkbox();
        this.errorCheckbox.setValue(true);

        VerticalLayout errorFilterLayout = createSearchFilterLayout(this.errorImage, this.errorCheckbox);
        errorFilterLayout.add(this.errorButtonTooltip);

        this.hospitalImage = new Image("frontend/images/hospital-service.png", "");
        this.hospitalImage.setHeight("40px");
        this.hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.hospitalImage, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));

        this.hospitalCheckbox = new Checkbox();
        this.hospitalCheckbox.setValue(true);
        VerticalLayout hospitalFilterLayout = createSearchFilterLayout(this.hospitalImage, this.hospitalCheckbox);
        hospitalFilterLayout.add(this.hospitalButtonTooltip);

        this.replayImage = new Image("frontend/images/replay-service.png", "");
        this.replayImage.setHeight("40px");
        this.replaySearchButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.replayImage, getTranslation("tooltip.search-replay-events", UI.getCurrent().getLocale()));

        this.replayCheckbox = new Checkbox();
        this.replayCheckbox.setValue(true);
        VerticalLayout replayFilterLayout = createSearchFilterLayout(replayImage, replayCheckbox);
        replayFilterLayout.add(replaySearchButtonTooltip);

        this.negateQueryCheckbox = new Checkbox(getTranslation("checkbox-label.negate-search", UI.getCurrent().getLocale()));
        this.negateQueryCheckbox.setWidth("100px");
        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setWidth("100px");
        checkboxLayout.add(this.negateQueryCheckbox);
        checkboxLayout.setVerticalComponentAlignment(Alignment.CENTER, this.negateQueryCheckbox);

        HorizontalLayout searchIconLayout = new HorizontalLayout();
        searchIconLayout.setSpacing(true);
        searchIconLayout.add(wiretapFilterLayout, replayFilterLayout, hospitalFilterLayout, errorFilterLayout, checkboxLayout);
        searchIconLayout.setVerticalComponentAlignment(Alignment.CENTER, checkboxLayout);

        ComponentSecurityVisibility.applySecurity(replayFilterLayout, SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(errorFilterLayout, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(hospitalFilterLayout, SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(replayFilterLayout, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

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

            if(this.errorCheckbox.getValue()) {
                entityTypes.add("error");
            }
            if(this.wiretapCheckbox.getValue()) {
                entityTypes.add("wiretap");
            }
            if(this.hospitalCheckbox.getValue()) {
                entityTypes.add("exclusion");
            }
            if(this.replayCheckbox.getValue()) {
                entityTypes.add("replay");
            }

            this.searchListeners.forEach(searchListener -> {
                searchListener.search(searchTerm.getTerm(), entityTypes, this.negateQueryCheckbox.getValue(),
                    Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.startTimePicker.getValue()),
                    Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.endTimePicker.getValue()));
            });
        });
    }

    /**
     * Create the filter layout
     *
     * @param filterImage
     * @param checkbox
     * @return
     */
    private VerticalLayout createSearchFilterLayout(Image filterImage, Checkbox checkbox)
    {
        VerticalLayout buttonLayout = new VerticalLayout();
        filterImage.setHeight("40px");
        filterImage.setWidth("40px");

        checkbox.setHeight("10px");
        buttonLayout.add(filterImage, checkbox);
        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, checkbox);
        buttonLayout.setFlexGrow(1.0, checkbox);

        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, filterImage);
        buttonLayout.setWidth("50px");

        buttonLayout.setFlexGrow(2.0, filterImage);

        return buttonLayout;
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
        this.allButtonTooltip.attachToComponent(this.searchButton);
        this.wiretapButtonTooltip.attachToComponent(this.wiretapImage);
        this.errorButtonTooltip.attachToComponent(this.errorImage);
        this.hospitalButtonTooltip.attachToComponent(this.hospitalImage);
        this.replaySearchButtonTooltip.attachToComponent(this.replayImage);
    }

    public void addSearchListener(SearchListener searchListener) {
        this.searchListeners.add(searchListener);
    }
}
