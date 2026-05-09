package org.ikasan.spec.entity;

import java.util.List;

/**
 * Interface for ESB Entity DAO operations.
 * Created by Ikasan Development Team on 02/05/2026.
 */
public interface EsbEntityDao<T>
{
    /**
     * Save an individual ErrorOccurrence entity
     *
     * @param event
     */
    void save(T event);

    /**
     * Save a list of ErrorOccurrence entities.
     * @param events
     */
    void save(List<T> events);
}
