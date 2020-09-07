package org.ikasan.dashboard.ui.search.component.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.security.model.IkasanPrincipalLite;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchFilter
{
    private List<String> moduleNamesFilterList = new ArrayList<>();
    private List<String> flowNamesFilterList = new ArrayList<>();
    private String moduleNamesFilter = null;
    private String flowNamesFilter = null;
    private String componentNameFilter = null;
    private String eventIdFilter = null;

    public List<String> getModuleNamesFilterList()
    {
        return moduleNamesFilterList;
    }

    public void setModuleNamesFilterList(List<String> moduleNameFilter)
    {
        this.moduleNamesFilterList = moduleNameFilter;
    }

    public List<String> getFlowNamesFilterList()
    {
        return flowNamesFilterList;
    }

    public void setFlowNamesFilterList(List<String> flowNameFilter)
    {
        this.flowNamesFilterList = flowNameFilter;
    }

    public String getModuleNameFilter()
    {
        return this.moduleNamesFilter;
    }

    public boolean isValidModuleNameFilter() {
        if(this.moduleNamesFilter == null) {
            return false;
        }
        else if(moduleNamesFilterList.isEmpty() || this.moduleNamesFilter.isEmpty()) {
            return true;
        }

        AtomicBoolean returnVal = new AtomicBoolean(false);

        this.moduleNamesFilterList.forEach(moduleNamesFilter -> {
            if(moduleNamesFilter.toLowerCase().contains(this.moduleNamesFilter.toLowerCase())){
                returnVal.set(true);
            }
        });

        return returnVal.get();
    }

    public void setModuleNameFilter(String moduleNameFilter)
    {
        this.moduleNamesFilter = moduleNameFilter;
    }

    public String getFlowNameFilter()
    {
        return this.flowNamesFilter;
    }

    public boolean isValidFlowNameFilter() {
        if(this.flowNamesFilter == null) {
            return false;
        }
        else if(this.flowNamesFilterList.isEmpty() || this.flowNamesFilter.isEmpty()) {
            return true;
        }

        AtomicBoolean returnVal = new AtomicBoolean(false);

        this.flowNamesFilterList.forEach(flowNamesFilter -> {
            if(flowNamesFilter.toLowerCase().contains(this.flowNamesFilter.toLowerCase())){
                returnVal.set(true);
            }
        });

        return returnVal.get();
    }

    public void setFlowNameFilter(String flowNameFilter)
    {
        this.flowNamesFilter = flowNameFilter;
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
