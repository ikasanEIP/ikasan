package org.ikasan.dashboard.ui.visualisation.component.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.spec.metadata.BusinessStreamMetaData;

import java.util.Comparator;
import java.util.List;

public class BusinessStreamSearchFilter
{
    private String businessStreamNameFilter = null;

    public String getBusinessStreamNameFilter()
    {
        return businessStreamNameFilter;
    }

    public void setBusinessStreamNameFilter(String businessStreamNameFilter)
    {
        this.businessStreamNameFilter = businessStreamNameFilter;
    }

    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator = Comparator.comparing(BusinessStreamMetaData::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }
}
