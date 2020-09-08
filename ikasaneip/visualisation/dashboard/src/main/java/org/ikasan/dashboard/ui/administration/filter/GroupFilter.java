package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.IkasanPrincipalLite;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GroupFilter implements Filter<IkasanPrincipalLite, Optional<GroupFilter>>
{
    private Collection<IkasanPrincipalLite> principals;
    private String nameFilter = null;
    private String typeFilter = null;
    private String descriptionFilter = null;

    public void setItems(Collection<IkasanPrincipalLite> groups)
    {
        this.principals = groups;
    }

    public String getNameFilter()
    {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter)
    {
        this.nameFilter = nameFilter;
    }

    public String getTypeFilter()
    {
        return typeFilter;
    }

    public void setTypeFilter(String typeFilter)
    {
        this.typeFilter = typeFilter;
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
    public Stream<IkasanPrincipalLite> getFilterStream()
    {
        return principals
            .stream()
            .filter(group ->
            {
                if(this.getNameFilter() == null || this.getNameFilter().isEmpty())
                {
                    return true;
                }
                else if(group.getName() == null)
                {
                    return false;
                }
                else
                {
                    return group.getName().toLowerCase().contains(getNameFilter().toLowerCase());
                }
            })
            .filter(group ->
            {
                if(getTypeFilter() == null || getTypeFilter().isEmpty())
                {
                    return true;
                }
                else if(group.getType() == null)
                {
                    return false;
                }
                else
                {
                    return group.getType().toLowerCase().contains(getTypeFilter().toLowerCase());
                }
            })
            .filter(group ->
            {
                if(getDescriptionFilter() == null || getDescriptionFilter().isEmpty())
                {
                    return true;
                }
                else if(group.getDescription() == null)
                {
                    return false;
                }
                else
                {
                    return group.getDescription().toLowerCase().contains(getDescriptionFilter().toLowerCase());
                }
            });
    }

    @Override
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

    @Override
    public Collection<IkasanPrincipalLite> getItems() {
        return this.principals;
    }
}
