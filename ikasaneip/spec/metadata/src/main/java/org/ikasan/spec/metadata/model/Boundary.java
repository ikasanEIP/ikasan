package org.ikasan.spec.metadata.model;

/**
 * Interface for BoundaryImpl implementations.
 * Represents a boundary in a business stream visualization used to group related components visually.
 *
 * @author Ikasan Development Team
 */
public interface Boundary {

    /**
     * Get the X coordinate of the boundary.
     *
     * @return the X coordinate
     */
    int getX();

    /**
     * Set the X coordinate of the boundary.
     *
     * @param x the X coordinate to set
     */
    void setX(int x);

    /**
     * Get the Y coordinate of the boundary.
     *
     * @return the Y coordinate
     */
    int getY();

    /**
     * Set the Y coordinate of the boundary.
     *
     * @param y the Y coordinate to set
     */
    void setY(int y);

    /**
     * Get the width of the boundary.
     *
     * @return the width
     */
    int getW();

    /**
     * Set the width of the boundary.
     *
     * @param w the width to set
     */
    void setW(int w);

    /**
     * Get the height of the boundary.
     *
     * @return the height
     */
    int getH();

    /**
     * Set the height of the boundary.
     *
     * @param h the height to set
     */
    void setH(int h);

    /**
     * Get the colour of the boundary.
     *
     * @return the colour
     */
    String getColour();

    /**
     * Set the colour of the boundary.
     *
     * @param colour the colour to set
     */
    void setColour(String colour);

    /**
     * Get the label of the boundary.
     *
     * @return the label
     */
    String getLabel();

    /**
     * Set the label of the boundary.
     *
     * @param label the label to set
     */
    void setLabel(String label);
}
