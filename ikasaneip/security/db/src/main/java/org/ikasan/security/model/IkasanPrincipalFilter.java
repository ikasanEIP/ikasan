package org.ikasan.security.model;

public class IkasanPrincipalFilter
{
    private String nameFilter = null;
    private String typeFilter = null;
    private String descriptionFilter = null;
    private String sortOrder;
    private String sortColumn;

    /**
     * Retrieves the current sort order used for filtering.
     *
     * @return The current sort order.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sort order for filtering.
     *
     * @param sortOrder the sort order to be set
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Retrieves the column by which the data should be sorted.
     *
     * @return The column name used for sorting.
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * Sets the column to be used for sorting in the IkasanPrincipalFilter.
     *
     * @param sortColumn the column to be used for sorting
     */
    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    /**
     * Retrieves the name filter that is set in the IkasanPrincipalFilter object.
     *
     * @return the name filter string used for filtering IkasanPrincipals
     */
    public String getNameFilter()
    {
        return nameFilter;
    }

    /**
     * Sets the name filter used for filtering data based on name.
     *
     * @param nameFilter the name filter to be applied
     */
    public void setNameFilter(String nameFilter)
    {
        this.nameFilter = nameFilter;
    }

    /**
     * Get the type filter criteria for filtering IkasanPrincipals.
     *
     * @return The type filter criteria used for filtering IkasanPrincipals.
     */
    public String getTypeFilter()
    {
        return typeFilter;
    }

    /**
     * Sets the type filter for filtering objects of a specific type.
     *
     * @param typeFilter the type filter to be set
     */
    public void setTypeFilter(String typeFilter)
    {
        this.typeFilter = typeFilter;
    }

    /**
     * Get the description filter for filtering IkasanPrincipals based on description criteria.
     *
     * @return the description filter used for filtering IkasanPrincipals
     */
    public String getDescriptionFilter()
    {
        return descriptionFilter;
    }

    /**
     * Sets the description filter to be used for filtering IkasanPrincipals based on description criteria.
     *
     * @param descriptionFilter the description filter to be applied
     */
    public void setDescriptionFilter(String descriptionFilter)
    {
        this.descriptionFilter = descriptionFilter;
    }
}
