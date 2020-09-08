package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.data.provider.QuerySortOrder;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public interface Filter<ENTITY>
{
    public void setItems(Collection<ENTITY> users);

    public Collection<ENTITY> getItems();

    public Stream<ENTITY> getFilterStream();

    public Comparator getSortComparator(List<QuerySortOrder> querySortOrders);
}
