package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilteringGrid<T> extends Grid<T>
{
    private Logger logger = LoggerFactory.getLogger(FilteringGrid.class);

    private Filter filter;
    private DataProvider<T,Filter> dataProvider;
    private ConfigurableFilterDataProvider<T,Void, Filter> filteredDataProvider;

    /**
     * Constructors
     *
     * @param filter
     */
    public FilteringGrid(Filter filter)
    {
        this.filter = filter;
        if(this.filter == null)
        {
            throw new IllegalArgumentException("filter cannot be null!");
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

    @Override
    public void setItems(Collection<T> items)
    {
        this.filter.setItems(items);

        dataProvider = DataProvider.fromFilteringCallbacks(query ->
        {
            Optional<Filter> filter = query.getFilter();
            Stream<T> stream;

            if(filter.isPresent())
            {
                if(query.getSortOrders() != null && query.getSortOrders().size() > 0)
                {
                    if(query.getSortOrders().get(0).getDirection().equals(SortDirection.ASCENDING))
                    {
                        stream = this.filter.getFilterStream()
                            .skip(query.getOffset())
                            .sorted(filter.get().getSortComparator(query.getSortOrders()))
                            .limit(query.getLimit());
                    }
                    else
                    {
                        stream = this.filter.getFilterStream()
                            .skip(query.getOffset())
                            .sorted(filter.get().getSortComparator(query.getSortOrders()).reversed())
                            .limit(query.getLimit());
                    }
                }
                else
                {
                    stream = this.filter.getFilterStream()
                        .skip(query.getOffset())
                        .limit(query.getLimit());
                }
            }
            else
            {
                if(query.getSortOrders() != null && query.getSortOrders().size() > 0)
                {
                    stream = items.stream()
                        .sorted(filter.get().getSortComparator(query.getSortOrders()))
                        .limit(query.getLimit());
                }
                else
                {
                    stream = items.stream()
                        .limit(query.getLimit());
                }
            }

            return stream;
        }, query ->
        {

            Optional<Filter> filter = query.getFilter();

            if(filter.isPresent())
            {
                return ((List)this.filter.getFilterStream()
                    .collect(Collectors.toList())).size();
            }
            else
            {
                return items.size();
            }

        });



        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(filter);

        this.setDataProvider(filteredDataProvider);
    }

    public DataProvider getDataProvider() {
        return this.filteredDataProvider;
    }

}
