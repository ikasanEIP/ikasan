package org.ikasan.dashboard.ui.search.component.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.security.model.IkasanPrincipalLite;

import java.util.*;

public class SearchFilter
{
    private List<String> moduleNamesFilter = new ArrayList<>();
    private List<String> flowNamesFilter = new ArrayList<>();
    private String componentNameFilter = null;
    private String eventIdFilter = null;

    public List<String> getModuleNamesFilter()
    {
        return moduleNamesFilter;
    }

    public void setModuleNamesFilter(List<String> moduleNameFilter)
    {
        this.moduleNamesFilter = moduleNameFilter;
    }

    public List<String> getFlowNamesFilter()
    {
        return flowNamesFilter;
    }

    public void setFlowNamesFilter(List<String> flowNameFilter)
    {
        this.flowNamesFilter = flowNameFilter;
    }

    public String getModuleNameFilter()
    {
        return moduleNamesFilter.get(0);
    }

    public void setModuleNameFilter(String moduleNameFilter)
    {
        this.moduleNamesFilter = new ArrayList<>();
        this.moduleNamesFilter.add(moduleNameFilter);
    }

    public String getFlowNameFilter()
    {
        return flowNamesFilter.get(0);
    }

    public void setFlowNameFilter(String flowNameFilter)
    {
        this.flowNamesFilter = new ArrayList<>();
        this.flowNamesFilter.add(flowNameFilter);
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
