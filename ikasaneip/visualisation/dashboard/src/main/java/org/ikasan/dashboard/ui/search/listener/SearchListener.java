package org.ikasan.dashboard.ui.search.listener;

import java.util.List;

public interface SearchListener {

    /**
     * Component search listener.
     *
     * @param searchTerm
     * @param entityTypes
     * @param negateQuery
     * @param startDate
     * @param endDate
     */
    public void search(String searchTerm, List<String> entityTypes, boolean negateQuery, long startDate, long endDate);
}
