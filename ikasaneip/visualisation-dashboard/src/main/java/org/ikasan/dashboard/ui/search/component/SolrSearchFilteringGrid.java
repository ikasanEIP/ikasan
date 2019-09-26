package org.ikasan.dashboard.ui.search.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SolrSearchFilteringGrid extends Grid<IkasanSolrDocument>
{
    private Logger logger = LoggerFactory.getLogger(SolrSearchFilteringGrid.class);

    private SolrSearchService<IkasanSolrDocumentSearchResults> solrSearchService;

    private DataProvider<IkasanSolrDocument,SearchFilter> dataProvider;
    private ConfigurableFilterDataProvider<IkasanSolrDocument,Void, SearchFilter> filteredDataProvider;

    private SearchFilter searchFilter;

    private long resultSize = 0;
    private long queryTime = 0;

    private Label resultsLabel;

    /**
     * Constructors
     */
    public SolrSearchFilteringGrid(SolrSearchService<IkasanSolrDocumentSearchResults> solrSearchService, SearchFilter searchFilter, Label resultsLabel)
    {
        this.solrSearchService = solrSearchService;
        if(this.solrSearchService ==  null)
        {
            throw new IllegalArgumentException("solrSearchService cannot be null!");
        }
        this.searchFilter = searchFilter;
        if(this.searchFilter ==  null)
        {
            throw new IllegalArgumentException("SearchFilter cannot be null!");
        }
        this.resultsLabel = resultsLabel;
        if(this.searchFilter ==  null)
        {
            throw new IllegalArgumentException("resultsLabel cannot be null!");
        }
    }

    /**
     * Add filtering to a column.
     *
     * @param hr
     * @param setFilter
     * @param columnKey
     */
    public void addGridFiltering(HeaderRow hr, Consumer<String> setFilter, String columnKey)
    {
        TextField textField = new TextField();
        textField.setWidthFull();

        textField.addValueChangeListener(ev->{

            setFilter.accept(ev.getValue());

            filteredDataProvider.refreshAll();
        });

        hr.getCell(getColumnByKey(columnKey)).setComponent(textField);
    }

    public void init(long startTime, long endTime, String searchTerm, List<String> types)
    {
        dataProvider = DataProvider.fromFilteringCallbacks(query ->
        {
            Optional<SearchFilter> filter = query.getFilter();

            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            IkasanSolrDocumentSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(filter.get(), startTime, endTime, searchTerm, offset, limit, types);
            }
            else
            {
                results = this.solrSearchService.search(searchTerm,
                    startTime, endTime, offset, limit, types);
            }

            return results.getResultList().stream();
        }, query ->
        {

            Optional<SearchFilter> filter = query.getFilter();

            IkasanSolrDocumentSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(filter.get(), startTime, endTime, searchTerm, 0, 0, types);
            }
            else
            {
                results = this.solrSearchService.search(searchTerm,
                    startTime, endTime, 0, 0, types);
            }

            this.resultSize = results.getTotalNumberOfResults();
            this.queryTime = results.getQueryResponseTime();

            this.resultsLabel.setText(String.format(getTranslation("label.search-results-returned", UI.getCurrent().getLocale(), null), this.resultSize, this.queryTime));
            this.resultsLabel.getElement().getStyle().set("fontSize", "10pt");

            return (int) results.getTotalNumberOfResults();
        });

        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(this.searchFilter);

        this.setDataProvider(filteredDataProvider);
    }

    private IkasanSolrDocumentSearchResults getResults(SearchFilter filter, long startTime, long endTime, String searchTerm, int offset, int limit, List<String> types)
    {
        HashSet<String> moduleNames = null;

        if(filter.getModuleNameFilter() != null && !filter.getModuleNameFilter().isEmpty())
        {
            moduleNames = new HashSet<>();
            moduleNames.add(filter.getModuleNameFilter() + "*");
        }

        HashSet<String> flowNames = null;

        if(filter.getFlowNameFilter() != null && !filter.getFlowNameFilter().isEmpty())
        {
            flowNames = new HashSet<>();
            flowNames.add(filter.getFlowNameFilter() + "*");
        }

        HashSet<String> componentNames = null;

        if(filter.getComponentNameFilter() != null && !filter.getComponentNameFilter().isEmpty())
        {
            componentNames = new HashSet<>();
            componentNames.add(filter.getComponentNameFilter() + "*");
        }

        String eventId = null;

        if(filter.getEventIdFilter() != null && !filter.getEventIdFilter().isEmpty())
        {
            eventId = "*" + filter.getEventIdFilter() + "*";
        }

        return this.solrSearchService.search(moduleNames, flowNames, componentNames, eventId, searchTerm,
            startTime, endTime, offset, limit, types);
    }

    public long getResultSize()
    {
        return resultSize;
    }
}
