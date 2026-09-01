package org.ikasan.spec.metadata.model;

/**
 * Interface for CorrelatorImpl implementations.
 * Represents a correlator in a business stream flow used to define correlation logic.
 *
 * @author Ikasan Development Team
 */
public interface Correlator {

    /**
     * Get the type of the correlator.
     *
     * @return the type
     */
    String getType();

    /**
     * Set the type of the correlator.
     *
     * @param type the type to set
     */
    void setType(String type);

    /**
     * Get the query for the correlator.
     *
     * @return the query
     */
    String getQuery();

    /**
     * Set the query for the correlator.
     *
     * @param query the query to set
     */
    void setQuery(String query);
}
