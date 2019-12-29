package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ModuleFilter implements Filter<ModuleMetaData, Optional<ModuleFilter>>
{
    private Collection<ModuleMetaData> modules;
    private String moduleNameFilter = null;

    public void setItems(Collection<ModuleMetaData> groups)
    {
        this.modules = groups;
    }

    public String getModuleNameFilter()
    {
        return moduleNameFilter;
    }

    public void setModuleNameFilter(String moduleNameFilter)
    {
        this.moduleNameFilter = moduleNameFilter;
    }

    @Override
    public Stream<ModuleMetaData> getFilterStream()
    {
        return modules
            .stream()
            .filter(group ->
            {
                if(this.getModuleNameFilter() == null || this.getModuleNameFilter().isEmpty())
                {
                    return true;
                }
                else if(group.getName() == null)
                {
                    return false;
                }
                else
                {
                    return group.getName().toLowerCase().contains(getModuleNameFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator = Comparator.comparing(ModuleMetaData::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }
}
