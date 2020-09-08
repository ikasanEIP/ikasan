package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RoleFilter implements Filter<Role, Optional<RoleFilter>>
{
    private Collection<Role> roles;
    private String nameFilter = null;
    private String descriptionFilter = null;

    public void setItems(Collection<Role> roles)
    {
        this.roles = roles;
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
    public Stream<Role> getFilterStream()
    {
        return roles
            .stream()
            .filter(role ->
            {
                if(this.getNameFilter() == null || this.getNameFilter().isEmpty())
                {
                    return true;
                }
                else if(role.getName() == null)
                {
                    return false;
                }
                else
                {
                    return role.getName().toLowerCase().startsWith(getNameFilter().toLowerCase());
                }
            })
            .filter(role ->
            {
                if(this.descriptionFilter == null || this.descriptionFilter.isEmpty())
                {
                    return true;
                }
                else if(role.getDescription() == null)
                {
                    return false;
                }
                else
                {
                    return role.getDescription().toLowerCase().contains(getDescriptionFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("name"))
        {
            comparator =  Comparator.comparing(Role::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("description"))
        {
            comparator = Comparator.comparing(Role::getDescription, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        return comparator;
    }

    @Override
    public Collection<Role> getItems() {
        return this.roles;
    }
}
