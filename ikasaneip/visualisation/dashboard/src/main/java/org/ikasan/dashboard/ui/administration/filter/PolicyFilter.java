package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PolicyFilter implements Filter<Policy, Optional<PolicyFilter>>
{
    private Collection<Policy> policies;
    private String nameFilter = null;
    private String descriptionFilter = null;

    public void setItems(Collection<Policy> policies)
    {
        this.policies = policies;
    }

    public String getNameFilter()
    {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter)
    {
        this.nameFilter = nameFilter;
    }

    public String getDescriptionFilter()
    {
        return descriptionFilter;
    }

    public void setDescriptionFilter(String descriptionFilter)
    {
        this.descriptionFilter = descriptionFilter;
    }

    @Override
    public Stream<Policy> getFilterStream()
    {
        return policies
            .stream()
            .filter(policy ->
            {
                if(this.getNameFilter() == null || this.getNameFilter().isEmpty())
                {
                    return true;
                }
                else if(policy.getName() == null)
                {
                    return false;
                }
                else
                {
                    return policy.getName().toLowerCase().startsWith(getNameFilter().toLowerCase());
                }
            })
            .filter(policy ->
            {
                if(this.descriptionFilter == null || this.descriptionFilter.isEmpty())
                {
                    return true;
                }
                else if(policy.getDescription() == null)
                {
                    return false;
                }
                else
                {
                    return policy.getDescription().toLowerCase().contains(getDescriptionFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator =  Comparator.comparing(Policy::getName, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("description"))
        {
            comparator = Comparator.comparing(Policy::getDescription, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }

    @Override
    public Collection<Policy> getItems() {
        return this.policies;
    }
}
