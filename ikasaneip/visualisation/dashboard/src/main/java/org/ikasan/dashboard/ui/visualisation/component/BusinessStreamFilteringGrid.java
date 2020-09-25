package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.visualisation.component.filter.BusinessStreamSearchFilter;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;
import org.ikasan.spec.metadata.BusinessStreamMetadataSearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BusinessStreamFilteringGrid extends Grid<BusinessStreamMetaData>
{
    private Logger logger = LoggerFactory.getLogger(BusinessStreamFilteringGrid.class);

    private BusinessStreamMetaDataService<BusinessStreamMetaData> solrSearchService;

    private DataProvider<BusinessStreamMetaData,BusinessStreamSearchFilter> dataProvider;
    private ConfigurableFilterDataProvider<BusinessStreamMetaData,Void, BusinessStreamSearchFilter> filteredDataProvider;

    private BusinessStreamSearchFilter searchFilter;

    private long resultSize = 0;

    /**
     * Constructors
     */
    public BusinessStreamFilteringGrid(BusinessStreamMetaDataService<BusinessStreamMetaData> solrSearchService,
                                       BusinessStreamSearchFilter searchFilter)
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

    public void init()
    {
        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        dataProvider = DataProvider.fromFilteringCallbacks(query ->
        {
            Optional<BusinessStreamSearchFilter> filter = query.getFilter();

            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            BusinessStreamMetadataSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(filter.get(), offset, limit);
            }
            else
            {
                results = this.getResults(null, offset, limit);
            }

            return results.getResultList().stream();
        }, query ->
        {
            Optional<BusinessStreamSearchFilter> filter = query.getFilter();

            BusinessStreamMetadataSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(filter.get(), 0, 0);
            }
            else
            {
                results = this.getResults(null, 0, 0);
            }

            this.resultSize = results.getTotalNumberOfResults();


            return (int) results.getTotalNumberOfResults();
        });

        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(this.searchFilter);

        this.setDataProvider(filteredDataProvider);
    }

    private BusinessStreamMetadataSearchResults getResults(BusinessStreamSearchFilter filter, int offset, int limit)
    {
        List<String> businessStreamNames = null;

        if(filter.getBusinessStreamNameFilter() != null && !filter.getBusinessStreamNameFilter().isEmpty())
        {
            businessStreamNames = new ArrayList<>();
            businessStreamNames.add("*" + ClientUtils.escapeQueryChars(filter.getBusinessStreamNameFilter()) + "*");
        }

        try {
            return this.solrSearchService.find(businessStreamNames, offset, limit);
        }
        catch (Exception e) {
            final UI current = UI.getCurrent();
            final I18NProvider i18NProvider = VaadinService.getCurrent().getInstantiator().getI18NProvider();
            NotificationHelper.showErrorNotification(i18NProvider.getTranslation("error.solr-unavailable"
                , current.getLocale()));
        }

        return new BusinessStreamMetadataSearchResults(new ArrayList<>(), 0, 0);
    }

    public long getResultSize()
    {
        return resultSize;
    }
}
