package org.ikasan.dashboard.ui.administration.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import org.ikasan.dashboard.ui.administration.component.SystemEventDialog;
import org.ikasan.dashboard.ui.administration.component.SystemEventSearchForm;
import org.ikasan.dashboard.ui.administration.util.SystemEventFormatter;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SystemEventSearchView extends VerticalLayout implements SearchListener
{
    private Logger logger = LoggerFactory.getLogger(SystemEventSearchView.class);

    private SolrGeneralServiceImpl solrSearchService;

    private SolrSearchFilteringGrid searchResultsGrid;
    private SearchFilter searchFilter = new SearchFilter();

    private SystemEventSearchForm searchForm;

    private Label resultsLabel = new Label();

    /**
     * Constructor
     */
    public SystemEventSearchView(SolrGeneralServiceImpl solrSearchService)
    {
        super();
        this.solrSearchService = solrSearchService;
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(false);
        this.setPadding(false);

        resultsLabel.setVisible(false);

        this.createSearchForm();

        this.searchResultsGrid = new SolrSearchFilteringGrid(this.solrSearchService, this.searchFilter, this.resultsLabel);

        ObjectMapper objectMapper = new ObjectMapper();

        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.START);

            try {
                horizontalLayout.add(objectMapper.readValue(ikasanSolrDocument.getEvent(), SystemEventImpl.class).getActor());
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return horizontalLayout;
        })).setFlexGrow(2).setHeader("Action performed by").setKey("actor").setResizable(true);
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.START);

            try {
                horizontalLayout.add(SystemEventFormatter.getContext(objectMapper
                    .readValue(ikasanSolrDocument.getEvent(), SystemEventImpl.class)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return horizontalLayout;
        })).setFlexGrow(4).setKey("context").setHeader("Context").setResizable(true);
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.START);

            try {
                horizontalLayout.add(SystemEventFormatter.getEvent(objectMapper
                    .readValue(ikasanSolrDocument.getEvent(), SystemEventImpl.class)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            return horizontalLayout;
        })).setFlexGrow(12).setHeader("Action").setKey("action").setResizable(true);
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp())))
            .setHeader(getTranslation("table-header.timestamp", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setKey("timestamp")
            .setFlexGrow(2)
            .setResizable(true);

        HeaderRow hr = searchResultsGrid.appendHeaderRow();
        this.searchResultsGrid.addGridFiltering(hr, value -> searchFilter.setSystemEventFilter("actor", value), "actor");
        this.searchResultsGrid.addGridFiltering(hr, value -> searchFilter.setSystemEventFilter("context", value), "context");
        this.searchResultsGrid.addGridFiltering(hr, value -> searchFilter.setSystemEventFilter("action", value), "action");
        this.searchResultsGrid.setVisible(true);

        this.searchResultsGrid.setWidthFull();
        this.searchResultsGrid.setHeight("70vh");

        this.searchResultsGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<IkasanSolrDocument>>)
            ikasanSolrDocumentItemDoubleClickEvent ->
            {
                SystemEventDialog systemEventDialog = new SystemEventDialog();
                systemEventDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
            });

        HorizontalLayout resultsLayout = new HorizontalLayout();
        resultsLayout.setWidthFull();
        resultsLayout.add(resultsLabel);

        add(searchForm, resultsLayout, this.searchResultsGrid);
    }

    /**
     * Create the search form that appears at the top of the screen.
     */
    protected void createSearchForm()
    {
        this.searchForm = new SystemEventSearchForm();
        this.searchForm.addSearchListener(this);
    }

    @Override
    public void search(String searchTerm, List<String> entityTypes, boolean negateQuery, long startDate, long endDate) {
        this.searchResultsGrid.init(startDate, endDate, searchTerm, List.of("systemEvent"), false, this.searchFilter);
        this.resultsLabel.setVisible(true);
    }


}
