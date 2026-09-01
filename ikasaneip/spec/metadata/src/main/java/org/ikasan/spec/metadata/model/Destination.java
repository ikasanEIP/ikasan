package org.ikasan.spec.metadata.model;

/**
 * Interface for DestinationImpl implementations.
 * Represents a destination in a business stream where data is sent.
 *
 * @author Ikasan Development Team
 */
public interface Destination {

    /**
     * Get the ID of the destination.
     *
     * @return the ID
     */
    String getId();

    /**
     * Set the ID of the destination.
     *
     * @param id the ID to set
     */
    void setId(String id);

    /**
     * Get the name of the destination.
     *
     * @return the name
     */
    String getName();

    /**
     * Set the name of the destination.
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * Get the X coordinate of the destination.
     *
     * @return the X coordinate
     */
    Integer getX();

    /**
     * Set the X coordinate of the destination.
     *
     * @param x the X coordinate to set
     */
    void setX(Integer x);

    /**
     * Get the Y coordinate of the destination.
     *
     * @return the Y coordinate
     */
    Integer getY();

    /**
     * Set the Y coordinate of the destination.
     *
     * @param y the Y coordinate to set
     */
    void setY(Integer y);
}
