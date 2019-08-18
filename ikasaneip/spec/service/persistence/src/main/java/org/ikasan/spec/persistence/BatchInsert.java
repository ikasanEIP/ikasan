package org.ikasan.spec.persistence;

import java.util.List;

public interface BatchInsert<T>
{
    /**
     * Insert a list of entities into the underlying data store.
     *
     * @param entities
     */
    public void insert(List<T> entities);
}
