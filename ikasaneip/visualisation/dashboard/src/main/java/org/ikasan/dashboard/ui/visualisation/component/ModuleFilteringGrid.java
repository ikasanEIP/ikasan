package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.ikasan.dashboard.security.SecurityUtils;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.filter.ModuleSearchFilter;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.metadata.ModuleMetadataSearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModuleFilteringGrid extends Grid<ModuleMetaData>
{
    private Logger logger = LoggerFactory.getLogger(ModuleFilteringGrid.class);

    private ModuleMetaDataService solrSearchService;

    private DataProvider<ModuleMetaData,ModuleSearchFilter> dataProvider;
    private ConfigurableFilterDataProvider<ModuleMetaData,Void, ModuleSearchFilter> filteredDataProvider;

    private ModuleSearchFilter searchFilter;

    private long resultSize = 0;
    private long queryTime = 0;

    /**
     * Constructor
     */
    public ModuleFilteringGrid(ModuleMetaDataService solrSearchService,
                               ModuleSearchFilter searchFilter)
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
            Optional<ModuleSearchFilter> filter = query.getFilter();

            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            ModuleMetadataSearchResults results;

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
            Optional<ModuleSearchFilter> filter = query.getFilter();

            ModuleMetadataSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(filter.get(), 0, 0);
            }
            else
            {
                results = this.getResults(null, 0, 0);
            }

            this.resultSize = results.getTotalNumberOfResults();
            this.queryTime = results.getQueryResponseTime();


            return (int) results.getTotalNumberOfResults();
        });

        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(this.searchFilter);

        this.setDataProvider(filteredDataProvider);
    }

    private ModuleMetadataSearchResults getResults(ModuleSearchFilter filter, int offset, int limit)
    {
        List<String> moduleNames = null;

        if(filter.getModuleNameFilter() != null && !filter.getModuleNameFilter().isEmpty())
        {
            moduleNames = new ArrayList<>();

            moduleNames.add("*" + ClientUtils.escapeQueryChars(filter.getModuleNameFilter()) + "*");
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        ModuleMetadataSearchResults results;

        if(!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            List<String> accessibleModules = SecurityUtils.getAccessibleModules(authentication)
                .stream()
                .collect(Collectors.toList());;

             results =  this.solrSearchService.find(accessibleModules, offset, limit);
        }
        else
        {
            results =  this.solrSearchService.find(moduleNames, offset, limit);
        }

        return results;
    }
}
