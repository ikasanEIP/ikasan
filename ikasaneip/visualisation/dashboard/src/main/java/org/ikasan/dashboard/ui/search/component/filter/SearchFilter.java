package org.ikasan.dashboard.ui.search.component.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.IkasanPrincipalLite;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SearchFilter
{
    private String moduleNameFilter = null;
    private String flowNameFilter = null;
    private String componentNameFilter = null;
    private String eventIdFilter = null;

    public String getModuleNameFilter()
    {
        return moduleNameFilter;
    }

    public void setModuleNameFilter(String moduleNameFilter)
    {
        this.moduleNameFilter = moduleNameFilter;
    }

    public String getFlowNameFilter()
    {
        return flowNameFilter;
    }

    public void setFlowNameFilter(String flowNameFilter)
    {
        this.flowNameFilter = flowNameFilter;
    }

    public String getComponentNameFilter()
    {
        return componentNameFilter;
    }

    public void setComponentNameFilter(String componentNameFilter)
    {
        this.componentNameFilter = componentNameFilter;
    }

    public String getEventIdFilter()
    {
        return eventIdFilter;
    }

    public void setEventIdFilter(String eventIdFilter)
    {
        this.eventIdFilter = eventIdFilter;
    }

    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator = Comparator.comparing(IkasanPrincipalLite::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("type"))
        {
            comparator = Comparator.comparing(IkasanPrincipalLite::getType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("description"))
        {
            comparator = Comparator.comparing(IkasanPrincipalLite::getDescription, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }
}
