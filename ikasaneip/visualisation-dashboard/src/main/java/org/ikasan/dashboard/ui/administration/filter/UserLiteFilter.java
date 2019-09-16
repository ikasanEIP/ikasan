package org.ikasan.dashboard.ui.administration.filter;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.ikasan.dashboard.ui.general.component.Filter;
import org.ikasan.security.model.User;
import org.ikasan.security.model.UserLite;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UserLiteFilter implements Filter<UserLite, Optional<UserLiteFilter>>
{
    private Collection<UserLite> users;
    private String usernameFilter = null;
    private String nameFilter = null;
    private String lastNameFilter = null;
    private String emailFilter = null;
    private String departmentFilter;

    public void setItems(Collection<UserLite> users)
    {
        this.users = users;
    }

    public String getNameFilter()
    {
        return nameFilter;
    }

    public String getLastNameFilter()
    {

        return lastNameFilter;

    }

    public void setNameFilter(String nameFilter)
    {

        this.nameFilter = nameFilter;

    }

    public void setLastNameFilter(String lastNameFilter)
    {

        this.lastNameFilter = lastNameFilter;

    }

    public String getUsernameFilter()
    {
        return usernameFilter;
    }

    public void setUsernameFilter(String usernameFilter)
    {
        this.usernameFilter = usernameFilter;
    }

    public String getEmailFilter()
    {
        return emailFilter;
    }

    public void setEmailFilter(String emailFilter)
    {
        this.emailFilter = emailFilter;
    }

    public String getDepartmentFilter()
    {
        return departmentFilter;
    }

    public void setDepartmentFilter(String departmentFilter)
    {
        this.departmentFilter = departmentFilter;
    }

    @Override
    public Stream<UserLite> getFilterStream()
    {
        return users
            .stream()
            .filter(user ->
            {
                if(getUsernameFilter() == null || getUsernameFilter().isEmpty())
                {
                    return true;
                }
                else if(user.getUsername() == null)
                {
                    return false;
                }
                else
                {
                    return user.getUsername().toLowerCase().startsWith(getUsernameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(getNameFilter() == null || getNameFilter().isEmpty())
                {
                    return true;
                }
                else if(user.getFirstName() == null)
                {
                    return false;
                }
                else
                {
                    return user.getFirstName().toLowerCase().startsWith(getNameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(getLastNameFilter() == null || getLastNameFilter().isEmpty())
                {
                    return true;
                }
                else if(user.getSurname() == null)
                {
                    return false;
                }
                else
                {
                    return user.getSurname().toLowerCase().startsWith(getLastNameFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(getEmailFilter() == null || getEmailFilter().isEmpty())
                {
                    return true;
                }
                else if(user.getEmail() == null)
                {
                    return false;
                }
                else
                {
                    return user.getEmail().toLowerCase().startsWith(getEmailFilter().toLowerCase());
                }
            })
            .filter(user ->
            {
                if(getDepartmentFilter() == null || getDepartmentFilter().isEmpty())
                {
                    return true;
                }
                else if(user.getDepartment() == null)
                {
                    return false;
                }
                else
                {
                    return user.getDepartment().toLowerCase().startsWith(getDepartmentFilter().toLowerCase());
                }
            });
    }

    @Override
    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders)
    {
        Comparator comparator = null;

        if(querySortOrders.get(0).getSorted().equals("username"))
        {
            comparator = Comparator.comparing(UserLite::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("firstname"))
        {
            comparator = Comparator.comparing(UserLite::getFirstName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("surname"))
        {
            comparator = Comparator.comparing(UserLite::getSurname, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("email"))
        {
            comparator = Comparator.comparing(UserLite::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("department"))
        {
            comparator = Comparator.comparing(UserLite::getDepartment, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        else if(querySortOrders.get(0).getSorted().equals("lastaccess"))
        {
            comparator = Comparator.comparing(UserLite::getPreviousAccessTimestamp, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        return comparator;
    }
}
