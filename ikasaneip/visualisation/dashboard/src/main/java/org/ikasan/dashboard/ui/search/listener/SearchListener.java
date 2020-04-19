package org.ikasan.dashboard.ui.search.listener;

public interface SearchListener {

    /**
     * Component search listener.
     *
     * @param searchTerm
     * @param startDate
     * @param endDate
     */
    public void search(String searchTerm, long startDate, long endDate);
}
