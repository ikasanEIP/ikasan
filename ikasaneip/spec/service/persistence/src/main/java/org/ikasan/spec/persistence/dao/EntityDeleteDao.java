package org.ikasan.spec.persistence.dao;

public interface EntityDeleteDao
{
    /**
     * Removes an entity of the specified type with the given identifier.
     *
     * @param type the type of the entity to be removed
     * @param id the unique identifier of the entity to be removed
     */
    void removeById(String type, String id);

    /**
     * Method to remove expired records from the persistence.
     */
    void removeExpired();
}
