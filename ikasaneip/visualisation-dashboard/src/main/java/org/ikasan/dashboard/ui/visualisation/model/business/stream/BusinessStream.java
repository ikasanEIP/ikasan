package org.ikasan.dashboard.ui.visualisation.model.business.stream;


import org.ikasan.vaadin.visjs.network.Edge;

import java.util.List;

public class BusinessStream
{
    List<Flow> flows;
    List<IntegratedSystem> integratedSystems;
    List<Edge> edges;
    List<Destination> destinations;

    public BusinessStream(List<Flow> flows, List<IntegratedSystem> integratedSystems, List<Edge> edges,
                          List<Destination> destinations)
    {
        this.flows = flows;
        this.integratedSystems = integratedSystems;
        this.edges = edges;
        this.destinations = destinations;
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
}
