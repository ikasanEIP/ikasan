package org.ikasan.spec.entity;

import java.util.List;

/**
 * Interface for ESB Entity operations.
 * Created by Ikasan Development Team on 02/05/2026.
 */
public interface EsbEntityService<T>
{
    /**
     * Save an individual ErrorOccurrence entity
     *
     * @param entity
     */
    void save(T entity);

    /**
     * Save a list of ErrorOccurrence entities
     *
     * @param entities
     */
    void save(List<T> entities);
}
