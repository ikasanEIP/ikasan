package org.ikasan.dashboard.ui.visualisation.model.business.stream;


import org.ikasan.vaadin.visjs.network.Edge;

import java.util.List;

public class BusinessStream
{
    List<Flow> flows;
    List<IntegratedSystem> integratedSystems;
    List<Edge> edges;
    List<Destination> destinations;
    List<Boundary> boundaries;

    public BusinessStream(List<Flow> flows, List<IntegratedSystem> integratedSystems, List<Edge> edges,
                          List<Destination> destinations, List<Boundary> boundaries)
    {
        this.flows = flows;
        this.integratedSystems = integratedSystems;
        this.edges = edges;
        this.destinations = destinations;
        this.boundaries = boundaries;
    }

    public List<Flow> getFlows()
    {
        return flows;
    }

    public List<IntegratedSystem> getIntegratedSystems()
    {
        return integratedSystems;
    }

    public List<Edge> getEdges()
    {
        return edges;
    }

    public List<Destination> getDestinations()
    {
        return destinations;
    }

    public List<Boundary> getBoundaries() {
        return boundaries;
    }
}
