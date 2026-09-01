package org.ikasan.spec.metadata.model;

/**
 * Interface for IntegratedSystem implementations.
 * Represents an integrated system in a business stream - external systems that are integrated as part of the business stream.
 *
 * @author Ikasan Development Team
 */
public interface IntegratedSystem {

    /**
     * Get the ID of the integrated system.
     *
     * @return the ID
     */
    String getId();

    /**
     * Set the ID of the integrated system.
     *
     * @param id the ID to set
     */
    void setId(String id);

    /**
     * Get the name of the integrated system.
     *
     * @return the name
     */
    String getName();

    /**
     * Set the name of the integrated system.
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * Get the X coordinate of the integrated system.
     *
     * @return the X coordinate
     */
    Integer getX();

    /**
     * Set the X coordinate of the integrated system.
     *
     * @param x the X coordinate to set
     */
    void setX(Integer x);

    /**
     * Get the Y coordinate of the integrated system.
     *
     * @return the Y coordinate
     */
    Integer getY();

    /**
     * Set the Y coordinate of the integrated system.
     *
     * @param y the Y coordinate to set
     */
    void setY(Integer y);

    /**
     * Get the image associated with the integrated system.
     *
     * @return the image
     */
    String getImage();

    /**
     * Set the image associated with the integrated system.
     *
     * @param image the image to set
     */
    void setImage(String image);

    /**
     * Get the size of the integrated system icon.
     *
     * @return the size
     */
    Integer getSize();

    /**
     * Set the size of the integrated system icon.
     *
     * @param size the size to set
     */
    void setSize(Integer size);
}
