package org.ikasan.spec.persistence.service;

public interface EntityDeleteService
{
    /**
     * Removes an entity of the specified type with the given identifier.
     *
     * @param type the type of the entity to be removed
     * @param id the unique identifier of the entity to be removed
     */
    void removeById(String type, String id);
}
