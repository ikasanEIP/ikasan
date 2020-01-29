package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.RoleModule;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RoleModuleFilter implements Filter<RoleModule, Optional<RoleModuleFilter>>
{
    private Collection<RoleModule> groups;
    private String moduleNameFilter = null;

    public void setItems(Collection<RoleModule> groups)
    {
        this.groups = groups;
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
    public Stream<RoleModule> getFilterStream()
    {
        return groups
            .stream()
            .filter(group ->
            {
                if(this.getModuleNameFilter() == null || this.getModuleNameFilter().isEmpty())
                {
                    return true;
                }
                else if(group.getModuleName() == null)
                {
                    return false;
                }
                else
                {
                    return group.getModuleName().toLowerCase().contains(getModuleNameFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator = Comparator.comparing(RoleModule::getModuleName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }
}
