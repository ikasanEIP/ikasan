package org.ikasan.business.stream.metadata.model;

import java.util.ArrayList;
import java.util.List;

public class BusinessStream
{
    private List<Flow> flows = new ArrayList<>();
    private List<Destination> destinations = new ArrayList<>();
    private List<IntegratedSystem> integratedSystems = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public List<Flow> getFlows()
    {
        return flows;
    }

    public void setFlows(List<Flow> flows)
    {
        this.flows = flows;
    }

    public List<Destination> getDestinations()
    {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations)
    {
        this.destinations = destinations;
    }

    public List<IntegratedSystem> getIntegratedSystems()
    {
        return integratedSystems;
    }

    public void setIntegratedSystems(List<IntegratedSystem> integratedSystems)
    {
        this.integratedSystems = integratedSystems;
    }

    public List<Edge> getEdges()
    {
        return edges;
    }

    public void setEdges(List<Edge> edges)
    {
        this.edges = edges;
    }
}
