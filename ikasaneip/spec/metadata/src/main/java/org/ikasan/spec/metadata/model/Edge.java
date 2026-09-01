package org.ikasan.spec.metadata.model;

/**
 * Interface for Edgeimpl implementations.
 * Represents an edge connecting two nodes in a business stream, defining the flow of data.
 *
 * @author Ikasan Development Team
 */
public interface Edge {

    /**
     * Get the source node ID.
     *
     * @return the source node ID
     */
    String getFrom();

    /**
     * Set the source node ID.
     *
     * @param from the source node ID to set
     */
    void setFrom(String from);

    /**
     * Get the target node ID.
     *
     * @return the target node ID
     */
    String getTo();

    /**
     * Set the target node ID.
     *
     * @param to the target node ID to set
     */
    void setTo(String to);
}
