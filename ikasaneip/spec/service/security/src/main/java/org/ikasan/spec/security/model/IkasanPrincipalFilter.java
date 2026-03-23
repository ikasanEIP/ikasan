package org.ikasan.spec.security.model;

/**
 * Filter criteria interface for querying and sorting {@link IkasanPrincipal} instances.
 *
 * <p>Provides filtering capabilities based on principal attributes such as name, type, and description,
 * along with sorting options to control the order of query results. All filter values use partial matching
 * semantics where applicable.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface IkasanPrincipalFilter
{
    String getSortOrder();

    void setSortOrder(String sortOrder);

    String getSortColumn();

    void setSortColumn(String sortColumn);

    String getNameFilter();

    void setNameFilter(String nameFilter);

    String getTypeFilter();

    void setTypeFilter(String typeFilter);

    String getDescriptionFilter();

    void setDescriptionFilter(String descriptionFilter);
}
