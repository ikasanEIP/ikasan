package org.ikasan.spec.search;

import java.util.List;

public interface SearchResults<DATA> {

    List<DATA> getResultList();

    long getTotalNumberOfResults();

    long getQueryResponseTime();
}
