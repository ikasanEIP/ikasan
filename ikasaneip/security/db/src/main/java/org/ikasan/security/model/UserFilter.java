package org.ikasan.security.model;

import java.util.Collection;

public class UserFilter
{
    private String usernameFilter = null;
    private String nameFilter = null;
    private String lastNameFilter = null;
    private String emailFilter = null;
    private String departmentFilter = null;
    private String sortOrder;
    private String sortColumn;

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

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }
}
