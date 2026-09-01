package org.ikasan.spec.metadata.model;

import java.util.List;

/**
 * Interface for BusinessStreamImpl implementations.
 * Represents a complete business stream containing flows, destinations, systems, and their connections.
 *
 * @author Ikasan Development Team
 */
public interface BusinessStream {

    /**
     * Get the list of flows in the business stream.
     *
     * @return the flows
     */
    List<Flow> getFlows();

    /**
     * Set the list of flows in the business stream.
     *
     * @param flows the flows to set
     */
    void setFlows(List<Flow> flows);

    /**
     * Get the list of destinations in the business stream.
     *
     * @return the destinations
     */
    List<Destination> getDestinations();

    /**
     * Set the list of destinations in the business stream.
     *
     * @param destinations the destinations to set
     */
    void setDestinations(List<Destination> destinations);

    /**
     * Get the list of integrated systems in the business stream.
     *
     * @return the integrated systems
     */
    List<IntegratedSystem> getIntegratedSystems();

    /**
     * Set the list of integrated systems in the business stream.
     *
     * @param integratedSystems the integrated systems to set
     */
    void setIntegratedSystems(List<IntegratedSystem> integratedSystems);

    /**
     * Get the list of edges connecting nodes in the business stream.
     *
     * @return the edges
     */
    List<Edge> getEdges();

    /**
     * Set the list of edges connecting nodes in the business stream.
     *
     * @param edges the edges to set
     */
    void setEdges(List<Edge> edges);

    /**
     * Get the list of boundaries grouping components in the business stream.
     *
     * @return the boundaries
     */
    List<Boundary> getBoundaries();

    /**
     * Set the list of boundaries grouping components in the business stream.
     *
     * @param boundaries the boundaries to set
     */
    void setBoundaries(List<Boundary> boundaries);
}
