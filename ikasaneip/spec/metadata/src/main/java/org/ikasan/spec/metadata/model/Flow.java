package org.ikasan.spec.metadata.model;

/**
 * Interface for Flowimpl implementations.
 * Represents a flow in a business stream containing flow metadata including position and correlation information.
 *
 * @author Ikasan Development Team
 */
public interface Flow {

    /**
     * Get the ID of the flow.
     *
     * @return the ID
     */
    String getId();

    /**
     * Set the ID of the flow.
     *
     * @param id the ID to set
     */
    void setId(String id);

    /**
     * Get the module name.
     *
     * @return the module name
     */
    String getModuleName();

    /**
     * Set the module name.
     *
     * @param moduleName the module name to set
     */
    void setModuleName(String moduleName);

    /**
     * Get the flow name.
     *
     * @return the flow name
     */
    String getFlowName();

    /**
     * Set the flow name.
     *
     * @param flowName the flow name to set
     */
    void setFlowName(String flowName);

    /**
     * Get the X coordinate of the flow.
     *
     * @return the X coordinate
     */
    Integer getX();

    /**
     * Set the X coordinate of the flow.
     *
     * @param x the X coordinate to set
     */
    void setX(Integer x);

    /**
     * Get the Y coordinate of the flow.
     *
     * @return the Y coordinate
     */
    Integer getY();

    /**
     * Set the Y coordinate of the flow.
     *
     * @param y the Y coordinate to set
     */
    void setY(Integer y);

    /**
     * Get the correlator for the flow.
     *
     * @return the correlator
     */
    Correlator getCorrelator();

    /**
     * Set the correlator for the flow.
     *
     * @param correlator the correlator to set
     */
    void setCorrelator(Correlator correlator);
}
