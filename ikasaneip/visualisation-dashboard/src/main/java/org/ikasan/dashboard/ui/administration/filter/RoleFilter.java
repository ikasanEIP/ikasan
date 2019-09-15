package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.IkasanPrincipalLite;
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

    @Override
    public Stream<Role> getFilterStream()
    {
        return roles
            .stream()
            .filter(group ->
            {
                if(this.getNameFilter() == null || this.getNameFilter().isEmpty() || group.getName() == null)
                {
                    return true;
                }
                else
                {
                    return group.getName().toLowerCase().startsWith(getNameFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator;

        if(querySortOrders.get(0).getSorted().equals("username"))
        {
            comparator = Comparator.comparing(User::getUsername);
        }
        else
        {
            comparator = Comparator.naturalOrder();
        }

        return comparator;
    }
}
