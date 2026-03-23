package org.ikasan.spec.security.model;

/**
 * Filter criteria interface for querying and sorting {@link User} instances.
 *
 * <p>Provides filtering capabilities based on user attributes such as username, first name, last name,
 * email, and department, along with sorting options to control the order of query results. All filter
 * values use partial matching semantics where applicable.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface UserFilter
{
    String getNameFilter();

    String getLastNameFilter();

    void setNameFilter(String nameFilter);

    void setLastNameFilter(String lastNameFilter);

    String getUsernameFilter();

    void setUsernameFilter(String usernameFilter);

    String getEmailFilter();

    void setEmailFilter(String emailFilter);

    String getDepartmentFilter();

    void setDepartmentFilter(String departmentFilter);

    String getSortOrder();

    void setSortOrder(String sortOrder);

    String getSortColumn();

    void setSortColumn(String sortColumn);
}
